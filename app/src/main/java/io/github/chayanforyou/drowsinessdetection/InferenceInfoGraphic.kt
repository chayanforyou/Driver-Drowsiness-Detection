/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.chayanforyou.drowsinessdetection

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import io.github.chayanforyou.drowsinessdetection.overlay.GraphicOverlay
import io.github.chayanforyou.drowsinessdetection.overlay.GraphicOverlay.Graphic

class InferenceInfoGraphic(
    overlay: GraphicOverlay,
    private val frameLatency: Long,
    private val detectorLatency: Long,
    private val framesPerSecond: Int,
    private val eyesClosedCount: Int,
    private val alarmCount: Int
) : Graphic(overlay) {
    private val textPaint = Paint()

    init {
        textPaint.color = TEXT_COLOR
        textPaint.textSize = TEXT_SIZE
        postInvalidate()
    }

    @Synchronized
    override fun draw(canvas: Canvas) {
        val x = TEXT_SIZE * 0.5f
        val y = TEXT_SIZE * 1.5f

        canvas.drawText("Alarm Count: $alarmCount", x, y, textPaint)
        canvas.drawText("Eyes Closed Count: $eyesClosedCount", x, y + TEXT_SIZE, textPaint)
        canvas.drawText("FPS: $framesPerSecond, Frame latency: $frameLatency ms", x, y + TEXT_SIZE * 2, textPaint)
        canvas.drawText("Detector latency: $detectorLatency ms", x, y + TEXT_SIZE * 3, textPaint)
    }

    companion object {
        private const val TEXT_COLOR = Color.BLUE
        private const val TEXT_SIZE = 50.0f
    }
}
