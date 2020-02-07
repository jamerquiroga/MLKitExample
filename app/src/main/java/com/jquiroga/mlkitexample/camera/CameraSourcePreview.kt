
package com.jquiroga.mlkitexample.camera

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.FrameLayout
import com.google.android.gms.common.images.Size
import com.jquiroga.mlkitexample.R
import com.jquiroga.mlkitexample.utils.Utils
import java.io.IOException

/** Vista previa de la imagen de la cámara en la pantalla.  */
class CameraSourcePreview(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val surfaceView: SurfaceView = SurfaceView(context).apply {
        holder.addCallback(SurfaceCallback())
        addView(this)
    }
    private var graphicOverlay: GraphicOverlay? = null
    private var startRequested = false
    private var surfaceAvailable = false
    private var cameraSource: CameraSource? = null
    private var cameraPreviewSize: Size? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        graphicOverlay = findViewById(R.id.camera_preview_graphic_overlay)
    }

    @Throws(IOException::class)
    fun start(cameraSource: CameraSource) {
        this.cameraSource = cameraSource
        startRequested = true
        startIfReady()
    }

    fun stop() {
        cameraSource?.let {
            it.stop()
            cameraSource = null
            startRequested = false
        }
    }

    @Throws(IOException::class)
    private fun startIfReady() {
        if (startRequested && surfaceAvailable) {
            cameraSource?.start(surfaceView.holder)
            requestLayout()
            graphicOverlay?.let { overlay ->
                cameraSource?.let {
                    overlay.setCameraInfo(it)
                }
                overlay.clear()
            }
            startRequested = false
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val layoutWidth = right - left
        val layoutHeight = bottom - top

        cameraSource?.previewSize?.let { cameraPreviewSize = it }

        val previewSizeRatio = cameraPreviewSize?.let { size ->
            if (Utils.isPortraitMode(context)) {
                // La orientación natural de la cámara es horizontal, por lo que es necesario cambiar el ancho y la altura.
                size.height.toFloat() / size.width
            } else {
                size.width.toFloat() / size.height
            }
        } ?: layoutWidth.toFloat() / layoutHeight.toFloat()

        // Haga coincidir el ancho de la vista secundaria con su padre.
        val childHeight = (layoutWidth / previewSizeRatio).toInt()
        if (childHeight <= layoutHeight) {
            for (i in 0 until childCount) {
                getChildAt(i).layout(0, 0, layoutWidth, childHeight)
            }
        } else {
            // Cuando la vista secundaria es demasiado alta para ajustarse a su elemento primario: Si la vista
            // secundaria es un contenedor de vista de superposición estática (contiene vistas como el chip de
            // solicitud inferior), le aplicamos el tamaño de la vista primaria. De lo contrario, compensamos
            // la posición superior / inferior por igual para colocarla en el centro del padre.
            val excessLenInHalf = (childHeight - layoutHeight) / 2
            for (i in 0 until childCount) {
                val childView = getChildAt(i)
                when (childView.id) {
                    R.id.static_overlay_container -> {
                        childView.layout(0, 0, layoutWidth, layoutHeight)
                    }
                    else -> {
                        childView.layout(
                            0, -excessLenInHalf, layoutWidth, layoutHeight + excessLenInHalf)
                    }
                }
            }
        }

        try {
            startIfReady()
        } catch (e: IOException) {
            Log.e(TAG, "Could not start camera source.", e)
        }
    }

    private inner class SurfaceCallback : SurfaceHolder.Callback {
        override fun surfaceCreated(surface: SurfaceHolder) {
            surfaceAvailable = true
            try {
                startIfReady()
            } catch (e: IOException) {
                Log.e(TAG, "Could not start camera source.", e)
            }
        }

        override fun surfaceDestroyed(surface: SurfaceHolder) {
            surfaceAvailable = false
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    }

    companion object {
        private const val TAG = "CameraSourcePreview"
    }
}
