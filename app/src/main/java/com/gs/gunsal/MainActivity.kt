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
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.gs.gunsal.adapterPackage.MyTabFragStateAdapter
import com.gs.gunsal.dataClass.WalkDataDetail
import com.gs.gunsal.dataClass.WaterDataDetail
import com.gs.gunsal.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLEncoder

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
    val scope = CoroutineScope(Dispatchers.IO)

    var walkalarm = true //걸음 수 알림
    var wateralarm = true //물 알림

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        if(walkalarm ==true){
            walkNotification()
        }
    }


    private fun init() {
        binding.viewPager.adapter = MyTabFragStateAdapter(this)
        initIconColor()
        TabLayoutMediator(binding.myTabIconview, binding.viewPager) { tab, position ->
            tab.text = textarr[position]
            tab.setIcon(iconarr[position])
        }.attach() //꼭 attach해야함.

    }

    private fun initParsing() {
        val my_id = "4X7Z074B7gOxj0qI58lo"
        val password = "q9QIDTlWlG"
        scope.launch {
            var text: String? = null
            text = try {
                URLEncoder.encode("건강", "UTF-8")
            } catch (e: UnsupportedEncodingException) {
                throw RuntimeException("검색어 인코딩 실패", e)
            }
            text?.let { Log.i("text", it) }
            val apiURL =
                "https://openapi.naver.com/v1/search/news.json?query=$text" // json 결과
            val requestHeaders: MutableMap<String, String> = HashMap()
            requestHeaders["X-Naver-Client-Id"] = my_id
            requestHeaders["X-Naver-Client-Secret"] = password
            val responseBody: String = get(apiURL, requestHeaders)
            Log.i("naver", responseBody)
        }
    }


    private operator fun get(apiUrl: String, requestHeaders: Map<String, String>): String {
        val con: HttpURLConnection = connect(apiUrl)
        return try {
            con.setRequestMethod("GET")
            for ((key, value) in requestHeaders) {
                con.setRequestProperty(key, value)
            }
            val responseCode: Int = con.getResponseCode()
            Log.i("error code", responseCode.toString())
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                readBody(con.getInputStream())
            } else { // 에러 발생
                Log.i("error", "error!!!")
                readBody(con.getErrorStream())
            }
        } catch (e: IOException) {
            throw java.lang.RuntimeException("API 요청과 응답 실패", e)
        } finally {
            con.disconnect()
        }
    }

    private fun connect(apiUrl: String): HttpURLConnection {
        try {
            val url = URL(apiUrl)
            return url.openConnection() as HttpURLConnection
        } catch (e: MalformedURLException) {
            throw java.lang.RuntimeException("API URL이 잘못되었습니다. : $apiUrl", e)
        } catch (e: IOException) {
            throw java.lang.RuntimeException("연결이 실패했습니다. : $apiUrl", e)
        }


    }

    private fun readBody(body: InputStream): String {
        val streamReader = InputStreamReader(body)
        try {
            BufferedReader(streamReader).use { lineReader ->
                val responseBody = StringBuilder()
                var line: String?
                while (lineReader.readLine().also { line = it } != null) {
                    responseBody.append(line)
                }

                Log.i("body", responseBody.toString())
                return responseBody.toString()
            }
        } catch (e: IOException) {
            throw java.lang.RuntimeException("API 응답을 읽는데 실패했습니다.", e)
        }
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