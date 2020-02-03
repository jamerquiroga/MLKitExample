
package com.jquiroga.mlkitexample.camera

import android.hardware.Camera
import com.google.android.gms.common.images.Size

/**
 * Almacena un tamaño de vista previa y un tamaño de imagen con la misma relación de aspecto correspondiente. Para
 * evitar imágenes de vista previa distorsionadas en algunos dispositivos, el tamaño de la imagen debe establecerse
 * en un tamaño que tenga la misma relación de aspecto que el tamaño de vista previa o la vista previa puede terminar
 * distorsionada. Si el tamaño de la imagen es nulo, entonces no hay tamaño de imagen con la misma relación de
 * aspecto que el tamaño de la vista previa.
 */
class CameraSizePair {
    val preview: Size
    val picture: Size?

    constructor(previewSize: Camera.Size, pictureSize: Camera.Size?) {
        preview = Size(previewSize.width, previewSize.height)
        picture = pictureSize?.let { Size(it.width, it.height) }
    }

    constructor(previewSize: Size, pictureSize: Size?) {
        preview = previewSize
        picture = pictureSize
    }
}
