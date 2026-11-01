package com.parabd.lib.imagecomparison

import com.parabd.lib.traceability.SatisfiesRequirement
import com.parabd.lib.traceability.CoveredRequirement

import com.google.common.truth.Truth.assertThat
import com.google.mediapipe.tasks.components.containers.Embedding
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Unit tests for [EmbeddingUtils].
 *
 * These tests ensure that the conversion between MediaPipe embeddings and byte arrays
 * is accurate and handles different data types correctly.
 */
@RunWith(RobolectricTestRunner::class)
@Suppress("RedundantVisibilityModifier", "ReplaceCallWithBinaryOperator")
public class EmbeddingUtilsTest {

    /**
     * Verifies that [EmbeddingUtils.embeddingToByteArray] throws an exception for empty float embedding.
     */
    @Test(expected = IllegalArgumentException::class)
    @CoveredRequirement("2.2.3.1.1")
    @CoveredRequirement("2.2.3.2.1")
    @CoveredRequirement("2.2.3.3.1")
    @CoveredRequirement("2.2.3.4.1")
    @CoveredRequirement("2.2.3.9.1")
    @CoveredRequirement("2.2.8.2")
    public fun embeddingToByteArray_EmptyFloat_ThrowsException() {
        val mockEmbedding: Embedding = mock()
        whenever(mockEmbedding.floatEmbedding()).thenReturn(floatArrayOf())
        whenever(mockEmbedding.quantizedEmbedding()).thenReturn(null)

        EmbeddingUtils.embeddingToByteArray(mockEmbedding)
    }

    /**
     * Verifies that [EmbeddingUtils.embeddingToByteArray] throws an exception for null float and quantized embeddings.
     */
    @Test(expected = IllegalArgumentException::class)
    @CoveredRequirement("2.2.8.2")
    public fun embeddingToByteArray_NullEmbeddings_ThrowsException() {
        val mockEmbedding: Embedding = mock()
        whenever(mockEmbedding.floatEmbedding()).thenReturn(null)
        whenever(mockEmbedding.quantizedEmbedding()).thenReturn(null)

        EmbeddingUtils.embeddingToByteArray(mockEmbedding)
    }

    /**
     * Verifies that [EmbeddingUtils.embeddingToByteArray] handles empty quantized embedding by falling back to float.
     */
    @Test
    @CoveredRequirement("2.2.8.1")
    public fun embeddingToByteArray_EmptyQuantized_FallsBackToFloat() {
        val testFloats = floatArrayOf(0.5f)
        val mockEmbedding: Embedding = mock()
        whenever(mockEmbedding.quantizedEmbedding()).thenReturn(byteArrayOf())
        whenever(mockEmbedding.floatEmbedding()).thenReturn(testFloats)

        val resultBytes = EmbeddingUtils.embeddingToByteArray(mockEmbedding)
        assertThat(resultBytes.size).isEqualTo(4)
    }

    /**
     * Verifies that [EmbeddingUtils.embeddingToByteArray] converts a valid float embedding correctly.
     */
    @Test
    @CoveredRequirement("2.2.8.1.10")
    public fun embeddingToByteArray_ValidFloat_ConvertsCorrectly() {
        val testFloats = floatArrayOf(0.1f, -0.2f, 1.0f)
        val mockEmbedding: Embedding = mock()
        whenever(mockEmbedding.floatEmbedding()).thenReturn(testFloats)
        whenever(mockEmbedding.quantizedEmbedding()).thenReturn(null)

        val resultBytes = EmbeddingUtils.embeddingToByteArray(mockEmbedding)

        assertEquals(12, resultBytes.size) // 3 floats * 4 bytes
        val buffer = ByteBuffer.wrap(resultBytes).order(ByteOrder.LITTLE_ENDIAN)
        val restoredFloats = FloatArray(3) { buffer.getFloat() }
        assertArrayEquals(testFloats, restoredFloats, 0.001f)
    }

