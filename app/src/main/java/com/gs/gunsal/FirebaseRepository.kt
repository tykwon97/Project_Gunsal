package com.gs.gunsal

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.gs.gunsal.dataClass.*
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object FirebaseRepository {
    //lateinit var uid: String
    val reference = FirebaseDatabase.getInstance().reference
    var ddd = 0.0

    interface OnWaterDataListener{
        fun onWaterDataCaught(waterDataDetail: WaterDataDetail)
    }
    var waterDataListener:OnWaterDataListener ?= null

    interface OnWalkingDataListener{
        fun onWalkDataCaught(walkDataDetail: WalkDataDetail)
    }
    var walkDataListener:OnWalkingDataListener ?= null

    interface OnBodyDataListener{
        fun onBodyDataCaught(bodyDataDetail: BodyDataDetail)
    }
    var bodyDataListener:OnBodyDataListener ?= null

    interface OnTotalDataListener{
        fun onTotalDataCaught(userData: UserData, bodyData: BodyDataDetail, waterData: WaterDataDetail, walkData: WalkDataDetail)
    }
    var totalDataListener:OnTotalDataListener ?= null

    interface OnUserDataListener{
        fun onUserDataCaught(userData: UserData)
    }
    var userDataListener: OnUserDataListener ?= null


////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////<User Data>////////////////////////////////////////////////////////////////

    fun enrollUser(userId: String, nickName: String?){
        val user : FUser = if(nickName != null) {
            FUser(nickName)
        } else{
            FUser("null")
        }
        reference.child("users").child(userId).setValue(user)
    }

    fun removeUser(uid: String) {
        reference.child("users").child(uid).removeValue()
        reference.child("data").child(uid).removeValue()
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////<Drinking Data>////////////////////////////////////////////////////////////

    private fun handleDrinkData(userId: String, date: String, drinkData: WaterDataDetail){
        reference.child("water_data").child(userId).child(date).get().addOnSuccessListener { snapShot->
            if(!snapShot.exists()) {
                val oldDrinkData = WaterDataDetail(-1, "", "")
                updateDrinkCallBack(userId, date, oldDrinkData, drinkData)
            }
            else {
                val quantity = snapShot.child("quantity").value.toString()
                val memo = snapShot.child("memo").value.toString()
                val recentTime = snapShot.child("recent_time").value.toString()
                val oldWaterData = WaterDataDetail(quantity.toInt(), memo, recentTime)
                //Log.d("getBodyData1","${oldWalkData.height}, ${oldBodyData.weight}")
                updateDrinkCallBack(userId, date, oldWaterData, drinkData)
            }
        }
    }

    fun getDrinkData(userId: String, date: String){
        val currentDate = getCurrentDate()
        reference.child("water_data").child(userId).child(date).get().addOnSuccessListener { snapShot ->
            if(!snapShot.exists()){
                val waterData = WaterDataDetail(-1, "ERROR", "00:00:00")
                waterDataListener!!.onWaterDataCaught(waterData)
            }
            else{
                val quantity = snapShot.child("quantity").value.toString()
                val memo = snapShot.child("memo").value.toString()
                val recentTime = snapShot.child("recent_time").value.toString()
                val waterData = WaterDataDetail(quantity.toInt(), memo, recentTime)
                waterDataListener!!.onWaterDataCaught(waterData)
            }
        }
    }

    private fun updateDrinkCallBack(
        userId: String,
        date: String,
        oldDrinkData: WaterDataDetail,
        newDrinkData: WaterDataDetail
    ) {
        if(oldDrinkData.quantity == -1){
            reference.child("water_data")
                .child(userId)
                .child(date)
                .setValue(newDrinkData)
        }
        else{
            var quantity = oldDrinkData.quantity + newDrinkData.quantity
            if(quantity <= 0) quantity = 0
            val updatedData = WaterDataDetail(quantity, newDrinkData.memo, newDrinkData.recent_time)
            reference.child("water_data")
                .child(userId)
                .child(date)
                .setValue(updatedData)
        }
    }

    fun addDrinkData(userId: String, quantity: Int, memo: String){
        val currentDate = getCurrentDate()
        val currentTime = getCurrentTime()
        val waterData = WaterDataDetail(quantity, memo, currentTime)
        handleDrinkData(userId, currentDate, waterData)
    }

    fun updateDrinkData(userId: String, date: String, time: String, quantity: Int, memo: String){
        val waterData = WaterDataDetail(quantity, memo, time)
        handleDrinkData(userId, date, waterData)
    }

    fun removeDrinkingData(userId: String, time: String){
        reference.child("water_data")
            .child(userId)
            .child(time)
            .removeValue()
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////<Walking Data>/////////////////////////////////////////////////////////////

    fun getWalkData(userId: String, date: String) {
        reference.child("walk_data").child(userId).child(date).get().addOnSuccessListener { snapShot->
            val stepCount = snapShot.child("step_count").value.toString()
            val memo = snapShot.child("memo").value.toString()
            val kcalConsumed = snapShot.child("kcal_consumed").value.toString()
            val walkData = WalkDataDetail(stepCount.toInt(), kcalConsumed.toDouble(), memo)
            walkDataListener!!.onWalkDataCaught(walkData)

        }
    }


    // 처음 add도 update로 대체, 데이터가 존재하지 않으면 자동으로 삽입되게 함
    // + stepCount가 어떤 식으로 들어오는 건지 궁금 (계속 누적되게 가져와지는건지)
    private fun handleWalkData(userId: String, date: String, walkData: WalkDataDetail){
        reference.child("walk_data").child(userId).child(date).get().addOnSuccessListener { snapShot->
            if(!snapShot.exists()) {
                val oldWalkData = WalkDataDetail(-1, -1.0, "")
                updateWalkCallBack(userId, date, oldWalkData, walkData)
            }
            else {
                val stepCount = snapShot.child("step_count").value.toString()
                val kcalConsumed = snapShot.child("kcal_consumed").value.toString()
                val memo = snapShot.child("memo").value.toString()
                val oldWalkData = WalkDataDetail(stepCount.toInt(), kcalConsumed.toDouble(), memo)
                //Log.d("getBodyData1","${oldWalkData.height}, ${oldBodyData.weight}")
                updateWalkCallBack(userId, date, oldWalkData, walkData)
            }
        }
    }

    private fun updateWalkCallBack(
        userId: String,
        date: String,
        oldWalkData: WalkDataDetail,
        newWalkData: WalkDataDetail
    ) {
        if(oldWalkData.step_count == -1){
            reference.child("walk_data")
                .child(userId)
                .child(date)
                .setValue(newWalkData)
        }
        else{
            var stepCount = oldWalkData.step_count + newWalkData.step_count
            var kcalConsumed = oldWalkData.kcal_consumed + newWalkData.kcal_consumed
            if(stepCount <= 0) stepCount = 0
            if(kcalConsumed <= 0) kcalConsumed = 0.0
            val sumData = WalkDataDetail(stepCount,
                kcalConsumed, newWalkData.memo)
            reference.child("walk_data")
                .child(userId)
                .child(date)
                .setValue(sumData)
        }
    }

    fun addWalkingData(userId: String, stepCount: Int, kcalConsumed: Double, memo: String){
        val currentDate = getCurrentDate()
        val walkData = WalkDataDetail(stepCount, kcalConsumed, memo)
        handleWalkData(userId, currentDate, walkData)
    }

    fun updateWalkingData(userId: String, date: String, stepCount: Int, kcalConsumed: Double, memo: String){
        val walkData = WalkDataDetail(stepCount, kcalConsumed, memo)
        handleWalkData(userId, date, walkData)
    }

    fun removeWalkingData(userId: String, date: String){
        reference.child("walk_data")
            .child(userId)
            .child(date)
            .removeValue()
    }



////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////<Body Data>////////////////////////////////////////////////////////////////////

    fun getBodyData(userId: String, date: String) {
        reference.child("body_data").child(userId).child(date).get().addOnSuccessListener { snapShot->
            val height = snapShot.child("height").value.toString()
            val weight = snapShot.child("weight").value.toString()
            val bodyData = BodyDataDetail(height = height.toDouble(), weight = weight.toDouble())
            bodyDataListener!!.onBodyDataCaught(bodyData)

        }
    }
    private fun handleBodyData(userId: String, date: String, bodyData: BodyDataDetail){
        reference.child("body_data").child(userId).child(date).get().addOnSuccessListener { snapShot->
            if(!snapShot.exists()) {
                val oldBodyData = BodyDataDetail(-1.0, -1.0)
                updateBodyCallBack(userId, date, oldBodyData, bodyData)
            }
            else {
                val weight = snapShot.child("weight").value.toString()
                val height = snapShot.child("height").value.toString()
                val oldBodyData = BodyDataDetail(weight.toDouble(), height.toDouble())
                        Log.d("getBodyData1","${oldBodyData.height}, ${oldBodyData.weight}")
                updateBodyCallBack(userId, date, oldBodyData, bodyData)
            }
        }
    }

    private fun updateBodyCallBack
                (userId: String,
                 date: String,
                 oldBodyData: BodyDataDetail,
                 newBodyData: BodyDataDetail) {
        if(oldBodyData.height == -1.0){
            reference.child("body_data")
                .child(userId)
                .child(date)
                .setValue(newBodyData)
        }
        else{
            reference.child("body_data")
                .child(userId)
                .child(date)
                .setValue(newBodyData)
        }
    }

    fun addBodyData(userId: String, height: Double, weight: Double){
        val currentDate = getCurrentDate()
        val bodyData = BodyDataDetail(height, weight)
        handleBodyData(userId, currentDate, bodyData)
    }

    fun updateBodyData(userId: String, date: String, height: Double, weight: Double){
        val bodyData = BodyDataDetail(height, weight)
        handleBodyData(userId, date, bodyData)
    }

    fun removeBodyData(userId: String, date: String){
        reference.child("body_data")
            .child(userId)
            .child(date)
            .removeValue()
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
    fun getUserData(user: FirebaseUser, param: OnUserDataListener){
    user.email?.let {
        reference.child("users").child(it).get().addOnSuccessListener { snapShot->
            val userData = UserData(user.email!!, user.displayName!!)
            userDataListener!!.onUserDataCaught(userData)
        }.addOnFailureListener {
            enrollUser(user.email.toString(), user.displayName.toString())
            val userData = UserData(user.email!!, user.displayName!!)
            userDataListener!!.onUserDataCaught(userData)
        }
    }
    }

    fun getTotalData(userId: String, date: String){
        reference.get().addOnSuccessListener { snapShot->
            val bodySnapShot = snapShot.child("body_data").child(userId).child(date)
            val userSnapShot = snapShot.child("users").child(userId)
            val waterSnapShot = snapShot.child("water_data").child(userId).child(date)
            val walkSnapShot = snapShot.child("walk_data").child(userId).child(date)
            val height = bodySnapShot.child("height").value.toString()
            val weight = bodySnapShot.child("weight").value.toString()
            val nickName = userSnapShot.child("nick_name").value.toString()
            val kcalConsumed = walkSnapShot.child("kcal_consumed").value.toString()
            val walkMemo = walkSnapShot.child("memo").value.toString()
            val stepCount = walkSnapShot.child("step_count").value.toString()
            val quantity = waterSnapShot.child("quantity").value.toString()
            val recentTime = waterSnapShot.child("recent_time").value.toString()
            val drinkMemo = waterSnapShot.child("memo").value.toString()

            val bodyData = BodyDataDetail(weight = weight.toDouble(), height = height.toDouble())
            val userData = UserData(userId, nickName)
            val waterData = WaterDataDetail(quantity.toInt(), drinkMemo, recentTime)
            val walkData = WalkDataDetail(stepCount.toInt(), kcalConsumed.toDouble(), walkMemo)

            totalDataListener!!.onTotalDataCaught(userData, bodyData, waterData, walkData)

        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

    fun getCurrentDate(): String{
         return LocalDateTime.now(ZoneOffset.of("+9")).format(DateTimeFormatter.ISO_LOCAL_DATE) as String
    }

    fun getCurrentTime(): String{
         val temp = LocalDateTime.now(ZoneOffset.of("+9")).format(DateTimeFormatter.ISO_LOCAL_TIME) as String
         var time = ""
         for(s in temp){
             if(s == '.') break;
             time += s
         }
        //Log.d("TIME::", time)
        return time
    }

    data class FUser(val nickName: String)

    data class FDrinkData(var quantity: Int, val memo: String)



}


