package com.photo.editor.snapstudio.AiSearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.photo.editor.snapstudio.AiSearch.Constants.SIZE_1024
import com.photo.editor.snapstudio.AiSearch.Constants.SIZE_256
import com.photo.editor.snapstudio.AiSearch.Constants.SIZE_512
import com.photo.editor.snapstudio.AiSearch.model.GeneratedImage
import com.photo.editor.snapstudio.AiSearch.model.RequestBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenerateImageViewModel @Inject constructor(private val generateImageUseCase: GenerateImageUseCase) :
    ViewModel() {


    private val _state = MutableStateFlow<Resource<GeneratedImage>?>(null)
    val state = _state.asStateFlow()

    fun generateImage(prompt: String, n: Int, size: Sizes) = viewModelScope.launch {

        val sizeString = when (size) {
            Sizes.SIZE_256 -> SIZE_256
            Sizes.SIZE_512 -> SIZE_512
            Sizes.SIZE_1024 -> SIZE_1024
        }

        generateImageUseCase(RequestBody(n, prompt, sizeString)).collect {
            _state.emit(it)
        }
    }


}