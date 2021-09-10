package com.ruben.healthassistant.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.ruben.healthassistant.R
import com.ruben.healthassistant.databinding.ActivityHeartRateMonitorBinding
import com.ruben.healthassistant.presentation.base.BaseActivity
import com.ruben.healthassistant.presentation.dialog.PermissionDialogFragment
import com.ruben.healthassistant.utility.Constants
import com.ruben.healthassistant.utility.showSnack

class HeartRateMonitorActivity : BaseActivity() {

    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            initReader()
        } else {
            _binding.heartMonitorParent.showSnack(getString(R.string.heart_monitor_camera_permission), getString(R.string.all_ok))
        }
    }


    private lateinit var _binding: ActivityHeartRateMonitorBinding

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
        checkPermissions()
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

    private fun initReader() {

        fun setupCamera() {

        }
    }
}