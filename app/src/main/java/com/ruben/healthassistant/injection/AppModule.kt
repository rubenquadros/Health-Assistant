package com.ruben.healthassistant.injection

import com.ruben.healthassistant.core.AppCoroutineDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by Ruben Quadros on 11/09/21
 **/
@[Module InstallIn(SingletonComponent::class)]
internal object AppModule {

    @[Provides Singleton]
    fun provideCoroutineDispatcher(): AppCoroutineDispatcher = AppCoroutineDispatcher()
}