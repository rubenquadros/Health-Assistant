package com.ruben.healthassistant.presentation.heartrate

import androidx.lifecycle.ViewModel
import com.ruben.healthassistant.data.Type
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.ByteBuffer
import javax.inject.Inject

/**
 * Created by Ruben Quadros on 11/09/21
 **/
@HiltViewModel
class HeartRateViewModel @Inject constructor(
    private val imageProcessing: ImageProcessing
): ViewModel() {

    private var beatsIndex = 0
    private val beatsArraySize = 9
    private val beatsArray = IntArray(beatsArraySize)
    private var averageIndex = 0
    private val averageArraySize = 10
    private val averageArray = IntArray(averageArraySize)
    private var currentType = Type.GREEN
    private var beats = 0.0
    private var startTime: Long = 0

    private val _shouldProcess: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val shouldProcess = _shouldProcess.asStateFlow()

    private val _beatsPerMin: MutableStateFlow<Int> = MutableStateFlow(0)
    val beatsPerMin = _beatsPerMin.asStateFlow()

    fun processImageData(width: Int, height: Int, byteBuffer: ByteBuffer?) {
        val imgAvg = imageProcessing.decodeYUV420SPtoRedAvg(width, height, byteBuffer)
        if (imgAvg == 0 || imgAvg == 255) {
            _shouldProcess.value = false
            return
        }

        var averageArrayAvg = 0
        var averageArrayCnt = 0
        for (i in averageArray.indices) {
            if (averageArray[i] > 0) {
                averageArrayAvg += averageArray[i]
                averageArrayCnt++
            }
        }

        val rollingAverage = if (averageArrayCnt > 0) averageArrayAvg / averageArrayCnt else 0
        var newType: Type = currentType
        if (imgAvg < rollingAverage) {
            newType = Type.RED
            if (newType != currentType) {
                beats++
            }
        } else if (imgAvg > rollingAverage) {
            newType = Type.GREEN
        }

        if (averageIndex == averageArraySize) averageIndex = 0
        averageArray[averageIndex] = imgAvg
        averageIndex++

        // Transitioned from one state to another to the same
        if (newType != currentType) {
            currentType = newType
        }

        val endTime = System.currentTimeMillis()
        val totalTimeInSecs: Double =
            (endTime - startTime) / 1000.0
        if (totalTimeInSecs >= 10) {
            val bps: Double = beats / totalTimeInSecs
            val dpm = (bps * 60.0).toInt()
            if (dpm < 30 || dpm > 180) {
                startTime = System.currentTimeMillis()
                beats = 0.0
                _shouldProcess.value = false
                return
            }

            if (beatsIndex == beatsArraySize) beatsIndex = 0
            beatsArray[beatsIndex] = dpm
            beatsIndex++
            var beatsArrayAvg = 0
            var beatsArrayCnt = 0
            for (i in beatsArray.indices) {
                if (beatsArray[i] > 0) {
                    beatsArrayAvg += beatsArray[i]
                    beatsArrayCnt++
                }
            }
            val beatsAvg = beatsArrayAvg / beatsArrayCnt
            _beatsPerMin.value = beatsAvg
            startTime = System.currentTimeMillis()
            beats = 0.0
        }
        _shouldProcess.value = false
    }
}