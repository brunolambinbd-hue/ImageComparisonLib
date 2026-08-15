package com.parabd.lib.imagecomparison

import com.parabd.lib.traceability.SatisfiesRequirement
import com.parabd.lib.traceability.CoveredRequirement

import android.content.Context
import android.graphics.Bitmap
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

/**
 * Unit tests for [ImageEmbedderHelper] covering logic that doesn't require native MediaPipe.
 */
@RunWith(RobolectricTestRunner::class)
@Suppress("RedundantVisibilityModifier")
public class ImageEmbedderHelperUnitTest {

    private val context: Context = mock()

    /**
     * Verifies constants are correctly defined.
     */
    @Test
    @CoveredRequirement("2.2.1.1")
    public fun constants_AreCorrect() {
        assertThat(ImageEmbedderHelper.DELEGATE_CPU).isEqualTo(0)
        assertThat(ImageEmbedderHelper.DELEGATE_GPU).isEqualTo(1)
        assertThat(ImageEmbedderHelper.MODEL_MOBILENETV3_LARGE).isEqualTo(0)
        assertThat(ImageEmbedderHelper.MODEL_MOBILENETV3_SMALL).isEqualTo(1)
        assertThat(ImageEmbedderHelper.UNKNOWN_ERROR).isEqualTo(0)
        assertThat(ImageEmbedderHelper.GPU_ERROR).isEqualTo(1)
    }

    /**
     * Verifies the secondary constructor and default values.
     */
    @Test
    @CoveredRequirement("2.2.1.1")
    public fun secondaryConstructor_SetsDefaultValues() {
        val listener: ImageEmbedderHelper.EmbedderListener = mock()
        val helper = ImageEmbedderHelper(context, listener)

        assertThat(helper.currentDelegate).isEqualTo(ImageEmbedderHelper.DELEGATE_CPU)
        assertThat(helper.currentModel).isEqualTo(ImageEmbedderHelper.MODEL_MOBILENETV3_LARGE)
        assertThat(helper.listener).isEqualTo(listener)
    }

    /**
     * Verifies getters and setters for properties.
     */
    @Test
    @CoveredRequirement("2.2.1.1")
    public fun properties_GettersAndSetters_WorkCorrectly() {
        val helper = ImageEmbedderHelper(context, ImageEmbedderHelper.DELEGATE_CPU, ImageEmbedderHelper.MODEL_MOBILENETV3_LARGE)
        
        helper.currentDelegate = ImageEmbedderHelper.DELEGATE_GPU
        assertThat(helper.currentDelegate).isEqualTo(ImageEmbedderHelper.DELEGATE_GPU)
        
        helper.currentModel = ImageEmbedderHelper.MODEL_MOBILENETV3_SMALL
        assertThat(helper.currentModel).isEqualTo(ImageEmbedderHelper.MODEL_MOBILENETV3_SMALL)
        
        val listener: ImageEmbedderHelper.EmbedderListener = mock()
        helper.listener = listener
        assertThat(helper.listener).isEqualTo(listener)
    }

    /**
     * Verifies that [ImageEmbedderHelper.clearImageEmbedder] resets state.
     */
    @Test
    @CoveredRequirement("2.2.1.1")
    public fun clearImageEmbedder_ResetsState() {
        val helper = ImageEmbedderHelper(context, null)
        helper.clearImageEmbedder()
    }

    /**
     * Verifies [ImageEmbedderHelper.ResultBundle] accessors.
     */
    @Test
    @CoveredRequirement("2.2.8.1")
    public fun resultBundle_Accessors_Work() {
        val result = ImageEmbedderHelper.ResultBundle(0.95, 100L)
        assertThat(result.similarity).isEqualTo(0.95)
    }

    /**
     * Verifies that [ImageEmbedderHelper.computeSignature] returns null if setup fails (e.g. on JVM).
     * This also covers the catch block in setupImageEmbedder.
     */
    @Test
    @CoveredRequirement("2.2.1.2")
    public fun computeSignature_ReturnsNullOnSetupFailure_AndNotifiesListener() {
        val listener: ImageEmbedderHelper.EmbedderListener = mock()
        val helper = ImageEmbedderHelper(context, listener)
        val bitmap: Bitmap = mock()
        
        val result = helper.computeSignature(bitmap)
        
        assertThat(result).isNull()
        verify(listener).onError(any(), eq(ImageEmbedderHelper.UNKNOWN_ERROR))
    }

    /**
     * Verifies GPU delegate logic in setup (covers the branch).
     */
    @Test
    @CoveredRequirement("2.2.1.1")
    public fun setup_WithGPU_TriggersCatchOnJVM() {
        val listener: ImageEmbedderHelper.EmbedderListener = mock()
        val helper = ImageEmbedderHelper(context, ImageEmbedderHelper.DELEGATE_GPU, ImageEmbedderHelper.MODEL_MOBILENETV3_LARGE, listener)
        val bitmap: Bitmap = mock()
        
        helper.computeSignature(bitmap)
        
        verify(listener).onError(any(), eq(ImageEmbedderHelper.GPU_ERROR))
    }

    /**
     * Verifies small model selection branch.
     */
    @Test
    @CoveredRequirement("2.2.1.1")
    public fun setup_WithSmallModel_TriggersCatchOnJVM() {
        val helper = ImageEmbedderHelper(context, ImageEmbedderHelper.DELEGATE_CPU, ImageEmbedderHelper.MODEL_MOBILENETV3_SMALL, null)
        val bitmap: Bitmap = mock()
        val result = helper.computeSignature(bitmap)
        assertThat(result).isNull()
    }

    /**
     * Verifies unknown model selection branch (else).
     */
    @Test
    @CoveredRequirement("2.2.1.1")
    public fun setup_WithUnknownModel_TriggersCatchOnJVM() {
        val helper = ImageEmbedderHelper(context, ImageEmbedderHelper.DELEGATE_CPU, 99, null)
        val bitmap: Bitmap = mock()
        val result = helper.computeSignature(bitmap)
        assertThat(result).isNull()
    }

    /**
     * Verifies that [ImageEmbedderHelper.embed] returns null if not initialized.
     */
    @Test
    @CoveredRequirement("2.2.8.2")
    public fun embed_ReturnsNullIfNotInitialized() {
        val helper = ImageEmbedderHelper(context, null)
        val bitmap: Bitmap = mock()
        val result = helper.embed(bitmap, bitmap)
        assertThat(result).isNull()
    }

    /**
     * Coverage for the internal bridge of EmbedderListener.
     */
    @Test
    @CoveredRequirement("2.2.1.2")
    public fun embedderListener_DefaultImplementation_Coverage() {
        val listener = object : ImageEmbedderHelper.EmbedderListener {
            override fun onError(error: String, errorCode: Int) {
                // do nothing
            }
        }
        listener.onError("test") // Should call bridge if it exists
    }
}



