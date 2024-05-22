package com.MemeMaster.MemeMaster.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class ImageViewModel : ViewModel() {
    var pickedImageUrl = mutableStateOf<String?>(null)
    var pickedImageSize = mutableStateOf(200.dp)
    var scale = mutableFloatStateOf(1f)
    var offsetX = mutableFloatStateOf(0f)
    var offsetY = mutableFloatStateOf(0f)
    var confirmedScale by mutableFloatStateOf(1f)
    var confirmedOffsetX by mutableFloatStateOf(0f)
    var confirmedOffsetY by mutableFloatStateOf(0f)
}