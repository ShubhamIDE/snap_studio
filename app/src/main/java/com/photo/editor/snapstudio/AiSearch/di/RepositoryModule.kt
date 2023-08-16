package com.muratozturk.openai_dall_e_2.di


import com.photo.editor.snapstudio.AiSearch.DallERepository
import com.photo.editor.snapstudio.AiSearch.DallERepositoryImpl
import com.photo.editor.snapstudio.AiSearch.RemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideDallERepository(
        remoteDataSource: RemoteDataSource
    ): DallERepository =
        DallERepositoryImpl(remoteDataSource)
}