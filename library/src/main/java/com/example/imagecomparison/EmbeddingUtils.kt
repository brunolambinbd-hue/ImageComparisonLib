package com.example.imagecomparison

import android.util.Log
import com.google.mediapipe.tasks.components.containers.Embedding
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * A data class to hold embedding values when a full MediaPipe [Embedding] object cannot be reconstructed.
 * This serves as an internal, serializable representation.
 *
 * @property floatValues The float array for a standard embedding.
 * @property quantizedValues The byte array for a quantized embedding.
 * @property isQuantized A flag indicating which of the two arrays is populated.
 */
@SatisfiesRequirement("2.2.8.2.0")
public data class MyEmbedding(
    public val floatValues: FloatArray? = null,
    public val quantizedValues: ByteArray? = null,
    public val isQuantized: Boolean = false,
) {
    /**
     * Compares this embedding with another object for equality.
     *
     * @param other The object to compare with.
     * @return True if embeddings values are identical.
     */
    @SatisfiesRequirement("2.2.8.2.0")
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MyEmbedding

        if (floatValues != null) {
            if (other.floatValues == null) return false
            if (!floatValues.contentEquals(other.floatValues)) return false
        } else if (other.floatValues != null) {
            return false
        }
        if (quantizedValues != null) {
            if (other.quantizedValues == null) return false
            if (!quantizedValues.contentEquals(other.quantizedValues)) return false
        } else if (other.quantizedValues != null) {
            return false
        }
        if (isQuantized != other.isQuantized) return false

        return true
    }

    /**
     * Generates a hash code for this embedding.
     *
     * @return The hash integer.
     */
    @SatisfiesRequirement("2.2.8.2.0")
    override fun hashCode(): Int {
        var result = floatValues?.contentHashCode() ?: 0
        result = 31 * result + (quantizedValues?.contentHashCode() ?: 0)
        result = 31 * result + isQuantized.hashCode()
        return result
    }
}

/**
 * A utility object for serializing and deserializing MediaPipe [Embedding] objects.
 *
 * This utility handles the conversion of complex AI model outputs into formats suitable for
 * database storage (BLOB) and network transmission.
 */
@SatisfiesRequirement("2.2.8.2.0")
public object EmbeddingUtils {
    private const val TAG: String = "EmbeddingUtils"

    /**
     * Converts a MediaPipe [Embedding] object to a [ByteArray] for database storage.
     *
     * It prioritizes the quantized embedding if available (saving space), otherwise it converts
     * the float embedding into a byte array using Little Endian byte order.
     *
     * @param embedding The MediaPipe embedding to convert.
     * @return A [ByteArray] representation of the embedding.
     * @throws IllegalArgumentException if the embedding is null or empty.
     */
    @SatisfiesRequirement("2.2.8.2.0")
    @Suppress("kotlin:S6529") // isNullOrEmpty() is not available for FloatArray, so this check is necessary.
    public fun embeddingToByteArray(embedding: Embedding): ByteArray {
        // Vérifie d'abord l'embedding quantifié
        embedding.quantizedEmbedding()?.let {
            if (it.isNotEmpty()) return it
        }

        // Sinon, on vérifie l'embedding float
        val floatArray = embedding.floatEmbedding()

        // Crucial check: if the float array is null or empty, this is an error.
        require(floatArray != null && floatArray.isNotEmpty()) { "Embedding is null or empty, cannot convert." }

        val buffer = ByteBuffer.allocate(floatArray.size * 4).order(ByteOrder.LITTLE_ENDIAN)
        for (f in floatArray) buffer.putFloat(f)
        return buffer.array()
    }

    /**
     * Reconstructs our internal [MyEmbedding] representation from a [ByteArray].
     *
     * @param bytes The serialized embedding data from the database.
     * @param fromQuantized True if the original embedding was quantized (INT8).
     * @return A [MyEmbedding] object containing the deserialized data.
     */
    @SatisfiesRequirement("2.2.8.2.0")
    public fun byteArrayToMyEmbedding(
        bytes: ByteArray,
        fromQuantized: Boolean = false,
    ): MyEmbedding {
        return if (fromQuantized) {
            MyEmbedding(quantizedValues = bytes, isQuantized = true)
        } else {
            val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
            val floats = FloatArray(bytes.size / 4)
            for (i in floats.indices) {
                if (buffer.remaining() >= 4) {
                    floats[i] = buffer.getFloat()
                }
            }
            MyEmbedding(floatValues = floats)
        }
    }

    /**
     * Demonstrates the conversion cycle for logging or debugging purposes.
     *
     * @param embedding The original embedding to process.
     */
    @SatisfiesRequirement("2.2.8.2.0")
    @Suppress("unused")
    public fun demoUsage(embedding: Embedding) {
        val bytes = embeddingToByteArray(embedding)
        val restored = byteArrayToMyEmbedding(bytes, fromQuantized = embedding.quantizedEmbedding() != null)

        Log.d(TAG, "Original : ${bytes.size} bytes, restauré : ${if (restored.isQuantized) "quantized" else "float"}")
    }
}
