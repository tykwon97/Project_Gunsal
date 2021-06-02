package com.gs.gunsal.dataClass

import java.io.Serializable

data class BodyData(
    val user_id : List<BodyDate>
): Serializable

data class BodyDate(
    val water_data : List<BodyDataDetail>
): Serializable

data class BodyDataDetail(
    val height: Double,
    val weight: Double
): Serializable