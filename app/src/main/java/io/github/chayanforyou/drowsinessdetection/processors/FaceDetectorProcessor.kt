package io.github.chayanforyou.drowsinessdetection.processors

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import io.github.chayanforyou.drowsinessdetection.overlay.GraphicOverlay
import io.github.chayanforyou.drowsinessdetection.FaceGraphic
import io.github.chayanforyou.drowsinessdetection.utils.SoundPoolManager
import java.util.Locale

class FaceDetectorProcessor(context: Context, detectorOptions: FaceDetectorOptions?) :
  VisionProcessorBase<List<Face>>(context) {

  private val detector: FaceDetector
  private val soundManager: SoundPoolManager
  private var eyesClosedFrameCount = 0

  init {
    val options = detectorOptions
      ?: FaceDetectorOptions.Builder()
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .enableTracking()
        .build()

    detector = FaceDetection.getClient(options)
    soundManager = SoundPoolManager.getInstance(context)

    Log.v(MANUAL_TESTING_LOG, "Face detector options: $options")
  }

  override fun stop() {
    super.stop()
    detector.close()
    soundManager.stop()
  }

  override fun detectInImage(image: InputImage): Task<List<Face>> {
    return detector.process(image)
  }

  override fun onSuccess(faces: List<Face>, graphicOverlay: GraphicOverlay) {
    for (face in faces) {
      val leftEyeOpen = face.leftEyeOpenProbability
      val rightEyeOpen = face.rightEyeOpenProbability

      if (leftEyeOpen != null && rightEyeOpen != null) {
        if (leftEyeOpen < EYES_CLOSED_THRESHOLD && rightEyeOpen < EYES_CLOSED_THRESHOLD) {
          eyesClosedFrameCount++

          if (eyesClosedFrameCount >= framesPerSecond / 2) {
            eyesClosedCount++
            eyesClosedFrameCount = 0

            if (eyesClosedCount >= ALARM_COUNT_THRESHOLD) {
              alarmCount++
              eyesClosedCount = 0
              soundManager.playSound()
            }
          }
        } else {
          eyesClosedFrameCount = 0
        }
      }

      graphicOverlay.add(FaceGraphic(graphicOverlay, face))
    }
  }

  override fun onFailure(e: Exception) {
    Log.e(TAG, "Face detection failed $e")
  }

  companion object {
    private const val TAG = "FaceDetectorProcessor"
    private const val EYES_CLOSED_THRESHOLD = 0.50f
    private const val ALARM_COUNT_THRESHOLD = 10
  }
}
