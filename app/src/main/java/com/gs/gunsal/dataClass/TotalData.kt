package com.gs.gunsal.dataClass

data class TotalData(
    val userId: String,
    val userData: UserData,
    val walkData: List<WalkDate>,
    val waterData: List<WaterDate>,
    val bodyData: List<BodyDate>
)