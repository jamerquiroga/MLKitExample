package com.jquiroga.mlkitexample.barcodedetection

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import androidx.annotation.MainThread
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.jquiroga.mlkitexample.camera.*
import kotlinx.coroutines.*

import java.io.IOException

/** Un procesador para ejecutar el detector de código de barras.  */
class BarcodeProcessor(graphicOverlay: GraphicOverlay, private val workflowModel: WorkflowModel) :
    FrameProcessorBase<List<FirebaseVisionBarcode>>() {

    private val detector = FirebaseVision.getInstance().visionBarcodeDetector
    private val cameraReticleAnimator: CameraReticleAnimator = CameraReticleAnimator(graphicOverlay)
    private val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

    override fun detectInImage(image: FirebaseVisionImage): Task<List<FirebaseVisionBarcode>> =
        detector.detectInImage(image)

    @MainThread
    override fun onSuccess(
        image: FirebaseVisionImage,
        results: List<FirebaseVisionBarcode>,
        graphicOverlay: GraphicOverlay
    ) {

        graphicOverlay.clear()

        cameraReticleAnimator.start()
        graphicOverlay.add(BarcodeReticleGraphic(graphicOverlay, cameraReticleAnimator))

        if (!workflowModel.isCameraLive) {
            return
        }else{
            val barcodeInCenter = results.firstOrNull { barcode ->

                val boundingBox = barcode.boundingBox ?: return@firstOrNull false
                val box = graphicOverlay.translateRect(boundingBox)
                box.contains(graphicOverlay.width / 2f, graphicOverlay.height / 2f)
            }

            if (barcodeInCenter == null) {
                workflowModel.setWorkflowState(WorkflowModel.WorkflowState.DETECTING)
            } else {
                startTone()
                workflowModel.markCameraFrozen()
                workflowModel.setWorkflowState(WorkflowModel.WorkflowState.DETECTED)
                workflowModel.detectedBarcode.value = barcodeInCenter

                GlobalScope.launch(Dispatchers.Main) {
                    delay(1000)
                    workflowModel.markCameraLive()
                }
            }
            graphicOverlay.invalidate()
        }

    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Barcode detection failed!", e)
    }

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Failed to close barcode detector!", e)
        }
    }

    companion object {
        private const val TAG = "BarcodeProcessor"
    }

    private fun startTone() {
        try {
            toneGen.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
        }catch (e: Exception){
            Log.e(TAG, "Failed to start tone barcode detector!", e)
        }

    }
}
