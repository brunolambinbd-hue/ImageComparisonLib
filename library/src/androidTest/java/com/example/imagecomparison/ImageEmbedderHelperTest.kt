package com.example.imagecomparison

import android.content.Context
import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for [ImageEmbedderHelper].
 *
 * These tests verify the initialization and basic embedding computation using MediaPipe
 * in an Android environment.
 */
@RunWith(AndroidJUnit4::class)
@Suppress("RedundantVisibilityModifier")
public class ImageEmbedderHelperTest {
    private lateinit var context: Context

    /**
     * Set up the instrumentation context.
     */
    @Before
    public fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    /**
     * Verifies that [ImageEmbedderHelper.computeSignature] returns a 1280-dim vector for the Large model.
     */
    @Test
    @CoveredRequirement("REQ-2.2.1.2.1")
    public fun computeSignature_LargeModel_ReturnsValidEmbedding() {
        val helper = ImageEmbedderHelper(context, null)
        val dummy = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val result = helper.computeSignature(dummy)
        assertNotNull(result)
        assertEquals(1280, result?.floatEmbedding()?.size)
        helper.clearImageEmbedder()
    }

    /**
     * Verifies that [ImageEmbedderHelper.computeSignature] returns a 1024-dim vector for the Small model.
     */
    @Test
    @CoveredRequirement("REQ-2.2.1.2.1")
    public fun computeSignature_SmallModel_ReturnsValidEmbedding() {
        val helper = ImageEmbedderHelper(
            context = context,
            currentDelegate = ImageEmbedderHelper.DELEGATE_CPU,
            currentModel = ImageEmbedderHelper.MODEL_MOBILENETV3_SMALL,
            listener = null
        )
        val dummy = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val result = helper.computeSignature(dummy)
        assertNotNull(result)
        assertEquals(1024, result?.floatEmbedding()?.size)
        helper.clearImageEmbedder()
    }

    /**
     * Verifies that [ImageEmbedderHelper.computeSignature] returns null for recycled bitmap.
     */
    @Test
    @CoveredRequirement("REQ-2.2.1.2.2")
    public fun computeSignature_RecycledBitmap_ReturnsNull() {
        val helper = ImageEmbedderHelper(context, null)
        val dummy = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        dummy.recycle()
        val result = helper.computeSignature(dummy)
        assertNull(result)
        helper.clearImageEmbedder()
    }

    /**
     * Verifies that [ImageEmbedderHelper.computeSignature] handles internal exceptions.
     */
    @Test
    @CoveredRequirement("REQ-2.2.1.2.2")
    public fun computeSignature_HardwareBitmap_TriggersCatch() {
        val helper = ImageEmbedderHelper(context, null)
        val dummy = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val hardwareBitmap = dummy.copy(Bitmap.Config.HARDWARE, false)
        
        // MediaPipe BitmapImageBuilder might throw for HARDWARE bitmaps
        val result = helper.computeSignature(hardwareBitmap)
        assertNull(result)
        helper.clearImageEmbedder()
    }

    /**
     * Verifies that [ImageEmbedderHelper.embed] returns 1.0 similarity for identical images.
     */
    @Test
    @CoveredRequirement("REQ-2.2.8.2.1")
    public fun embed_IdenticalImages_ReturnsMaxSimilarity() {
        val helper = ImageEmbedderHelper(context, null)
        val dummy = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        
        // Initialisation de l'embedder nécessaire avant l'appel à embed
        helper.computeSignature(dummy)
        
        val result = helper.embed(dummy, dummy)
        assertNotNull(result)
        assertEquals(1.0, result!!.similarity, 0.001)
        helper.clearImageEmbedder()
    }

    /**
     * Verifies that [ImageEmbedderHelper.embed] returns null if second signature fails.
     */
    @Test
    @CoveredRequirement("REQ-2.2.8.2.2")
    public fun embed_SecondRecycledBitmap_ReturnsNull() {
        val helper = ImageEmbedderHelper(context, null)
        val dummy1 = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val dummy2 = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        
        helper.computeSignature(dummy1)
        dummy2.recycle()
        
        val result = helper.embed(dummy1, dummy2)
        assertNull(result)
        helper.clearImageEmbedder()
    }

    /**
     * Verifies that [ImageEmbedderHelper.embed] returns null if first signature fails.
     */
    @Test
    @CoveredRequirement("REQ-2.2.8.2.2")
    public fun embed_FirstRecycledBitmap_ReturnsNull() {
        val helper = ImageEmbedderHelper(context, null)
        val dummy1 = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val dummy2 = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        
        helper.computeSignature(dummy2)
        dummy1.recycle()
        
        val result = helper.embed(dummy1, dummy2)
        assertNull(result)
        helper.clearImageEmbedder()
    }

    /**
     * Verifies that [ImageEmbedderHelper.embed] returns null if both signatures fail.
     */
    @Test
    @CoveredRequirement("REQ-2.2.8.2.2")
    public fun embed_BothRecycledBitmaps_ReturnsNull() {
        val helper = ImageEmbedderHelper(context, null)
        val dummy1 = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val dummy2 = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        
        // Ensure initialized
        helper.computeSignature(dummy1)
        
        dummy1.recycle()
        dummy2.recycle()
        
        val result = helper.embed(dummy1, dummy2)
        assertNull(result)
        helper.clearImageEmbedder()
    }
}
