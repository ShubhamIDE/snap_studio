package com.photo.editor.snapstudio.AiSearch

import com.photo.editor.snapstudio.AiSearch.model.GeneratedImage
import com.photo.editor.snapstudio.AiSearch.model.RequestBody

interface RemoteDataSource {
    suspend fun generateImage(requestBody: RequestBody): GeneratedImage
}