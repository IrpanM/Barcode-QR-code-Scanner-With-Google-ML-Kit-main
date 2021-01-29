package com.rakasiwi.barandqrcodescanner.memory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object ResultMemory {
    private val _resultValue = MutableLiveData<String>()
    val resultValue : LiveData<String> get() = _resultValue


    fun setValue(value:String){
        _resultValue.value = value
    }
}