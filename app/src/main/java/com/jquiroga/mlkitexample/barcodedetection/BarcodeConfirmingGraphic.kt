
package com.jquiroga.mlkitexample.barcodedetection

import android.graphics.Canvas
import android.graphics.Path
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.jquiroga.mlkitexample.camera.GraphicOverlay
import com.jquiroga.mlkitexample.settings.PreferenceUtils

/** Guía al usuario a acercar la cámara para confirmar el código de barras detectado.  */
internal class BarcodeConfirmingGraphic(overlay: GraphicOverlay, private val barcode: FirebaseVisionBarcode) :
    BarcodeGraphicBase(overlay) {

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        // Draws a highlighted path to indicate the current progress to meet size requirement.
        val sizeProgress = PreferenceUtils.getProgressToMeetBarcodeSizeRequirement(overlay, barcode)
        val path = Path()
        if (sizeProgress > 0.95f) {
            // To have a completed path with all corners rounded.
            path.moveTo(boxRect.left, boxRect.top)
            path.lineTo(boxRect.right, boxRect.top)
            path.lineTo(boxRect.right, boxRect.bottom)
            path.lineTo(boxRect.left, boxRect.bottom)
            path.close()
        } else {
            path.moveTo(boxRect.left, boxRect.top + boxRect.height() * sizeProgress)
            path.lineTo(boxRect.left, boxRect.top)
            path.lineTo(boxRect.left + boxRect.width() * sizeProgress, boxRect.top)

            path.moveTo(boxRect.right, boxRect.bottom - boxRect.height() * sizeProgress)
            path.lineTo(boxRect.right, boxRect.bottom)
            path.lineTo(boxRect.right - boxRect.width() * sizeProgress, boxRect.bottom)
        }
        canvas.drawPath(path, pathPaint)
    }
}
