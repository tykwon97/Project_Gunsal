package com.gs.gunsal

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.gs.gunsal.dataClass.BodyDataDetail
import com.gs.gunsal.dataClass.WalkDataDetail
import com.gs.gunsal.dataClass.WaterDataDetail
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object FirebaseRepository {
    //lateinit var uid: String
    val reference = FirebaseDatabase.getInstance().reference
    var ddd = 0.0

    interface SnapShotListener{
        fun onSnapShotCaught(snapShot: DataSnapshot)
    }

    var snapShotListener:SnapShotListener ?= null


////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////<User Data>////////////////////////////////////////////////////////////////

    fun enrollUser(userId: String, name: String, nickName: String?){
        val user : FUser = if(nickName != null) {
            FUser(name, nickName)
        } else{
            FUser(name, "null")
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

    fun getDrinkRecentTime(userId: String){
        val currentDate = getCurrentDate()
        reference.child("water_data").child(userId).child(currentDate).get().addOnSuccessListener { snapShot ->
            if(!snapShot.exists()){
                snapShotListener!!.onSnapShotCaught(snapShot)
            }
            else{
                snapShotListener!!.onSnapShotCaught(snapShot)
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

//    fun getWalkingData(userId: String, date: String, onSuccess: (news: List<Items>) -> Unit,
//                       onError: () -> Unit) {
//        var data = WalkDataDetail(-1, -1.0, "")
//        reference.child("walk_data").child(userId).child(date).get().addOnCompleteListener {
//        }
//    }


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

     fun getCurrentDate(): String{
         return LocalDateTime.now(ZoneOffset.of("+9")).format(DateTimeFormatter.ISO_LOCAL_DATE) as String
    }

    private fun getCurrentTime(): String{
         val temp = LocalDateTime.now(ZoneOffset.of("+9")).format(DateTimeFormatter.ISO_LOCAL_TIME) as String
         var time = ""
         for(s in temp){
             if(s == '.') break;
             time += s
         }
        //Log.d("TIME::", time)
        return time
    }

    data class FUser(val name: String, val nickName: String)

    data class FDrinkData(var quantity: Int, val memo: String)

//    fun getTotalData(userId: String): TotalData {
//        var userData = UserData("FAIL", "FAIL")
//        FirebaseDatabase.getInstance().getReference("users/${userId}").get().addOnSuccessListener {
//            userData = it.value as UserData
//        }
//        var walkData = listOf(WalkDate(listOf(WalkDataDetail("-999","fail", "-999"))))
//        FirebaseDatabase.getInstance().getReference("walk_data/${userId}").get().addOnSuccessListener {
//            walkData = it.value as List<WalkDate>
//        }
//        var waterData = listOf(WaterDate(listOf(WaterDataDetail("-999","fail"))))
//        FirebaseDatabase.getInstance().getReference("water_data/${userId}").get().addOnSuccessListener {
//            waterData = it.value as List<WaterDate>
//        }
//
//        var bodyData = listOf(BodyDate(listOf(BodyDataDetail("-999","-999"))))
//        FirebaseDatabase.getInstance().getReference("body_data/${userId}").get().addOnSuccessListener {
//            bodyData = it.value as List<BodyDate>
//        }
//        return TotalData(userId, userData, walkData, waterData, bodyData)
//    }

}


