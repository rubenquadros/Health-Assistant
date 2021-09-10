package com.ruben.healthassistant.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.ruben.healthassistant.R
import com.ruben.healthassistant.databinding.ActivityHomeBinding
import com.ruben.healthassistant.presentation.base.BaseActivity
import com.ruben.healthassistant.utility.showToast

class HomeActivity : BaseActivity() {
    private lateinit var _binding: ActivityHomeBinding
    private var _isQuitApp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _binding.homeHeartRateBtn.setOnClickListener {
            startActivity(Intent(this, HeartRateMonitorActivity::class.java))
        }
    }

    override fun onBackPressed() {
        if(_isQuitApp) {
            super.onBackPressed()
            return
        }
        _isQuitApp = true
        this.showToast(getString(R.string.home_back_pressed))
        Handler(Looper.getMainLooper()).postDelayed({ _isQuitApp = false }, 2000)

    }
}