package com.gs.gunsal

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.gs.gunsal.dataClass.*
import java.time.LocalDate
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
    var waterDataListener: OnWaterDataListener ?= null

    interface OnWalkingDataListener{
        fun onWalkDataCaught(walkDataDetail: WalkDataDetail)
    }
    var walkDataListener: OnWalkingDataListener ?= null

    interface OnBodyDataListener{
        fun onBodyDataCaught(bodyDataDetail: BodyDataDetail)
    }
    var bodyDataListener: OnBodyDataListener ?= null

    interface OnUserDataListener{
        fun onUserDataCaught(userData: UserData, isFirst: Boolean)
        fun onUserDataUncaught(user: FirebaseUser)
    }
    var userDataListener: OnUserDataListener ?= null

    interface OnStretchDataListener{
        fun onStretchDataCaught(stretchDataDetail: StretchDataDetail)
    }
    var stretchDataListener: OnStretchDataListener ?= null

    interface OnWalkWeekDataListener{
        fun onWalkWeekDataCaught(weekWalkData: ArrayList<Int>, dayOfWeek: Int)
    }
    var walkWeekDataListener: OnWalkWeekDataListener ?= null

    interface OnWaterWeekDataListener{
        fun onWaterWeekDataCaught(weekWaterData: ArrayList<Int>, dayOfWeek: Int)
    }
    var waterWeekDataListener: OnWaterWeekDataListener ?= null

    interface  OnStretchWeekDataListener{
        fun onStretchWeekDataCaught(weekStretchData: ArrayList<Int>, dayOfWeek: Int)
    }
    var stretchWeekDataListener: OnStretchWeekDataListener ?= null

    interface OnTotalDataListener{
        fun onTotalDataCaught(userData: UserData, bodyData: BodyDataDetail, waterData: WaterDataDetail,
                              walkData: WalkDataDetail, stretchData: StretchDataDetail)
        //fun onTotalDataFailed() // 이 함수가 호출될 때면 이미 데이터가 새로 추가된 시점이므로 getTotalData를 다시 호출해주면 됨
    }
    var totalDataListener:OnTotalDataListener ?= null

    interface OnTotalMonthListener{
        fun onTotalMonthCaught(ratingArray: ArrayList<Rating>)
    }
    var totalMonthListener: OnTotalMonthListener ?= null


