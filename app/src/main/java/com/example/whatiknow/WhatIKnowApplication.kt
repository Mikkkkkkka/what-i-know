package com.example.whatiknow

import android.app.Application
import com.example.whatiknow.di.AppModule
import com.example.whatiknow.di.impl.AppModuleImpl
import com.example.whatiknow.di.impl.DataModuleImpl
import com.example.whatiknow.di.impl.DomainModuleImpl
import com.example.whatiknow.di.impl.RepositoryModuleImpl

class WhatIKnowApplication : Application() {
    private fun initializeAppModule() {
        val dataModule = DataModuleImpl(applicationContext)
        val repositoryModule = RepositoryModuleImpl()
        val domainModule = DomainModuleImpl()

        appModule = AppModuleImpl(
            dataModule,
            repositoryModule,
            domainModule
        )
    }

    override fun onCreate() {
        super.onCreate()
        initializeAppModule()
    }

    companion object {
        lateinit var appModule: AppModule
    }
}
