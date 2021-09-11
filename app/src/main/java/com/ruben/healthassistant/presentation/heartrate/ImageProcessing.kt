package com.ruben.healthassistant.presentation.heartrate

import android.util.Log
import java.nio.ByteBuffer
import javax.inject.Inject

/**
 * Created by Ruben Quadros on 11/09/21
 **/
class ImageProcessing @Inject constructor() {

    private fun decodeYUV420SPtoRedSum(width: Int, height: Int,  byteBuffer: ByteBuffer): Int {
        val frameSize = width * height

        var sum = 0
            var j = 0
            var yp = 0
            while (j < height) {
                var uvp = frameSize + (j shr 1) * width
                var u = 0
                var v = 0
                var i = 0
                while (i < width) {
                    var y = (0xff and byteBuffer[yp].toInt()) - 16
                    if (y < 0) y = 0
                    if (i and 1 == 0 && uvp < byteBuffer.capacity()) {
                        v = (0xff and byteBuffer[uvp++].toInt()) - 128
                        u = (0xff and byteBuffer[uvp++].toInt()) - 128
                    }
                    val y1192 = 1192 * y
                    var r = y1192 + 1634 * v
                    var g = y1192 - 833 * v - 400 * u
                    var b = y1192 + 2066 * u
                    if (r < 0) r = 0 else if (r > 262143) r = 262143
                    if (g < 0) g = 0 else if (g > 262143) g = 262143
                    if (b < 0) b = 0 else if (b > 262143) b = 262143
                    val pixel =
                        -0x1000000 or (r shl 6 and 0xff0000) or (g shr 2 and 0xff00) or (b shr 10 and 0xff)
                    val red = pixel shr 16 and 0xff
                    sum += red
                    i++
                    yp++
                }
                j++
        }
        return sum
    }

    fun decodeYUV420SPtoRedAvg(width: Int, height: Int, byteBuffer: ByteBuffer?): Int {
        if (byteBuffer == null) return 0
        val frameSize = width * height
        val sum = decodeYUV420SPtoRedSum(width, height, byteBuffer)
        return sum / frameSize
    }
}