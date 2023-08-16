package com.photo.editor.snapstudio.AiSearch


import com.photo.editor.snapstudio.AiSearch.model.RequestBody
import javax.inject.Inject

class GenerateImageUseCase @Inject constructor(private val repository: DallERepository) {
    operator fun invoke(requestBody: RequestBody) = repository.generateImage(requestBody)
}