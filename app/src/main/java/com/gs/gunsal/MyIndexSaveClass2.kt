package com.gs.gunsal

import android.content.Context
import android.content.SharedPreferences

class MyIndexSaveClass2(context: Context) {
    private val prefsFilename = "prefs2" //파일 이름
    private val prefsKeyIndex = "myIndex2"
    private val prefs2:SharedPreferences = context.getSharedPreferences(prefsFilename, 0)  //저장된 값 불러오기
    var myIndex2:Int?  //이 변수로 get, set.
    get() = prefs2.getInt(prefsKeyIndex, 0) //반환
    set(value) = prefs2.edit().putInt(prefsKeyIndex, value!!).apply() //저장

}