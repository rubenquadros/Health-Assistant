package com.ruben.healthassistant.presentation.base

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.ruben.healthassistant.utility.setupToolbar

/**
 * Created by Ruben Quadros on 10/09/21
 **/
abstract class BaseActivity: AppCompatActivity() {

    fun setupAppbar(toolbar: Toolbar, shouldShowBack: Boolean, toolbarTitleTv: TextView, title: String) {
        toolbarTitleTv.text = title
        setupToolbar(toolbar, true)
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onSupportNavigateUp()
        onBackPressed()
        return true
    }
}