    /**
     * Verifies that [EmbeddingUtils.embeddingToByteArray] returns the same array for quantized embedding.
     */
    @Test
    @CoveredRequirement("2.2.8.1")
    public fun embeddingToByteArray_Quantized_ReturnsSameArray() {
        val testBytes = byteArrayOf(10, 20, 30, 40)
        val mockEmbedding: Embedding = mock()
        whenever(mockEmbedding.quantizedEmbedding()).thenReturn(testBytes)
        whenever(mockEmbedding.floatEmbedding()).thenReturn(floatArrayOf(0.1f))

        val resultBytes = EmbeddingUtils.embeddingToByteArray(mockEmbedding)

        assertSame(testBytes, resultBytes)
    }

    /**
     * Verifies that [EmbeddingUtils.byteArrayToMyEmbedding] restores a float embedding correctly.
     */
    @Test
    @CoveredRequirement("2.2.8.1")
    public fun byteArrayToMyEmbedding_FloatBytes_RestoresCorrectly() {
        val buffer = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN)
        buffer.putFloat(0.1f).putFloat(-0.2f).putFloat(1.0f)
        val sourceBytes = buffer.array()

        val resultEmbedding = EmbeddingUtils.byteArrayToMyEmbedding(sourceBytes, fromQuantized = false)

