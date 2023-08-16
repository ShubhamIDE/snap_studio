package com.photo.editor.snapstudio.AiSearch

import com.photo.editor.snapstudio.AiSearch.model.GeneratedImage
import com.photo.editor.snapstudio.AiSearch.model.RequestBody

class RemoteDateSourceImpl(private val remoteService: DallEService) : RemoteDataSource {
    override suspend fun generateImage(
        requestBody: RequestBody
    ): GeneratedImage {
        return remoteService.generateImage(requestBody)
    }
}