package com.example.imagecomparison

import android.content.Context
import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
    @CoveredRequirement("2.2.1.2")
    public fun computeSignature_LargeModel_ReturnsValidEmbedding() {
        val helper = ImageEmbedderHelper(context, null)
        val dummy = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val result = helper.computeSignature(dummy)
        assertNotNull(result)
        assertEquals(1280, result?.floatEmbedding()?.size)
        helper.clearImageEmbedder()
    }

    /**
     * Verifies that [ImageEmbedderHelper.embed] returns 1.0 similarity for identical images.
     */
    @Test
    @CoveredRequirement("2.2.8.2")
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
}
