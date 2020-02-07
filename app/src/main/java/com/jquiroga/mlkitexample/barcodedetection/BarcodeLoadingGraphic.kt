
package com.jquiroga.mlkitexample.barcodedetection

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Point
import android.graphics.PointF
import com.jquiroga.mlkitexample.camera.GraphicOverlay

/** Dibuja el gráfico para indicar que el resultado del código de barras se está cargando.  */
internal class BarcodeLoadingGraphic(overlay: GraphicOverlay, private val loadingAnimator: ValueAnimator) :
    BarcodeGraphicBase(overlay) {

    private val boxClockwiseCoordinates: Array<PointF> = arrayOf(
            PointF(boxRect.left, boxRect.top),
            PointF(boxRect.right, boxRect.top),
            PointF(boxRect.right, boxRect.bottom),
            PointF(boxRect.left, boxRect.bottom)
    )
    private val coordinateOffsetBits: Array<Point> = arrayOf(
            Point(1, 0),
            Point(0, 1),
            Point(-1, 0),
            Point(0, -1)
    )
    private val lastPathPoint = PointF()

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        val boxPerimeter = (boxRect.width() + boxRect.height()) * 2
        val path = Path()
        // La distancia entre la esquina superior izquierda de la caja y el punto de inicio de la ruta de color blanco.
        var offsetLen = boxPerimeter * loadingAnimator.animatedValue as Float % boxPerimeter
        var i = 0
        while (i < 4) {
            val edgeLen = if (i % 2 == 0) boxRect.width() else boxRect.height()
            if (offsetLen <= edgeLen) {
                lastPathPoint.x = boxClockwiseCoordinates[i].x + coordinateOffsetBits[i].x * offsetLen
                lastPathPoint.y = boxClockwiseCoordinates[i].y + coordinateOffsetBits[i].y * offsetLen
                path.moveTo(lastPathPoint.x, lastPathPoint.y)
                break
            }

            offsetLen -= edgeLen
            i++
        }

        // Calcula la ruta en función del punto de inicio y la longitud de ruta determinados.
        var pathLen = boxPerimeter * 0.3f
        for (j in 0..3) {
            val index = (i + j) % 4
            val nextIndex = (i + j + 1) % 4
            // La longitud entre el punto final actual de la ruta y el siguiente punto de coordenadas del cuadro de retícula.
            val lineLen = Math.abs(boxClockwiseCoordinates[nextIndex].x - lastPathPoint.x) +
                    Math.abs(boxClockwiseCoordinates[nextIndex].y - lastPathPoint.y)
            if (lineLen >= pathLen) {
                path.lineTo(
                        lastPathPoint.x + pathLen * coordinateOffsetBits[index].x,
                        lastPathPoint.y + pathLen * coordinateOffsetBits[index].y
                )
                break
            }

            lastPathPoint.x = boxClockwiseCoordinates[nextIndex].x
            lastPathPoint.y = boxClockwiseCoordinates[nextIndex].y
            path.lineTo(lastPathPoint.x, lastPathPoint.y)
            pathLen -= lineLen
        }

        canvas.drawPath(path, pathPaint)
    }
}
