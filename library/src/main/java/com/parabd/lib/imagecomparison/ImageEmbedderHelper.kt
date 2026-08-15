/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.parabd.lib.imagecomparison

import com.parabd.lib.traceability.SatisfiesRequirement
import com.parabd.lib.traceability.CoveredRequirement

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.components.containers.Embedding
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.imageembedder.ImageEmbedder
import com.google.mediapipe.tasks.vision.imageembedder.ImageEmbedder.ImageEmbedderOptions

/**
 * A helper class that wraps the MediaPipe ImageEmbedder task.
 *
 * This class handles the setup of the MediaPipe model, computes image embeddings (signatures),
 * and calculates the cosine similarity between two embeddings.
 *
 * @param context The application context.
 * @property currentDelegate The hardware delegate to use (CPU or GPU).
 * @property currentModel The embedding model to use (e.g., MobileNetV3 Large).
 * @property listener An optional listener to receive error notifications.
 */
@SatisfiesRequirement("2.2.1.1")
public open class ImageEmbedderHelper(
    private val context: Context,
    public var currentDelegate: Int,
    public var currentModel: Int,
    public var listener: EmbedderListener? = null,
) {
    /**
     * Secondary constructor that provides default values for the delegate and model.
     *
     * @param context The application context.
     * @param listener The error listener.
     */
    @SatisfiesRequirement("2.2.1.1")
    public constructor(context: Context, listener: EmbedderListener?) : this(
        context,
        DELEGATE_CPU,
        MODEL_MOBILENETV3_LARGE,
        listener,
    )

    /** The internal MediaPipe ImageEmbedder instance. */
    private var imageEmbedder: ImageEmbedder? = null

    /** Flag to prevent multiple initializations. */
    private var isInitialized: Boolean = false

    /**
     * Configures and initializes the [ImageEmbedder] instance from MediaPipe.
     * This function sets up the model, delegate, and other options.
     */
    @SatisfiesRequirement("2.2.1.1")
    private fun setupImageEmbedder() {
        if (isInitialized) return

        val modelName =
            when (currentModel) {
                MODEL_MOBILENETV3_LARGE -> "mobilenet_v3_large.tflite"
                MODEL_MOBILENETV3_SMALL -> "mobilenet_v3_small.tflite"
                else -> "mobilenet_v3_large.tflite"
            }

        try {
            val baseOptionsBuilder = BaseOptions.builder().setModelAssetPath(modelName)
            if (currentDelegate == DELEGATE_GPU) {
                baseOptionsBuilder.setDelegate(Delegate.GPU)
            }
            val options =
                ImageEmbedderOptions.builder()
                    .setBaseOptions(baseOptionsBuilder.build())
                    .setL2Normalize(true)
                    .setQuantize(false)
                    .setRunningMode(RunningMode.IMAGE)
                    .build()

            imageEmbedder = ImageEmbedder.createFromOptions(context, options)
            isInitialized = true
            Log.i(TAG, "ImageEmbedder created successfully.")
        } catch (t: Throwable) {
            val errorMsg = "Image embedder failed to load. See error logs for details"
            listener?.onError(errorMsg, if (currentDelegate == DELEGATE_GPU) GPU_ERROR else UNKNOWN_ERROR)
            Log.e(TAG, "MediaPipe failed to load the model with error: ${t.message}", t)
        }
    }

    /**
     * Computes the embedding (signature) for a given bitmap image.
     *
     * @param bitmap The input image.
     * @return An [Embedding] object, or null if the computation fails.
     */
    @SatisfiesRequirement("2.2.1.1")
    public open fun computeSignature(bitmap: Bitmap): Embedding? {
        setupImageEmbedder()
        if (imageEmbedder == null) return null

        // CRUCIAL CHECK: Prevent native crash if bitmap is recycled.
        if (bitmap.isRecycled) {
            Log.e(TAG, "Cannot compute signature on a recycled bitmap.")
            return null
        }

        return try {
            val mpImage = BitmapImageBuilder(bitmap).build()
            imageEmbedder!!.embed(mpImage).embeddingResult().embeddings().firstOrNull()
        } catch (t: Throwable) {
            Log.e(TAG, "Error computing signature: ${t.message}", t)
            null
        }
    }

    /**
     * Computes the embeddings for two bitmaps and calculates their cosine similarity.
     *
     * @param firstBitmap The first image.
     * @param secondBitmap The second image.
     * @return A [ResultBundle] containing the similarity score and inference time, or null on failure.
     */
    @SatisfiesRequirement("2.2.8.1")
    public fun embed(
        firstBitmap: Bitmap,
        secondBitmap: Bitmap,
    ): ResultBundle? {
        if (imageEmbedder == null) return null

        val startTime = SystemClock.uptimeMillis()
        val firstEmbed = computeSignature(firstBitmap)
        val secondEmbed = computeSignature(secondBitmap)

        return if (firstEmbed != null && secondEmbed != null) {
            val inferenceTimeMs = SystemClock.uptimeMillis() - startTime
            ResultBundle(
                ImageEmbedder.cosineSimilarity(firstEmbed, secondEmbed),
                inferenceTimeMs,
            )
        } else {
            null
        }
    }

    /**
     * Closes the [ImageEmbedder] instance to free up resources.
     */
    @SatisfiesRequirement("2.2.1.1")
    public fun clearImageEmbedder() {
        imageEmbedder?.close()
        imageEmbedder = null
        isInitialized = false
    }

    /**
     * A data class to hold the result of an embedding comparison.
     *
     * @property similarity The cosine similarity score.
     * @property inferenceTime The time taken for the computation in milliseconds.
     */
    /**
     * A data class to hold the result of an embedding comparison.
     *
     * @property similarity The cosine similarity score.
     * @property inferenceTime The time taken for the computation in milliseconds.
     */
    @SatisfiesRequirement("2.2.8.1")
    public data class ResultBundle(public val similarity: Double, private val inferenceTime: Long)

    /**
     * An interface to listen for errors from the ImageEmbedderHelper.
     */
    public interface EmbedderListener {
        /**
         * Invoked when an internal error occurs.
         *
         * @param error Descriptive error message.
         * @param errorCode Machine-readable error code.
         */
        @SatisfiesRequirement("2.2.1.2")
        public fun onError(
            error: String,
            errorCode: Int = UNKNOWN_ERROR,
        )
    }

    public companion object {
        /** Use CPU for inference. */
        public const val DELEGATE_CPU: Int = 0

        /** Use GPU for inference. */
        public const val DELEGATE_GPU: Int = 1

        /** Large MobileNetV3 model (More accurate, slower). */
        public const val MODEL_MOBILENETV3_LARGE: Int = 0

        /** Small MobileNetV3 model (Faster, less accurate). */
        public const val MODEL_MOBILENETV3_SMALL: Int = 1

        /** Default error code. */
        public const val UNKNOWN_ERROR: Int = 0

        /** GPU-specific error code. */
        public const val GPU_ERROR: Int = 1

        /** Log tag. */
        private const val TAG: String = "ImageEmbedderHelper"
    }
}