////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////<User Data>////////////////////////////////////////////////////////////////

    fun enrollUser(userId: String, nickName: String, age: Int){
        val user = UserData(userId, nickName, age)
        reference.child("users").child(userId).setValue(user)
    }

    fun removeUser(uid: String) {
        reference.child("users").child(uid).removeValue()
        reference.child("walk_data").child(uid).removeValue()
        reference.child("water_data").child(uid).removeValue()
        reference.child("body_data").child(uid).removeValue()
        reference.child("stretch_data").child(uid).removeValue()
    }

    fun updateUserNickName(userId: String, nickName: String){
        reference.child("users").child(userId).child("nick_name").setValue(nickName)
    }

    fun updateUserAge(userId: String, age: Int) {
        reference.child("users").child(userId).child("age").setValue(age)
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

    fun getWeekDrinkData(userId: String, lastDate: LocalDate) {
        reference.get().addOnSuccessListener { snapShot ->
            val userWaterData = snapShot.child("water_data").child(userId)
            val tempWeekData = ArrayList<Int>()
            val dayOfWeek = lastDate.dayOfWeek.value // 월(1) ~ 일(7)
            for(i in 0 until 7) {
                val date = lastDate.minusDays(i.toLong()).format(DateTimeFormatter.ISO_LOCAL_DATE) as String
                if(userWaterData.child(date).value == null || userWaterData.child(date).value.toString() == "null"){
                    tempWeekData.add(0)
                }
                else{
                    val quantity = userWaterData.child(date).child("quantity").value.toString().toInt()
                    tempWeekData.add(quantity)
                }
            }
            //val walkWeekData = tempWeekData.subList(tempWeekData.lastIndex, 0) as ArrayList<Double>
            waterWeekDataListener!!.onWaterWeekDataCaught(tempWeekData.reversed() as ArrayList<Int>, dayOfWeek)

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

    fun getWeekWalkData(userId: String, lastDate: LocalDate) {
        reference.get().addOnSuccessListener { snapShot ->
            val userWalkData = snapShot.child("walk_data").child(userId)
            val tempWeekData = ArrayList<Int>()
            val dayOfWeek = lastDate.dayOfWeek.value // 월(1) ~ 일(7)
            for(i in 0 until 7) {
                val date = lastDate.minusDays(i.toLong()).format(DateTimeFormatter.ISO_LOCAL_DATE) as String
                if(userWalkData.child(date).value == null || userWalkData.child(date).value.toString() == "null"){
                    tempWeekData.add(0)
                }
                else{
                    val stepCount = userWalkData.child(date).child("step_count").value.toString().toInt()
                    tempWeekData.add(stepCount)
                }
            }
            //val walkWeekData = tempWeekData.subList(tempWeekData.lastIndex, 0) as ArrayList<Double>
            walkWeekDataListener!!.onWalkWeekDataCaught(tempWeekData.reversed() as ArrayList<Int>, dayOfWeek)

        }
    }


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

    fun getBodyData(userId: String) {
        reference.child("body_data").child(userId).get().addOnSuccessListener { snapShot->
            val height = snapShot.child("height").value.toString()
            val weight = snapShot.child("weight").value.toString()
            val bodyData = BodyDataDetail(height = height.toDouble(), weight = weight.toDouble())
            Log.d("FirebaseRepository", "getBodyData()")
            bodyDataListener!!.onBodyDataCaught(bodyData)

        }
    }
    private fun handleBodyData(userId: String, bodyData: BodyDataDetail){
        reference.child("body_data").child(userId).get().addOnSuccessListener { snapShot->
            if(!snapShot.exists()) {
                val oldBodyData = BodyDataDetail(0.0, 0.0)
                updateBodyCallBack(userId, oldBodyData, bodyData)
            }
            else {
                val weight = snapShot.child("weight").value.toString()
                val height = snapShot.child("height").value.toString()
                val oldBodyData = BodyDataDetail(weight.toDouble(), height.toDouble())
                Log.d("getBodyData1","${oldBodyData.height}, ${oldBodyData.weight}")
                updateBodyCallBack(userId, oldBodyData, bodyData)
            }
        }
    }

    private fun updateBodyCallBack
                (userId: String,
                 oldBodyData: BodyDataDetail,
                 newBodyData: BodyDataDetail) {
        if(oldBodyData.height == 0.0){
            reference.child("body_data")
                .child(userId)
                .setValue(newBodyData)
        }
        else{
            reference.child("body_data")
                .child(userId)
                .setValue(newBodyData)
        }
    }

    fun addBodyData(userId: String, height: Double, weight: Double){
        val bodyData = BodyDataDetail(height, weight)
        handleBodyData(userId, bodyData)
    }

    fun updateBodyData(userId: String, height: Double, weight: Double){
        val bodyData = BodyDataDetail(height, weight)
        handleBodyData(userId, bodyData)
    }

    fun removeBodyData(userId: String, date: String){
        reference.child("body_data")
            .child(userId)
            .child(date)
            .removeValue()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
    fun getStretchData(userId: String, date: String){
        reference.child("stretch_data").child(userId).child(date).get().addOnSuccessListener { snapShot->
            val time = snapShot.child("time").value.toString().toInt()
            val stretchData = StretchDataDetail(time = time)
            stretchDataListener!!.onStretchDataCaught(stretchData)
        }
    }

    fun getWeekStretchData(userId: String, lastDate: LocalDate) {
        reference.get().addOnSuccessListener { snapShot ->
            val userStretchData = snapShot.child("stretch_data").child(userId)
            val tempWeekData = ArrayList<Int>()
            val dayOfWeek = lastDate.dayOfWeek.value // 월(1) ~ 일(7)
            for(i in 0 until 7) {
                val date = lastDate.minusDays(i.toLong()).format(DateTimeFormatter.ISO_LOCAL_DATE) as String
                if(userStretchData.child(date).value == null || userStretchData.child(date).value.toString() == "null"){
                    tempWeekData.add(0)
                }
                else{
                    val time = userStretchData.child(date).child("time").value.toString().toInt()
                    tempWeekData.add(time)
                }
            }
            //val walkWeekData = tempWeekData.subList(tempWeekData.lastIndex, 0) as ArrayList<Double>
            stretchWeekDataListener!!.onStretchWeekDataCaught(tempWeekData.reversed() as ArrayList<Int>, dayOfWeek)

        }
    }

    fun addStretchData(userId: String, date: String, time: Int) {
        val stretchData = StretchDataDetail(time)
        reference.child("stretch_data").child(userId).child(date).get().addOnSuccessListener { snapShot ->
            if(!snapShot.exists()) {
                reference.child("stretch_data")
                    .child(userId)
                    .child(date)
                    .setValue(stretchData)
            }
            else {
                val oldTime = snapShot.child("time").value.toString().toInt()
                val sumTime = oldTime + time
                val newStretchData = StretchDataDetail(sumTime)
                reference.child("stretch_data")
                    .child(userId)
                    .child(date)
                    .setValue(newStretchData)
            }
        }
    }

    fun updateStretchData(userId: String, date: String, time: Int) {
        val stretchData = StretchDataDetail(time)
        reference.child("stretch_data").child(userId).child(date).get().addOnSuccessListener { snapShot ->
            if(!snapShot.exists()) {
                reference.child("stretch_data")
                    .child(userId)
                    .child(date)
                    .setValue(stretchData)
            }
            else {
                val oldTime = snapShot.child("time").value.toString().toInt()
                val sumTime = oldTime + time
                val newStretchData = StretchDataDetail(sumTime)
                reference.child("stretch_data")
                    .child(userId)
                    .child(date)
                    .setValue(newStretchData)
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
    fun getUserInfo(user: FirebaseUser){
        reference.child("users").get().addOnSuccessListener { snapShot->
            val userId = snapShot.child(user.uid).child("user_id").value.toString()
            val nickName = snapShot.child(user.uid).child("nick_name").value.toString()
            val age = snapShot.child(user.uid).child("age").value.toString()
            Log.d("getUserInfo", "$userId, $nickName")
            if(userId == "null" || nickName == "null"){
                enrollUser(user.uid, user.displayName.toString(), 0)
                val userData = UserData(userId, nickName, age.toInt())
                userDataListener!!.onUserDataCaught(userData, true)
            }
            else {
                Log.d("getUserInfo else", "$userId, $nickName")
                val userData = UserData(userId, nickName, age.toInt())
                userDataListener!!.onUserDataCaught(userData, false)
            }
        }.addOnFailureListener {
            enrollUser(user.uid, user.displayName.toString(), 0)
            val userData = UserData(user.uid, user.displayName!!, 0)
            userDataListener!!.onUserDataUncaught(user)
        }
    }

    fun getUserInfoByID(userId: String){
        reference.child("users").get().addOnSuccessListener { snapShot->
            val userId = snapShot.child(userId).child("user_id").value.toString()
            val nickName = snapShot.child(userId).child("nick_name").value.toString()
            val age = snapShot.child(userId).child("age").value.toString().toInt()
            Log.d("getUserInfo", "$userId, $nickName")
            val userData = UserData(userId, nickName, age)
            userDataListener!!.onUserDataCaught(userData, false)
        }
    }


    fun getTotalData(userId: String, date: String) {
        reference.get().addOnSuccessListener { snapShot->
            //Log.d("snapshot.value",snapShot.value.toString())
            val dataArray = ArrayList<String>()
            val bodySnapShot = snapShot.child("body_data").child(userId)
            val userSnapShot = snapShot.child("users").child(userId)
            val waterSnapShot = snapShot.child("water_data").child(userId).child(date)
            val walkSnapShot = snapShot.child("walk_data").child(userId).child(date)
            val stretchSnapShot = snapShot.child("stretch_data").child(userId).child(date)
            val height = bodySnapShot.child("height").value.toString()
            val weight = bodySnapShot.child("weight").value.toString()
            val nickName = userSnapShot.child("nick_name").value.toString()
            val age = userSnapShot.child("age").value.toString()
            val kcalConsumed = walkSnapShot.child("kcal_consumed").value.toString()
            val walkMemo = walkSnapShot.child("memo").value.toString()
            val stepCount = walkSnapShot.child("step_count").value.toString()
            val quantity = waterSnapShot.child("quantity").value.toString()
            val recentTime = waterSnapShot.child("recent_time").value.toString()
            val drinkMemo = waterSnapShot.child("memo").value.toString()
            val stretchTime = stretchSnapShot.child("time").value.toString()
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
                add(stretchTime)
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
                val userData = UserData(userId, nickName, age.toInt())
                val waterData = WaterDataDetail(quantity.toInt(), drinkMemo, recentTime)
                val walkData = WalkDataDetail(stepCount.toInt(), kcalConsumed.toDouble(), walkMemo)
                val stretchData = StretchDataDetail(stretchTime.toInt())
                totalDataListener!!.onTotalDataCaught(userData, bodyData, waterData, walkData, stretchData)
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
                                updateBodyData(userId,0.0, 0.0)
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
                        9->{
                            Log.d("getTotalData", "updateStretchData")
                            updateStretchData(userId, date, 0)
                        }
                    }
                }
                getTotalData(userId, date)
            }
        }
    }

    fun getTotalMonthData(userId: String, lastDay: String){
        reference.get().addOnSuccessListener { snapShot->
            val splitt = lastDay.split("-")
            val year = splitt[0]
            val month = splitt[1]
            var day = 0
            if(splitt[2][0] == '0')
                day = splitt[2][1].toInt()
            else
                day = splitt[2].toInt()
            val ratingArray = ArrayList<Rating>()
            for(i in 1 until (day + 1)){
                var tempDate = ""
                if(i < 10) {
                    tempDate = "$year-$month-0${i}"
                    Log.i("tempData", tempDate)
                }
                else
                    tempDate = "$year-$month-${i}"
                val userSnapShot = snapShot.child("users").child(userId)
                val waterSnapShot = snapShot.child("water_data").child(userId).child(tempDate)
                val walkSnapShot = snapShot.child("walk_data").child(userId).child(tempDate)
                val stretchSnapShot = snapShot.child("stretch_data").child(userId).child(tempDate)
                val drinkQuantity = waterSnapShot.child("quantity").value.toString()
                val stepCount = walkSnapShot.child("step_count").value.toString()
                val stretchTime = stretchSnapShot.child("time").value.toString()
                val dataArray = ArrayList<String>()
                val nullIndexArray = ArrayList<Int>()
                dataArray.add(drinkQuantity)
                dataArray.add(stepCount)
                dataArray.add(stretchTime)
                var j = 0
                for(data in dataArray){
                    if(data == "NULL" || data == "null"){
                        nullIndexArray.add(j)
                    }
                    j += 1
                }
                if(nullIndexArray.size == 0){
                    var score = 0
                    if(stepCount.toInt() >= 10000) score += 1
                    if(drinkQuantity.toInt() >= 2000) score += 1
                    if(stretchTime.toInt() >= 900) score += 1
                    var rate = 0
                    if(score == 3) rate = 3
                    else if(score in 1..2) rate = 2
                    else rate = 1
                    if(stepCount.toInt() == 0 && drinkQuantity.toInt() == 0 && stretchTime.toInt() == 0)
                        rate = 0

                    ratingArray.add(Rating(tempDate, rate))
                }
                else{
//                    for(index in nullIndexArray){
//                        when(index){
//                            0 -> updateDrinkData(userId,tempDate, "00:00:01", 0, "") // drink
//                            1 -> updateWalkingData(userId, tempDate, 0, 0.0, "") // Walk
//                            2 -> updateStretchData(userId, tempDate, 0)
//                        }
//                    }
//                    getTotalMonthData(userId, lastDay)
                    ratingArray.add(Rating(tempDate, 0))
                }
            }
            totalMonthListener!!.onTotalMonthCaught(ratingArray)
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


