package com.photo.editor.snapstudio.AiSearch



import com.photo.editor.snapstudio.AiSearch.model.RequestBody
import kotlinx.coroutines.flow.flow

class DallERepositoryImpl(
    private val remoteDataSource: RemoteDataSource
) : DallERepository {
    override fun generateImage(
        requestBody: RequestBody
    ) = flow {
        emit(Resource.Loading)

        try {
            val response = remoteDataSource.generateImage(requestBody)
            emit(Resource.Success(response))
        } catch (t: Throwable) {
            emit(Resource.Error(t))
        }

    }

}