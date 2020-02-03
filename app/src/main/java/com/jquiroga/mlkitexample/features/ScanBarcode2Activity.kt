package com.jquiroga.mlkitexample.features

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.common.base.Objects
import com.jquiroga.mlkitexample.R
import com.jquiroga.mlkitexample.camera.WorkflowModel
import kotlinx.android.synthetic.main.activity_scan_barcode2.*

class ScanBarcode2Activity : AppCompatActivity() {

    private lateinit var mWorkflowModel: WorkflowModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_barcode2)

        mWorkflowModel = ViewModelProviders.of(this).get(WorkflowModel::class.java)

        scnTottusScanner.setWorkFlowModel(mWorkflowModel)

        initObservers()
    }

    private fun initObservers(){
        mWorkflowModel.workflowState.observe(this, Observer { workflowState ->

            if (workflowState == null || Objects.equal(scnTottusScanner.getCurrentWorkFlowState(), workflowState)) {
                return@Observer
            }

            scnTottusScanner.setCurrentWorkFlowState(workflowState)

            when(workflowState){
                WorkflowModel.WorkflowState.DETECTING -> {
                    scnTottusScanner.starCameraPreview()
                }
                WorkflowModel.WorkflowState.CONFIRMING -> {
                    scnTottusScanner.starCameraPreview()
                }
                WorkflowModel.WorkflowState.SEARCHING -> {
                    scnTottusScanner.stopCameraPreview()
                }
                WorkflowModel.WorkflowState.DETECTED, WorkflowModel.WorkflowState.SEARCHED -> {
                    scnTottusScanner.stopCameraPreview()
                }
            }

        })

        mWorkflowModel.detectedBarcode.observe(this, Observer { barcode ->
            barcode?.let {
                Toast.makeText(this, "Code: ${barcode.rawValue}", Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onResume() {
        super.onResume()
        scnTottusScanner.onResumeScanner()
    }

    override fun onPause() {
        super.onPause()
        scnTottusScanner.onPauseScanner()
    }

    override fun onDestroy() {
        super.onDestroy()
        scnTottusScanner.onDestroyScanner()
    }
}
