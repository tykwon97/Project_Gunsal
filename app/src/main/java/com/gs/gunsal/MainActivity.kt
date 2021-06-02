package com.gs.gunsal

import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()


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

}