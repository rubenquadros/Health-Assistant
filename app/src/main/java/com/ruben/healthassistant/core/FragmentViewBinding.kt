package com.ruben.healthassistant.core

import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by Ruben Quadros on 11/09/21
 **/
fun <T> Fragment.viewLifecycle(): ReadWriteProperty<Fragment, T> =
    object : ReadWriteProperty<Fragment, T>, DefaultLifecycleObserver {

        private var binding: T? = null

        init {
            this@viewLifecycle
                .viewLifecycleOwnerLiveData
                .observe(this@viewLifecycle, Observer { owner: LifecycleOwner? ->
                    owner?.lifecycle?.addObserver(this)
                })
        }

        override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
            return this.binding ?: error("Called before onCreateView or after onDestroyView")
        }

        override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
            this.binding = value
        }

        override fun onDestroy(owner: LifecycleOwner) {
            this.binding = null
        }
    }
