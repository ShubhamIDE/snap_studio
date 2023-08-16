package com.photo.editor.snapstudio.AiSearch.model


import com.google.gson.annotations.SerializedName

data class RequestBody(
    @SerializedName("n")
    val n: Int,
    @SerializedName("prompt")
    val prompt: String,
    @SerializedName("size")
    val size: String
)