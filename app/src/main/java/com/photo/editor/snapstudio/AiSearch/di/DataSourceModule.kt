package com.muratozturk.openai_dall_e_2.di


import com.photo.editor.snapstudio.AiSearch.DallEService
import com.photo.editor.snapstudio.AiSearch.RemoteDataSource
import com.photo.editor.snapstudio.AiSearch.RemoteDateSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun provideRemoteDateSource(remoteService: DallEService): RemoteDataSource =
        RemoteDateSourceImpl(remoteService)
}