package com.rakasiwi.barandqrcodescanner.ui.Scan

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.rakasiwi.barandqrcodescanner.memory.ResultMemory

class ScannerViewModel @ViewModelInject constructor(): ViewModel() {
    val result : LiveData<String> get() = ResultMemory.resultValue

}