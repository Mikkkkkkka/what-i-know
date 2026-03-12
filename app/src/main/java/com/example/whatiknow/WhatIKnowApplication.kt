package com.example.whatiknow

import android.app.Application
import com.example.whatiknow.di.AppContainer

class WhatIKnowApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        container = AppContainer(applicationContext)
    }

    lateinit var container: AppContainer
}