package com.example.whatiknow.di.impl

import com.example.whatiknow.di.AppModule
import com.example.whatiknow.di.DataModule
import com.example.whatiknow.di.DomainModule
import com.example.whatiknow.di.RepositoryModule

class AppModuleImpl(
    private val dataModule: DataModule,
    private val repositoryModule: RepositoryModule,
    private val domainModule: DomainModule,
) : AppModule,
    DataModule by dataModule,
    RepositoryModule by repositoryModule,
    DomainModule by domainModule
