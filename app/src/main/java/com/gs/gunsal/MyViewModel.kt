package com.gs.gunsal

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    val selectednum = MutableLiveData<Int>()
    fun setLiveData(num: Int) {
        selectednum.value = num
    }
}