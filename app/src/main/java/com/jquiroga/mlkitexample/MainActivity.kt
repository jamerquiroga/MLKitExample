package com.jquiroga.mlkitexample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jquiroga.mlkitexample.features.ScanBarcode2Activity
import com.jquiroga.mlkitexample.features.ScanBarcodeActivity
import com.jquiroga.mlkitexample.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnBarCodeScanner.setOnClickListener {
            val intent = Intent(this, ScanBarcodeActivity::class.java)
            startActivity(intent)
        }

        btnRecognizeText.setOnClickListener {
            val intent = Intent(this, ScanBarcode2Activity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!Utils.allPermissionsGranted(this)) {
            Utils.requestRuntimePermissions(this)
        }
    }
}
