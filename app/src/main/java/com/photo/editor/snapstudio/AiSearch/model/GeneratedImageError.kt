package com.photo.editor.snapstudio.AiSearch.model

import com.google.gson.annotations.SerializedName

data class GeneratedImageError(
    @SerializedName("error")
    val error: Error
)