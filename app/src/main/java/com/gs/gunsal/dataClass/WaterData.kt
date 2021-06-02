package com.gs.gunsal.dataClass

import java.io.Serializable

data class WaterData(
    val user_id : List<WaterDate>
): Serializable

data class WaterDate(
    val water_data : List<WaterDataDetail>
): Serializable

data class WaterDataDetail(
    val quantity: Int,
    val memo: String,
    val recent_time: String
): Serializable