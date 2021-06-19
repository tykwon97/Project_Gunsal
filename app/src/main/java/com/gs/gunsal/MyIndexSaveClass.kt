package com.gs.gunsal

import android.content.Context
import android.content.SharedPreferences

class MyIndexSaveClass(context: Context) {
    private val prefsFilename = "prefs1" //파일 이름
    private val prefsKeyIndex = "myIndex1"
    private val prefs1:SharedPreferences = context.getSharedPreferences(prefsFilename, 0)  //저장된 값 불러오기
    var myIndex1:Int?  //이 변수로 get, set.
    get() = prefs1.getInt(prefsKeyIndex, 0) //반환
    set(value) = prefs1.edit().putInt(prefsKeyIndex, value!!).apply() //저장

}