package com.gs.gunsal

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.gs.gunsal.adapterPackage.MyTabFragStateAdapter
import com.gs.gunsal.dataClass.WalkDataDetail
import com.gs.gunsal.dataClass.WaterDataDetail
import com.gs.gunsal.databinding.ActivityMainBinding
import com.gs.gunsal.fragment.MyNewsRecyclerViewAdapter
import com.ms129.stockPrediction.naverAPI.Items
import com.ms129.stockPrediction.naverAPI.NaverRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MainActivity : AppCompatActivity() {
    val textarr = arrayListOf<String>("오늘의기록", "월간통계", "건강뉴스", "스트레칭", "설정")
    val iconarr = arrayListOf<Int>(
        R.drawable.ic_home,
        R.drawable.ic_monthly,
        R.drawable.ic_news,
        R.drawable.ic_category,
        R.drawable.ic_setting
    )
    lateinit var binding: ActivityMainBinding

    var walkalarm = true //걸음 수 알림
    var wateralarm = true //물 알림
    var titleArrayList = ArrayList<String>()
    var linkArrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        initNaver()
        setContentView(binding.root)
        init()

        if(walkalarm ==true){
            walkNotification()
        }
    }

    private fun initNaver() {
        NaverRepository.getSearchNews("건강", ::onSearchNewsFetched, ::onError)
    }
    fun onSearchNewsFetched(list: List<Items>) {
        for(n in list){
            titleArrayList.add(n.title)
            linkArrayList.add(n.originallink)
        }
    }

    fun onError() {
        Log.i("error", "error")
    }

    private fun init() {
        binding.viewPager.adapter = MyTabFragStateAdapter(this)
        initIconColor()
        TabLayoutMediator(binding.myTabIconview, binding.viewPager) { tab, position ->
            tab.text = textarr[position]
            tab.setIcon(iconarr[position])
        }.attach() //꼭 attach해야함.
    }

    fun initIconColor() {
        binding.myTabIconview.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var tabiconColor = ContextCompat.getColor(applicationContext, R.color.select_color)
                tab?.icon?.setColorFilter(tabiconColor, PorterDuff.Mode.SRC_IN)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                var tabiconColor =
                    ContextCompat.getColor(applicationContext, R.color.unselect_color)
                tab?.icon?.setColorFilter(tabiconColor, PorterDuff.Mode.SRC_IN)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                var tabiconColor = ContextCompat.getColor(applicationContext, R.color.select_color)
                tab?.icon?.setColorFilter(tabiconColor, PorterDuff.Mode.SRC_IN)
            }

        })
    }
    fun tabbargone(){
        binding.myTabIconview.visibility= View.GONE
    }
    fun tabbarvisible(){
        binding.myTabIconview.visibility= View.VISIBLE
    }
    //알림부분~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    fun walkNotification(){//만보기 알림부분

        FirebaseRepository.getWalkData("201710561", "2021-05-24")//FirebaseRepository.getCurrentDate())
        FirebaseRepository.walkDataListener = object: FirebaseRepository.OnWalkingDataListener{

            override fun onWalkDataCaught(walkDataDetail: WalkDataDetail) {
                val stepCount = walkDataDetail.step_count//.toInt()
                val currentTime = FirebaseRepository.getCurrentTime()

                Log.i("WalkNotification:recentTIme", stepCount.toString())
                Log.i("WalkNotification:recentTIme", currentTime)

                var step = stepCount.toString()
                val id = "walkchannel"
                val name = "만보기 알림"
                val notificationChannel = NotificationChannel(
                    id,
                    name,
                    NotificationManager.IMPORTANCE_HIGH
                )
                //속성 설정
                notificationChannel.enableVibration(true)//진동
                notificationChannel.enableLights(true) //빛
                notificationChannel.lightColor = Color.BLUE
                notificationChannel.lockscreenVisibility= Notification.VISIBILITY_PRIVATE
                var message = "걸음 수 : $step"

                val PROGRESS_MAX = 10000 //목표 걸음수
                val PROGRESS_CURRENT = step.toInt() //현재 걸음 수

                val builder = NotificationCompat.Builder(applicationContext, id)
                    .setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false) //만보기 progress bar
                    .setSmallIcon(R.drawable.setting_walk) //알림 이미지
                    .setContentTitle("만보기")
                    .setContentText(message)
                    .setOngoing(true); //상태바에 고정

                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.putExtra("walking", message) //key랑 message
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

                val pendingIntent = PendingIntent.getActivity(
                    applicationContext,
                    1,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                builder.setContentIntent(pendingIntent)

                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager //알림메시지를 NOTIFY
                manager.createNotificationChannel(notificationChannel)
                val notification = builder.build()
                manager.notify(1, notification)
            }
        }
    }

    fun waterNotification() { //물 섭취 알림부분
        FirebaseRepository.getDrinkData("201710560",FirebaseRepository.getCurrentDate())
        FirebaseRepository.waterDataListener = object : FirebaseRepository.OnWaterDataListener {
            override fun onWaterDataCaught(waterDataDetail: WaterDataDetail) {
                val recentTime = waterDataDetail.recent_time
                val currentTime = FirebaseRepository.getCurrentTime()

                Log.i("WalkNotification:recentTIme", recentTime)
                Log.i("WalkNotification:recentTIme", currentTime)

                val recent = recentTime.split(":")
                val current = currentTime.split(":")
                val recenttime =
                    recent[0].toInt() * 60 * 60 + recent[1].toInt() * 60 + recent[2].toInt()
                val currenttime =
                    current[0].toInt() * 60 * 60 + current[1].toInt() * 60 + current[2].toInt()

                val timeInterval = currenttime - recenttime
                Log.i("WalkNotification:timeInterval", timeInterval.toString())

                if (timeInterval <= 360) { //1분이내에 또 섭취한 경우
                    val id = "waterchannel"
                    val name = "물 섭취 알림"
                    val notificationChannel = NotificationChannel(
                        id,
                        name,
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    //속성 설정
                    notificationChannel.enableVibration(true)//진동
                    notificationChannel.enableLights(true) //빛
                    notificationChannel.lightColor = Color.BLUE
                    notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                    var message = "이제 물 마셔야 할 시간입니다! 수분 공급해주세요! "

                    val builder = NotificationCompat.Builder(applicationContext, id)
                        .setSmallIcon(R.drawable.setting_water) //알림 이미지
                        .setContentTitle("수분 섭취 알림")
                        .setContentText(message)
                        .setAutoCancel(true) //알림 클릭시 삭제 여부

                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.putExtra("water", message) //key랑 message
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

                    val pendingIntent = PendingIntent.getActivity(
                        applicationContext,
                        2,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    builder.setContentIntent(pendingIntent)

                    val manager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager //알림메시지를 NOTIFY
                    manager.createNotificationChannel(notificationChannel)
                    val notification = builder.build()

                    manager.notify(2, notification)
                }
            }
        }
    }
}