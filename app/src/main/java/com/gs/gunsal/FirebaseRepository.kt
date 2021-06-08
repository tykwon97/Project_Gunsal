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
        fun onWalkListCaught(newWalkData: ArrayList<NewWalkData>)
    }
    var walkDataListener:OnWalkingDataListener ?= null

    interface OnBodyDataListener{
        fun onBodyDataCaught(bodyDataDetail: BodyDataDetail)
    }
    var bodyDataListener:OnBodyDataListener ?= null

    interface OnTotalDataListener{
        fun onTotalDataCaught(userData: UserData, bodyData: BodyDataDetail, waterData: WaterDataDetail, walkData: WalkDataDetail)
        //fun onTotalDataFailed() // 이 함수가 호출될 때면 이미 데이터가 새로 추가된 시점이므로 getTotalData를 다시 호출해주면 됨
    }
    var totalDataListener:OnTotalDataListener ?= null

    interface OnUserDataListener{
        fun onUserDataCaught(userData: UserData, isFirst: Boolean)
        fun onUserDataUncaught(user: FirebaseUser)
    }
    var userDataListener: OnUserDataListener ?= null


////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////<User Data>////////////////////////////////////////////////////////////////

    fun enrollUser(userId: String, nickName: String){
        val user = UserData(userId, nickName)
        reference.child("users").child(userId).setValue(user)
    }

    fun removeUser(uid: String) {
        reference.child("users").child(uid).removeValue()
        reference.child("walk_data").child(uid).removeValue()
        reference.child("water_data").child(uid).removeValue()
        reference.child("body_data").child(uid).removeValue()
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

    fun getWalkDataList(userId: String){
        val today = getCurrentDate()

        reference.child("walk_data").child(userId).get().addOnSuccessListener { snapShot->
            val newWalDataList = ArrayList<NewWalkData>()
            val list = snapShot.children.forEach {
                val date = it.key.toString()
                val stepCount = it.child("step_count").value.toString()
                val memo = it.child("memo").value.toString()
                val kcalConsumed = it.child("kcal_consumed").value.toString()
                val newWalkData = NewWalkData(date, stepCount.toInt(), kcalConsumed.toDouble(), memo)
                newWalDataList.add(newWalkData)
            }
            walkDataListener!!.onWalkListCaught(newWalDataList)
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
            var stepCount = newWalkData.step_count
            var kcalConsumed = newWalkData.kcal_consumed
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
    fun getUserInfo(user: FirebaseUser){
        reference.child("users").get().addOnSuccessListener { snapShot->
            val userId = snapShot.child(user.uid).child("user_id").value.toString()
            val nickName = snapShot.child(user.uid).child("nick_name").value.toString()
            Log.d("getUserInfo", "$userId, $nickName")
            if(userId == "null" || nickName == "null"){
                enrollUser(user.uid, user.displayName.toString())
                val userData = UserData(userId, nickName)
                userDataListener!!.onUserDataCaught(userData, true)
            }
            else {
                Log.d("getUserInfo else", "$userId, $nickName")
                val userData = UserData(userId, nickName)
                userDataListener!!.onUserDataCaught(userData, false)
            }
        }.addOnFailureListener {
            enrollUser(user.uid, user.displayName.toString())
            val userData = UserData(user.uid, user.displayName!!)
            userDataListener!!.onUserDataUncaught(user)
        }
    }

    fun getUserInfoByID(userId: String){
        reference.child("users").get().addOnSuccessListener { snapShot->
            val userId = snapShot.child(userId).child("user_id").value.toString()
            val nickName = snapShot.child(userId).child("nick_name").value.toString()
            Log.d("getUserInfo", "$userId, $nickName")
            val userData = UserData(userId, nickName)
            userDataListener!!.onUserDataCaught(userData, false)
        }
    }


    fun getTotalData(userId: String, date: String) {
        reference.get().addOnSuccessListener { snapShot->
            //Log.d("snapshot.value",snapShot.value.toString())
            val dataArray = ArrayList<String>()
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
            dataArray.apply {
                add(height)
                add(weight)
                add(nickName)
                add(kcalConsumed)
                add(walkMemo)
                add(stepCount)
                add(quantity)
                add(recentTime)
                add(drinkMemo)
            }
            var i = 0
            val nullIndexArray = ArrayList<Int>()
            for(s in dataArray){
                if(s == "null" || s == "NULL" || s == null){
                    nullIndexArray.add(i)
                    Log.e("getTotalData", "NULL index: $i")
                }
                i += 1
            }
            if(nullIndexArray.size == 0) {
                Log.d("getTotalData", "dataLoad SUCCESS")
                val bodyData = BodyDataDetail(weight = weight.toDouble(), height = height.toDouble())
                val userData = UserData(userId, nickName)
                val waterData = WaterDataDetail(quantity.toInt(), drinkMemo, recentTime)
                val walkData = WalkDataDetail(stepCount.toInt(), kcalConsumed.toDouble(), walkMemo)
                totalDataListener!!.onTotalDataCaught(userData, bodyData, waterData, walkData)
            }
            else{
                val time = getCurrentTime()
                Log.e("getTotalData", "dataLoadFAIL, System progress Inserting default data...")
                var flagBody = true
                var flagWalk = true
                var flagWater = true
                for(index in nullIndexArray){
                    when(index){
                        0, 1 -> {
                            if(flagBody){
                                Log.d("getTotalData", "updateBodyData")
                                updateBodyData(userId, date ,0.0, 0.0)
                                flagBody = false
                            }
                        }
                        2->{}
                        3, 4, 5 -> {
                            if(flagWalk) {
                                Log.d("getTotalData", "updateWalkingData")
                                updateWalkingData(userId, date, 0, 0.0, "")
                                flagWalk = false
                            }
                        }
                        6, 7, 8 -> {
                            if(flagWater) {
                                Log.d("getTotalData", "updateDrinkData")
                                updateDrinkData(userId, date, time, 0, "")
                                flagWater = false
                            }
                        }
                    }
                }
                getTotalData(userId, date)
            }
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




}


