package com.gs.gunsal.dataClass

import java.io.Serializable

data class Users(
    val user_id : List<UserData>
): Serializable

data class UserData(
    val name: String,
    val nickName: String
): Serializable
