package com.jquiroga.mlkitexample.tottusScanner

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import com.jquiroga.mlkitexample.R
import com.jquiroga.mlkitexample.barcodedetection.BarcodeProcessor
import com.jquiroga.mlkitexample.camera.CameraSource
import com.jquiroga.mlkitexample.camera.WorkflowModel
import kotlinx.android.synthetic.main.camera_preview_overlay_kotlin.view.*
import kotlinx.android.synthetic.main.view_tottus_scanner.view.*
import java.io.IOException

class TottusScanner(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    companion object {
        private const val TAG = "TottusScanner"
    }

    private var mWorkflowModel: WorkflowModel? = null
    private var mCameraSource: CameraSource? = null

    private lateinit var mCurrentWorkflowState: WorkflowModel.WorkflowState

    init {
        inflate(context, R.layout.view_tottus_scanner, this)
        initUI()
    }

    private fun initUI() {
        mCameraSource = CameraSource(camera_preview_graphic_overlay)
    }

    fun setWorkFlowModel(pWorkflowModel: WorkflowModel) {
        mWorkflowModel = pWorkflowModel
    }

    fun setCurrentWorkFlowState(pCurrentWorkflowState: WorkflowModel.WorkflowState) {
        mCurrentWorkflowState = pCurrentWorkflowState
    }

    fun onResumeScanner() {
        if (mWorkflowModel != null && mCameraSource != null) {
            mWorkflowModel!!.markCameraFrozen()
            mCurrentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED
            mWorkflowModel!!.setWorkflowState(WorkflowModel.WorkflowState.DETECTING)
            mCameraSource!!.setFrameProcessor(
                BarcodeProcessor(
                    camera_preview_graphic_overlay,
                    mWorkflowModel!!
                )
            )
        }
    }

    fun onPauseScanner() {
        mCurrentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED
        stopCameraPreview()
    }

    fun onDestroyScanner() {
        mCameraSource?.release()
        mCameraSource = null
    }

    fun starCameraPreview() {

        when {
            mWorkflowModel != null && mCameraSource != null ->

                if (!mWorkflowModel!!.isCameraLive) {
                    try {
                        mWorkflowModel!!.markCameraLive()
                        camera_preview.start(mCameraSource!!)
                    } catch (e: IOException) {
                        Log.e(TAG, "Failed to start camera preview!", e)
                        mCameraSource!!.release()
                        mCameraSource = null
                    }
                }
            else -> Log.e(TAG, "Failed to start camera preview!")
        }
    }

    fun stopCameraPreview() {
        mWorkflowModel?.let {
            if (it.isCameraLive) {
                it.markCameraFrozen()
                camera_preview.stop()
            }
        }
    }

    fun getCurrentWorkFlowState(): WorkflowModel.WorkflowState {
        return mCurrentWorkflowState
    }

}