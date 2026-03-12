package dev.mikkkkkkka.whatiknow

import android.app.Application
import dev.mikkkkkkka.whatiknow.di.AppModule
import dev.mikkkkkkka.whatiknow.di.impl.AppModuleImpl
import dev.mikkkkkkka.whatiknow.di.impl.DataModuleImpl
import dev.mikkkkkkka.whatiknow.di.impl.DomainModuleImpl
import dev.mikkkkkkka.whatiknow.di.impl.RepositoryModuleImpl

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

