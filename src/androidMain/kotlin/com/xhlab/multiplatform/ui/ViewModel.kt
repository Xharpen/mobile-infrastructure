package com.xhlab.multiplatform.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

actual open class ViewModel : ViewModel() {
    actual val scope = viewModelScope
}