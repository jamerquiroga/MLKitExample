package com.jquiroga.mlkitexample.features

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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

        mWorkflowModel.detectedBarcode.observe(this, Observer { barcode ->
            barcode?.let {
                Toast.makeText(this, "Code: ${barcode.rawValue}", Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onResume() {
        super.onResume()
        scnTottusScanner.onResumeScanner()
        Log.d("998837", "onResume")
    }

    override fun onPause() {
        super.onPause()
        scnTottusScanner.onPauseScanner()
        Log.d("998837", "onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        scnTottusScanner.onDestroyScanner()
        Log.d("998837", "onDestroy")
    }
}
