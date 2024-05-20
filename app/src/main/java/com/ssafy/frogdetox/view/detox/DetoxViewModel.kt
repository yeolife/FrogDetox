package com.ssafy.frogdetox.view.detox

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DetoxViewModel : ViewModel(){
    private val _hour = MutableLiveData<Int>().apply {
        value = 11
    }
    private val _minute = MutableLiveData<Int>().apply {
        value = 0
    }
    val hour : LiveData<Int>
        get() = _hour

    val minute : LiveData<Int>
        get() = _minute

    fun setHour(h : Int){
        _hour.value = h
    }

    fun setMinute(m : Int){
        _minute.value = m
    }
}