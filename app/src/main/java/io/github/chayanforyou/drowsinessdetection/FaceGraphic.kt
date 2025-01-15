package io.github.chayanforyou.drowsinessdetection

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark
import com.google.mlkit.vision.face.FaceLandmark.LandmarkType
import io.github.chayanforyou.drowsinessdetection.overlay.GraphicOverlay
import java.util.Locale
import kotlin.math.max

class FaceGraphic(overlay: GraphicOverlay, private val face: Face, ) : GraphicOverlay.Graphic(overlay) {
  private val facePositionPaint: Paint
  private val faceLandmarkPaint: Paint
  private val labelPaint: Paint
  private val boxPaint: Paint
  private val boxBgPaint: Paint

  init {
    Log.d(TAG, "initialize")
    facePositionPaint = Paint()
    facePositionPaint.color = Color.GREEN

    faceLandmarkPaint = Paint()
    faceLandmarkPaint.color = Color.MAGENTA

    labelPaint = Paint()
    labelPaint.color = Color.WHITE
    labelPaint.textSize = ID_TEXT_SIZE

    boxPaint = Paint()
    boxPaint.color = Color.RED
    boxPaint.style = Paint.Style.STROKE
    boxPaint.strokeWidth = BOX_STROKE_WIDTH

    boxBgPaint = Paint()
    boxBgPaint.color = Color.RED
    boxBgPaint.style = Paint.Style.FILL
  }

  /** Draws the face annotations for position on the supplied canvas. */
  override fun draw(canvas: Canvas) {
    // Draws a circle at the position of the detected face, with the face's track id below.
    val x = translateX(face.boundingBox.centerX().toFloat())
    val y = translateY(face.boundingBox.centerY().toFloat())
    canvas.drawCircle(x, y, FACE_POSITION_RADIUS, facePositionPaint)

    // Calculate positions.
    val left = x - scale(face.boundingBox.width() / 2.0f)
    val top = y - scale(face.boundingBox.height() / 2.0f)
    val right = x + scale(face.boundingBox.width() / 2.0f)
    val bottom = y + scale(face.boundingBox.height() / 2.0f)
    val lineHeight = ID_TEXT_SIZE + BOX_STROKE_WIDTH
    var yLabelOffset = if (face.trackingId == null) 0f else -lineHeight

    // Calculate width and height of label box
    var textWidth = labelPaint.measureText("ID: " + face.trackingId)
    if (face.leftEyeOpenProbability != null) {
      yLabelOffset -= lineHeight
      textWidth =
        max(
          textWidth,
          labelPaint.measureText(
            String.format(Locale.US, "Left eye open: %.2f", face.leftEyeOpenProbability)
          )
        )
    }
    if (face.rightEyeOpenProbability != null) {
      yLabelOffset -= lineHeight
      textWidth =
        max(
          textWidth,
          labelPaint.measureText(
            String.format(Locale.US, "Right eye open: %.2f", face.rightEyeOpenProbability)
          )
        )
    }

    // Draw box
    canvas.drawRect(
      left - BOX_STROKE_WIDTH,
      top + yLabelOffset,
      left + textWidth + 2 * BOX_STROKE_WIDTH,
      top,
      boxBgPaint
    )
    yLabelOffset += ID_TEXT_SIZE

    // Draw face boundary
    canvas.drawRect(left, top, right, bottom, boxPaint)
    if (face.trackingId != null) {
      canvas.drawText("ID: " + face.trackingId, left, top + yLabelOffset, labelPaint)
      yLabelOffset += lineHeight
    }

    // Draws all face contours.
    for (contour in face.allContours) {
      for (point in contour.points) {
        canvas.drawCircle(
          translateX(point.x),
          translateY(point.y),
          FACE_POSITION_RADIUS,
          facePositionPaint
        )
      }
    }

    // Draws left/right eye open probabilities.
    if (face.leftEyeOpenProbability != null) {
      canvas.drawText(
        "Left eye open: " + String.format(Locale.US, "%.2f", face.leftEyeOpenProbability),
        left,
        top + yLabelOffset,
        labelPaint
      )
      yLabelOffset += lineHeight
    }

    if (face.rightEyeOpenProbability != null) {
      canvas.drawText(
        "Right eye open: " + String.format(Locale.US, "%.2f", face.rightEyeOpenProbability),
        left,
        top + yLabelOffset,
        labelPaint
      )
      yLabelOffset += lineHeight
    }

    // Draw facial landmarks
    drawFaceLandmark(canvas, FaceLandmark.LEFT_EYE)
    drawFaceLandmark(canvas, FaceLandmark.RIGHT_EYE)
    //drawFaceLandmark(canvas, FaceLandmark.LEFT_CHEEK)
    //drawFaceLandmark(canvas, FaceLandmark.RIGHT_CHEEK)
  }

  private fun drawFaceLandmark(canvas: Canvas, @LandmarkType landmarkType: Int) {
    val faceLandmark = face.getLandmark(landmarkType)
    if (faceLandmark != null) {
      canvas.drawCircle(
        translateX(faceLandmark.position.x),
        translateY(faceLandmark.position.y),
        FACE_POSITION_RADIUS,
        faceLandmarkPaint
      )
    }
  }

  companion object {
    private const val TAG = "FaceGraphic"
    private const val FACE_POSITION_RADIUS = 8.0f
    private const val ID_TEXT_SIZE = 36.0f
    private const val BOX_STROKE_WIDTH = 6.0f
  }
}
