package com.photo.editor.snapstudio.AiSearch

import com.photo.editor.snapstudio.AiSearch.model.GeneratedImage
import com.photo.editor.snapstudio.AiSearch.model.RequestBody

import kotlinx.coroutines.flow.Flow

interface DallERepository {
    fun generateImage(requestBody: RequestBody): Flow<Resource<GeneratedImage>>
}