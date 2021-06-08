package com.gs.gunsal.dataClass

import java.io.Serializable

data class WalkData(
    val user_id : List<WalkDate>
): Serializable

data class WalkDate(
    val water_data : List<WalkDataDetail>
): Serializable

data class WalkDataDetail(
    val step_count: Int,
    val kcal_consumed: Double,
    val memo: String
): Serializable

data class NewWalkData(
    val date: String,
    val step_count: Int,
    val kcal_consumed: Double,
    val memo: String
): Serializable