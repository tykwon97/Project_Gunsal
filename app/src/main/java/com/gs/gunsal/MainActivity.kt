package com.gs.gunsal

import android.Manifest
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_STEP_COUNTER
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.gs.gunsal.adapterPackage.MyTabFragStateAdapter
import com.gs.gunsal.dataClass.NewWalkData
import com.gs.gunsal.dataClass.WalkDataDetail
import com.gs.gunsal.dataClass.WaterDataDetail
import com.gs.gunsal.databinding.ActivityMainBinding
import com.gs.gunsal.fragment.MyNewsRecyclerViewAdapter
import com.ms129.stockPrediction.naverAPI.Items
import com.ms129.stockPrediction.naverAPI.NaverRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), SensorEventListener {
    lateinit var userId : String
    lateinit var nickName : String
    var timer: Timer?= null

    val textarr = arrayListOf<String>("오늘의기록", "월간통계", "건강뉴스", "스트레칭", "설정")
    val iconarr = arrayListOf<Int>(
        R.drawable.ic_home,
        R.drawable.ic_monthly,
        R.drawable.ic_news,
        R.drawable.ic_category,
        R.drawable.ic_setting
    )
    lateinit var binding: ActivityMainBinding
    //~~~~~~~~~~~~~~~~~~~~태윤~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //센서 연결을 위한 변수
    private var sensorManager: SensorManager? = null
    private var stepCountSensor: Sensor? = null

    //현재 걸음 수(출력되는 값)
    private var mSteps = 0
    //리스너가 등록되고 난 후의 step count(언제 지정할지 값)
    private var mCounterSteps = 0
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    var titleArrayList = ArrayList<String>()
    var linkArrayList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        initNaver()
        setContentView(binding.root)
        userId = intent.getStringExtra("USER_ID").toString()
//~~~~~~~~~~~~~~~~~~~~태윤~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        //저장된 값 가져오기
        mSteps =  App.prefs1.myIndex1!!
        mCounterSteps = App.prefs2.myIndex2!!
        Log.i("prefs1",mSteps.toString())
        Log.i("prefs2",mCounterSteps.toString())
        //binding.walknum.setText(mSteps.toString())//shared preference 받는 곳

        //만보기
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            Log.d("TAG", "PERMISSION 'ACTIVITY_RECOGNITION' NOT GRANTED");
            //ask for permission
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), TYPE_STEP_COUNTER)
            //requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PHYISCAL_ACTIVITY);
        }else
        {
            Log.d("TAG", "PERMISSION 'ACTIVITY_RECOGNITION' GRANTED");
        }

        //센서 연결[걸음수 센서를 이용한 흔듬 감지]
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        stepCountSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepCountSensor == null) {
            //Toast.makeText(this,"No Step Detect Sensor",Toast.LENGTH_SHORT).show()
        }

        //알림 버튼 확인시 이전 데이터 가져오기
        val step = intent.getIntExtra("walking_msteps",-1)
        val counterstep = intent.getIntExtra("walking_mcountersteps",-1)
        if (step != -1 && counterstep != -1) {
            mCounterSteps = counterstep
            mSteps = step
            //binding.walknum.setText(step.toString())
            Log.i("str : ",step.toString())
        }
        binding.apply {
            //walknum.setText(Integer.toString(mSteps))
        }
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        init()
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
        binding.viewPager.adapter = MyTabFragStateAdapter(this, userId)
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

    //~~~~~~~~~~~~~~~~~~~~~~태윤~~~~~~~~~~~~~~~~~~~~~~~
    //센서값이 변경되면 호출됨
    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type === Sensor.TYPE_STEP_COUNTER) {
            //stepcountsenersor는 앱이 꺼지더라도 초기화 되지않는다. 그러므로 우리는 초기값을 가지고 있어야한다.
            if (mCounterSteps == 0) {
                // initial value
                mCounterSteps = event!!.values[0].toInt()
            }
            //리셋 안된 값 + 현재값 - 리셋 안된 값
            mSteps = event!!.values[0].toInt() - mCounterSteps
            binding.apply {
                //walknum.setText(Integer.toString(mSteps))
                Log.i(
                    "log: ",
                    "New step detected by STEP_COUNTER sensor. Total step count: $mSteps"
                )
            }
            //service작업 시작해봄 ㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜ
            val intent = Intent(this, WalkingService::class.java)
            intent.putExtra("USER_ID", userId)
            intent.putExtra("walking", mSteps)
            intent.putExtra("counterstep", mCounterSteps)
            startService(intent)

            //파이어베이스에 저장
            //FirebaseRepository.updateWalkingData("201710561",FirebaseRepository.getCurrentDate(),mSteps,10.0,"test")
            FirebaseRepository.updateWalkingData(userId,FirebaseRepository.getCurrentDate(),mSteps,10.0,"test")
            //값 저장해두기
            App.prefs1.myIndex1 = mSteps
            App.prefs2.myIndex2 = mCounterSteps
        }
    }
    //센서 정밀도가 변경되면 호출됨
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //TODO("Not yet implemented")
    }

    override fun onStart() {
        super.onStart()
        if (stepCountSensor != null) {
            //센서의 속도 설정,만보기 센서 리스너 오브젝트를 등록
            sensorManager?.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //sensorManager?.unregisterListener(this)
        //unregisterReceiver(receiver)
    }

    fun waternoti(){ //수분섭취 아이콘 클릭한 경우 fragment에서 호출됨
        if (timer != null) { //타이머가 이미 존재하는 경우 초기화 시켜주기
            timer!!.cancel()
        }
        timer = Timer()
        //FirebaseRepository.addDrinkData(userId, 250, "250ml 추가") //물 섭취 기록하기
        //Toast.makeText(applicationContext, "250ml 추가", Toast.LENGTH_SHORT).show()
        val timerTask = object : TimerTask() {
            override fun run() { //TimerTask에서 반드시 override 해줘야됨!
                waterNotification()
            }
        }
        timer?.schedule(timerTask, 5000)//5초, 3600000(1시간)
    }

    fun waterNotification() {//--------------------------------------물 섭취 알림부분
        val id = "waterchannel"
        val name = "물 섭취 알림"
        val notificationChannel = NotificationChannel(
            id,
            name,
            NotificationManager.IMPORTANCE_HIGH
        )
        //속성 설정
        notificationChannel.enableVibration(true)//진동
        notificationChannel.enableLights(true) //빛
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        var message = "이제 물 마셔야 할 시간입니다! 수분 공급해주세요! "

        val builder = NotificationCompat.Builder(applicationContext, id)
            .setSmallIcon(R.drawable.today_water_icon) //알림 이미지
            .setContentTitle("수분 섭취 알림")
            .setContentText(message)
            .setAutoCancel(true) //알림 클릭시 삭제 여부

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("USER_ID", userId)
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