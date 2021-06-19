package com.gs.gunsal.dataClass

import java.io.Serializable

data class StretchData(
    val user_id : List<StretchDate>
): Serializable

data class StretchDate(
    val stretch_data : List<StretchDataDetail>
): Serializable

data class StretchDataDetail(
    val time: Int
): Serializable