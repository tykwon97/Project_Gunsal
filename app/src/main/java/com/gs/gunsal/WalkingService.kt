package com.gs.gunsal

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class WalkingService : Service() {

    var notificationid = 100
    var mSteps= 0
    var mCounterSteps=0

    var receiver = object :BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
        }
    }

    override fun onCreate() {//1번
        super.onCreate()
        registerReceiver(receiver, IntentFilter("com.example.walking"))
        Log.i("Myservice","onCreate()")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {//2번
        Log.i("Myservice","onStartCommand()")
        mSteps = intent!!.getIntExtra("walking",-1)
        mCounterSteps = intent!!.getIntExtra("counterstep",-1)
        walkNotification()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        Log.i("Myservice","onDestroy()")
    }

    override fun onBind(intent: Intent): IBinder? {
      return null
    }

    fun walkNotification(){//--------------------------------------만보기 알림부분
        var step = mSteps.toString()
        val id = "walkchannel"
        val name = "만보기 알림"
        val notificationChannel = NotificationChannel(
            id,
            name,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        //속성 설정
        notificationChannel.enableVibration(true)//진동
        notificationChannel.enableLights(true) //빛
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.lockscreenVisibility= Notification.VISIBILITY_PRIVATE
        //var message = "걸음 수 : $step"
        var message = "걸음 수 : $mSteps"

        val PROGRESS_MAX = 10000 //목표 걸음수
        //val PROGRESS_CURRENT = step.toInt() //현재 걸음 수
        val PROGRESS_CURRENT = mSteps?.toInt() //현재 걸음 수

        //val contentsView = RemoteViews(packageName, R.layout.walkingnotification)
        val builder = NotificationCompat.Builder(applicationContext, id)
            //.setContent(contentsView)
            .setProgress(PROGRESS_MAX, PROGRESS_CURRENT!!, false) //만보기 progress bar
            .setSmallIcon(R.drawable.health_8) //알림 이미지
            .setContentTitle("만보기")
            .setContentText(message)
            .setOngoing(true); //상태바에 고정

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("walking_msteps", mSteps) //key랑 message
        intent.putExtra("walking_mcountersteps", mCounterSteps)
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
        //manager.notify(1, notification)
        startForeground(notificationid,notification)
    }
}