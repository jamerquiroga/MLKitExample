
package com.jquiroga.mlkitexample.camera

import android.app.Application
import androidx.annotation.MainThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import java.util.HashSet

/** Vea el modelo para manejar el flujo de trabajo de la aplicación basado en la vista previa de la cámara. */
class WorkflowModel(application: Application) : AndroidViewModel(application) {

    val workflowState = MutableLiveData<WorkflowState>()
    val detectedBarcode = MutableLiveData<FirebaseVisionBarcode>()

    private val objectIdsToSearch = HashSet<Int>()

    var isCameraLive = false
        private set

    /**
     * State set of the application workflow.
     */
    enum class WorkflowState {
        NOT_STARTED,
        DETECTING,
        DETECTED,
        CONFIRMING,
        SEARCHING,
        SEARCHED
    }

    @MainThread
    fun setWorkflowState(workflowState: WorkflowState) {
        this.workflowState.value = workflowState
    }

    fun markCameraLive() {
        isCameraLive = true
        objectIdsToSearch.clear()
    }

    fun markCameraFrozen() {
        isCameraLive = false
    }
}
