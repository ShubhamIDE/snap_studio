package com.photo.editor.snapstudio.AiSearch


import com.photo.editor.snapstudio.AiSearch.Constants.GENERATE_IMAGE
import com.photo.editor.snapstudio.AiSearch.model.GeneratedImage
import com.photo.editor.snapstudio.AiSearch.model.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface DallEService {

    @POST(GENERATE_IMAGE)
    suspend fun generateImage(@Body body: RequestBody): GeneratedImage

}