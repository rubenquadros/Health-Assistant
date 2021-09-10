package com.ruben.healthassistant.utility

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar

/**
 * Created by Ruben Quadros on 10/09/21
 **/
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun AppCompatActivity.setupToolbar(toolbar: Toolbar, shouldShowBack: Boolean) {
    setSupportActionBar(toolbar)
    if (shouldShowBack) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }
}

fun View.showSnack(message: String, action: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE).apply {
        setAction(action) {
            dismiss()
        }
        show()
    }
}