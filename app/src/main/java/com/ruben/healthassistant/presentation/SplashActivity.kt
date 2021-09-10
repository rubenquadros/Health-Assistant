package com.ruben.healthassistant.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.ruben.healthassistant.presentation.base.BaseActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}