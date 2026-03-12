package dev.mikkkkkkka.whatiknow.di.impl

import dev.mikkkkkkka.whatiknow.di.AppModule
import dev.mikkkkkkka.whatiknow.di.DataModule
import dev.mikkkkkkka.whatiknow.di.DomainModule
import dev.mikkkkkkka.whatiknow.di.RepositoryModule

class AppModuleImpl(
    private val dataModule: DataModule,
    private val repositoryModule: RepositoryModule,
    private val domainModule: DomainModule,
) : AppModule,
    DataModule by dataModule,
    RepositoryModule by repositoryModule,
    DomainModule by domainModule

