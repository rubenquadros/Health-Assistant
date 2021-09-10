package com.ruben.healthassistant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ruben.healthassistant.R
import com.ruben.healthassistant.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(_binding.root)
    }
}