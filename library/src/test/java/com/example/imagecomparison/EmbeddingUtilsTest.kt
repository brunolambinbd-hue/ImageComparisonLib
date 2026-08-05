package com.example.imagecomparison

import com.google.common.truth.Truth.assertThat
import com.google.mediapipe.tasks.components.containers.Embedding
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
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
public class EmbeddingUtilsTest {

    /**
     * Verifies that [EmbeddingUtils.embeddingToByteArray] throws an exception for empty float embedding.
     */
    @Test(expected = IllegalArgumentException::class)
    @CoveredRequirement("REQ-2.2.8.2.2")
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
    @CoveredRequirement("REQ-2.2.8.2.2")
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
    @CoveredRequirement("REQ-2.2.8.2.1")
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
    @CoveredRequirement("REQ-2.2.8.2.1")
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
    @CoveredRequirement("REQ-2.2.8.2.1")
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
    @CoveredRequirement("REQ-2.2.8.2.1")
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
    @CoveredRequirement("REQ-2.2.8.2.1")
    public fun byteArrayToMyEmbedding_DefaultParams_RestoresAsFloat() {
        val sourceBytes = byteArrayOf(0, 0, 0, 0)
        val resultEmbedding = EmbeddingUtils.byteArrayToMyEmbedding(sourceBytes)
        assertThat(resultEmbedding.isQuantized).isFalse()
    }

    /**
     * Verifies that [EmbeddingUtils.byteArrayToMyEmbedding] handles corrupted/truncated byte arrays.
     */
    @Test
    @CoveredRequirement("REQ-2.2.8.2.4")
    public fun byteArrayToMyEmbedding_TruncatedBytes_HandlesPartialFloats() {
        val sourceBytes = byteArrayOf(0, 0, 128.toByte(), 63.toByte(), 0, 0) // 1.0f (4 bytes) + 2 extra bytes
        val resultEmbedding = EmbeddingUtils.byteArrayToMyEmbedding(sourceBytes, fromQuantized = false)
        
        // Expected size is 6/4 = 1.
        assertThat(resultEmbedding.floatValues?.size).isEqualTo(1)
        assertThat(resultEmbedding.floatValues?.get(0)).isEqualTo(1.0f)
    }

    /**
     * Verifies that [EmbeddingUtils.byteArrayToMyEmbedding] restores a quantized embedding correctly.
     */
    @Test
    @CoveredRequirement("REQ-2.2.8.2.1")
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
    @CoveredRequirement("REQ-2.2.8.2.1")
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
    @CoveredRequirement("REQ-2.2.8.2.1")
    public fun demoUsage_WithQuantized_ExecutesSuccessfully() {
        val mockEmbedding: Embedding = mock()
        whenever(mockEmbedding.floatEmbedding()).thenReturn(floatArrayOf(0.1f))
        whenever(mockEmbedding.quantizedEmbedding()).thenReturn(byteArrayOf(1, 2, 3))

        EmbeddingUtils.demoUsage(mockEmbedding)
    }

    /**
     * Verifies that [MyEmbedding] equals and hashCode work as expected.
     */
    @Test
    @CoveredRequirement("REQ-2.2.8.2.1")
    public fun myEmbedding_EqualsAndHashCode_BehaveCorrectly() {
        val f1 = floatArrayOf(0.1f)
        val f2 = floatArrayOf(0.1f)
        val q1 = byteArrayOf(1)
        val q2 = byteArrayOf(1)

        val e1 = MyEmbedding(f1, isQuantized = false)
        val e2 = MyEmbedding(f2, isQuantized = false)
        val e3 = MyEmbedding(quantizedValues = q1, isQuantized = true)
        val e4 = MyEmbedding(quantizedValues = q2, isQuantized = true)

        // Basic equality
        assertThat(e1).isEqualTo(e1)
        assertThat(e1).isEqualTo(e2)
        assertThat(e1.hashCode()).isEqualTo(e2.hashCode())
        assertThat(e3).isEqualTo(e4)
        assertThat(e3.hashCode()).isEqualTo(e4.hashCode())

        // Inequality
        assertThat(e1).isNotEqualTo(e3)
        assertThat(e1).isNotEqualTo(null)
        assertThat(e1).isNotEqualTo("not an embedding")
        
        // Edge cases for floatValues
        assertThat(MyEmbedding(floatValues = f1)).isNotEqualTo(MyEmbedding(floatValues = null))
        assertThat(MyEmbedding(floatValues = null)).isNotEqualTo(MyEmbedding(floatValues = f1))
        assertThat(MyEmbedding(floatValues = f1)).isNotEqualTo(MyEmbedding(floatValues = floatArrayOf(0.2f)))

        // Edge cases for quantizedValues
        assertThat(MyEmbedding(quantizedValues = q1)).isNotEqualTo(MyEmbedding(quantizedValues = null))
        assertThat(MyEmbedding(quantizedValues = null)).isNotEqualTo(MyEmbedding(quantizedValues = q1))
        assertThat(MyEmbedding(quantizedValues = q1)).isNotEqualTo(MyEmbedding(quantizedValues = byteArrayOf(2)))

        // isQuantized mismatch
        assertThat(MyEmbedding(isQuantized = true)).isNotEqualTo(MyEmbedding(isQuantized = false))
    }
}
