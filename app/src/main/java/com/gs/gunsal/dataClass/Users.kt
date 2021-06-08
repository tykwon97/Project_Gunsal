package com.gs.gunsal.dataClass

import java.io.Serializable

data class Users(
    val user_id : List<UserData>
): Serializable

data class UserData(
    val user_id: String,
    val nick_name: String
): Serializable
