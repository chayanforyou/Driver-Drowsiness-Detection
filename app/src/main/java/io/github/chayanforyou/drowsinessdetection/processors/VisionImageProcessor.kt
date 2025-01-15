package io.github.chayanforyou.drowsinessdetection.processors

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.google.mlkit.common.MlKitException
import io.github.chayanforyou.drowsinessdetection.overlay.GraphicOverlay
import io.github.chayanforyou.drowsinessdetection.utils.FrameMetadata
import java.nio.ByteBuffer

interface VisionImageProcessor {
    /** Processes a bitmap image.  */
    fun processBitmap(bitmap: Bitmap?, graphicOverlay: GraphicOverlay)

    /** Processes ByteBuffer image data, e.g. used for Camera1 live preview case.  */
    @Throws(MlKitException::class)
    fun processByteBuffer(data: ByteBuffer?, frameMetadata: FrameMetadata?, graphicOverlay: GraphicOverlay)

    /** Processes ImageProxy image data, e.g. used for CameraX live preview case.  */
    @Throws(MlKitException::class)
    fun processImageProxy(image: ImageProxy, graphicOverlay: GraphicOverlay)

    /** Stops the underlying machine learning model and release resources.  */
    fun stop()
}
