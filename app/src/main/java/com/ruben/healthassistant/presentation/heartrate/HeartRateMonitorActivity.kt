package com.ruben.healthassistant.presentation.heartrate

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.MainThread
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ruben.healthassistant.R
import com.ruben.healthassistant.core.BaseActivity
import com.ruben.healthassistant.databinding.ActivityHeartRateMonitorBinding
import com.ruben.healthassistant.presentation.dialog.PermissionDialogFragment
import com.ruben.healthassistant.utility.Constants
import com.ruben.healthassistant.utility.showSnack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class HeartRateMonitorActivity : BaseActivity() {

    companion object {
        private const val TAG = "HeartRateMonitor"
    }

    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            initReader()
        } else {
            _binding.heartMonitorParent.showSnack(getString(R.string.heart_monitor_camera_permission), getString(R.string.all_ok))
        }
    }


    private lateinit var _binding: ActivityHeartRateMonitorBinding
    private val heartRateViewModel by viewModels<HeartRateViewModel>()
    private var shouldProcess = true
    private var cameraProvider: ProcessCameraProvider? = null
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK
    private var imageAnalyzer: ImageAnalysis? = null
    private lateinit var cameraExecutor: ExecutorService
    private var camera: Camera? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHeartRateMonitorBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        setupAppbar(
            _binding.heartMonitorAppBar.toolbar,
            true,
            _binding.heartMonitorAppBar.toolbarTitleTv,
            getString(R.string.heart_monitor_title)
        )
        observeData()
        checkPermissions()
    }

    private fun observeData() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    heartRateViewModel.shouldProcess.collect {
                        shouldProcess = it
                    }
                }

                launch {
                    heartRateViewModel.beatsPerMin.collect {
                        _binding.heartMonitorValueTv.text = getString(R.string.heart_monitor_reading, it)
                    }
                }
            }
        }
    }

    private fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                initReader()
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(
                Manifest.permission.CAMERA
            ) -> {
                PermissionDialogFragment.newInstance().apply {
                    show(supportFragmentManager, Constants.PERMISSION_FRAGMENT_TAG)
                }
            }
            else -> {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun initReader() {
        cameraExecutor = Executors.newSingleThreadExecutor()

        fun hasCameraBack(): Boolean =
            cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) ?: false

        fun hasCameraFront(): Boolean =
            cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) ?: false

        fun enableFlash(camera: Camera) {
            if (camera.cameraInfo.hasFlashUnit()) {
                camera.cameraControl.enableTorch(true)
            }
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {

            cameraProviderFuture.get()?.let { cameraProvider ->
                this.cameraProvider = cameraProvider
                val lensFacing = when {
                    hasCameraBack() -> CameraSelector.LENS_FACING_BACK
                    hasCameraFront() -> CameraSelector.LENS_FACING_FRONT
                    else -> {
                        Log.d(TAG, "No camera available")
                        return@Runnable
                    }
                }

                val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

                imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { cameraImage->
                            if (shouldProcess) {
                                processData(cameraImage.width, cameraImage.height, cameraImage.image?.planes?.get(0)?.buffer)
                            } else {
                                shouldProcess = true
                            }
                            cameraImage.close()
                        })
                    }

                cameraProvider.unbindAll()

                try {
                    this.camera = cameraProvider.bindToLifecycle(
                        this as LifecycleOwner,
                        cameraSelector,
                        imageAnalyzer
                    )
                    this.camera?.let {
                        enableFlash(it)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Camera use case binding failed ${e.message}")
                }
            }

        }, ContextCompat.getMainExecutor(this))
    }

    @MainThread
    private fun processData(width: Int, height: Int, byteBuffer: ByteBuffer?) {
        heartRateViewModel.processImageData(width, height, byteBuffer)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}