package com.gs.gunsal.dataClass

import java.io.Serializable

data class Data(
    val body_data: BodyData,
    val users: Users,
    val walk_data: WalkData,
    val water_data: WaterData
): Serializable