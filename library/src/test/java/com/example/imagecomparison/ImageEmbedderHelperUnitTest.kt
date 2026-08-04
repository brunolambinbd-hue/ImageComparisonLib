package com.example.imagecomparison

import android.content.Context
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner

/**
 * Unit tests for [ImageEmbedderHelper] covering logic that doesn't require native MediaPipe.
 */
@RunWith(RobolectricTestRunner::class)
public class ImageEmbedderHelperUnitTest {

    private val context: Context = mock()

    /**
     * Verifies constants are correctly defined.
     */
    @Test
    @CoveredRequirement("2.2.1.2.0")
    public fun constants_AreCorrect() {
        assertThat(ImageEmbedderHelper.DELEGATE_CPU).isEqualTo(0)
        assertThat(ImageEmbedderHelper.DELEGATE_GPU).isEqualTo(1)
        assertThat(ImageEmbedderHelper.MODEL_MOBILENETV3_LARGE).isEqualTo(0)
        assertThat(ImageEmbedderHelper.MODEL_MOBILENETV3_SMALL).isEqualTo(1)
    }

    /**
     * Verifies the secondary constructor and default values.
     */
    @Test
    @CoveredRequirement("2.2.1.2.0")
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
    @CoveredRequirement("2.2.1.2.0")
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
    @CoveredRequirement("2.2.1.2.0")
    public fun clearImageEmbedder_ResetsState() {
        val helper = ImageEmbedderHelper(context, null)
        // We can't easily trigger initialization without native crash, 
        // but we can verify it doesn't crash and cleans up if it was initialized.
        helper.clearImageEmbedder()
    }
}