        assertThat(resultEmbedding.floatValues).isNotNull()
        assertArrayEquals(floatArrayOf(0.1f, -0.2f, 1.0f), resultEmbedding.floatValues!!, 0.001f)
        assertThat(resultEmbedding.isQuantized).isFalse()
    }

    /**
     * Verifies that [EmbeddingUtils.byteArrayToMyEmbedding] uses default parameters correctly.
     */
    @Test
    @CoveredRequirement("2.2.8.1")
    public fun byteArrayToMyEmbedding_DefaultParams_RestoresAsFloat() {
        val sourceBytes = byteArrayOf(0, 0, 0, 0)
        val resultEmbedding = EmbeddingUtils.byteArrayToMyEmbedding(sourceBytes)
        assertThat(resultEmbedding.isQuantized).isFalse()
    }
    
    /**
     * Verifies empty byte array handling.
     */
    @Test
    @CoveredRequirement("2.2.8.1")
    public fun byteArrayToMyEmbedding_EmptyBytes_ReturnsEmptyEmbedding() {
        val result = EmbeddingUtils.byteArrayToMyEmbedding(byteArrayOf(), fromQuantized = false)
        assertThat(result.floatValues).isEmpty()
    }

    /**
     * Verifies that [EmbeddingUtils.byteArrayToMyEmbedding] handles corrupted/truncated byte arrays.
     */
    @Test
    @CoveredRequirement("2.2.8.4")
    public fun byteArrayToMyEmbedding_TruncatedBytes_HandlesPartialFloats() {
        // 4 bytes for 1.0f + 3 bytes (not enough for another float)
        val sourceBytes = byteArrayOf(0, 0, 128.toByte(), 63.toByte(), 1, 2, 3) 
        val resultEmbedding = EmbeddingUtils.byteArrayToMyEmbedding(sourceBytes, fromQuantized = false)
        
        // Expected size is 7/4 = 1.
        assertThat(resultEmbedding.floatValues?.size).isEqualTo(1)
        assertThat(resultEmbedding.floatValues?.get(0)).isEqualTo(1.0f)
    }

    /**
     * Verifies that [EmbeddingUtils.byteArrayToMyEmbedding] restores a quantized embedding correctly.
     */
    @Test
    @CoveredRequirement("2.2.8.1")
    public fun byteArrayToMyEmbedding_QuantizedBytes_RestoresCorrectly() {
        val testBytes = byteArrayOf(1, 2, 3)
        val resultEmbedding = EmbeddingUtils.byteArrayToMyEmbedding(testBytes, fromQuantized = true)

        assertThat(resultEmbedding.quantizedValues).isEqualTo(testBytes)
        assertThat(resultEmbedding.isQuantized).isTrue()
        assertThat(resultEmbedding.floatValues).isNull()
    }

    /**
     * Verifies that [EmbeddingUtils.demoUsage] executes successfully with float embedding.
     */
    @Test
    @CoveredRequirement("2.2.8.1")
    public fun demoUsage_WithFloat_ExecutesSuccessfully() {
        val mockEmbedding: Embedding = mock()
        whenever(mockEmbedding.floatEmbedding()).thenReturn(floatArrayOf(0.1f))
        whenever(mockEmbedding.quantizedEmbedding()).thenReturn(null)

        EmbeddingUtils.demoUsage(mockEmbedding)
    }

    /**
     * Verifies that [EmbeddingUtils.demoUsage] executes successfully with quantized embedding.
     */
    @Test
    @CoveredRequirement("2.2.8.1")
    public fun demoUsage_WithQuantized_ExecutesSuccessfully() {
        val mockEmbedding: Embedding = mock()
        whenever(mockEmbedding.floatEmbedding()).thenReturn(floatArrayOf(0.1f))
        whenever(mockEmbedding.quantizedEmbedding()).thenReturn(byteArrayOf(1, 2, 3))

        EmbeddingUtils.demoUsage(mockEmbedding)
    }

    /**
     * Satures equals for MyEmbedding.
     */
    @Test
    @CoveredRequirement("2.2.8.1")
    public fun myEmbedding_Equals_Saturation() {
        val f1 = floatArrayOf(0.1f)
        val q1 = byteArrayOf(1)
        val e1 = MyEmbedding(f1, q1, false)

        // Branch: this === other
        assertTrue(e1.equals(e1))
        
        // Branch: javaClass != other?.javaClass
        assertFalse(e1.equals(null))
        assertFalse(e1.equals("Not an embedding"))
        
        // Branch: isQuantized mismatch
        assertFalse(e1.equals(e1.copy(isQuantized = true)))
        
        // Branch: floatValues null checks
        val eNullFloat = MyEmbedding(null, q1, false)
        assertFalse(e1.equals(eNullFloat)) // this.floatValues != null, other.floatValues == null
        assertFalse(eNullFloat.equals(e1)) // this.floatValues == null, other.floatValues != null
        
        // Both floatValues null (covers "false" branch of else if)
        val eBothNullFloat = MyEmbedding(null, q1, false)
        val eBothNullFloat2 = MyEmbedding(null, q1, false)
        assertTrue(eBothNullFloat.equals(eBothNullFloat2))
        
        // Branch: floatValues content mismatch (same size)
        assertFalse(e1.equals(e1.copy(floatValues = floatArrayOf(0.2f))))
        // Branch: floatValues content mismatch (different size)
        assertFalse(e1.equals(e1.copy(floatValues = floatArrayOf(0.1f, 0.2f))))
        
        // Branch: quantizedValues null checks
        val eNullQuant = MyEmbedding(f1, null, false)
        assertFalse(e1.equals(eNullQuant)) // this.quantizedValues != null, other.quantizedValues == null
        assertFalse(eNullQuant.equals(e1)) // this.quantizedValues == null, other.quantizedValues != null
        
        // Both quantizedValues null (covers "false" branch of else if)
        val eBothNullQuant = MyEmbedding(f1, null, false)
        val eBothNullQuant2 = MyEmbedding(f1, null, false)
        assertTrue(eBothNullQuant.equals(eBothNullQuant2))

        // Branch: quantizedValues content mismatch (same size)
        assertFalse(e1.equals(e1.copy(quantizedValues = byteArrayOf(2))))
        // Branch: quantizedValues content mismatch (different size)
        assertFalse(e1.equals(e1.copy(quantizedValues = byteArrayOf(1, 2))))

        // Equality check
        val e1Clone = MyEmbedding(floatArrayOf(0.1f), byteArrayOf(1), false)
        assertTrue(e1.equals(e1Clone))
        assertEquals(e1.hashCode(), e1Clone.hashCode())
    }

    /**
     * Satures hashCode specifically.
     */
    @Test
    @CoveredRequirement("2.2.8.1")
    public fun myEmbedding_HashCode_Saturation() {
        val eNull = MyEmbedding(null, null, false)
        val h1 = eNull.hashCode()
        
        val eFloat = MyEmbedding(floatArrayOf(1.0f), null, false)
        assertThat(eFloat.hashCode()).isNotEqualTo(h1)
        
        val eQuant = MyEmbedding(null, byteArrayOf(1), true)
        assertThat(eQuant.hashCode()).isNotEqualTo(h1)
    }
}



