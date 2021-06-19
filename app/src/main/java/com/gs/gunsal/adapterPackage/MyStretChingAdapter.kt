package com.gs.gunsal.adapterPackage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gs.gunsal.databinding.FragmentHealthNewsBinding
import com.gs.gunsal.databinding.StretchingPlayerItemBinding

class MyStretChingAdapter() : RecyclerView.Adapter<MyStretChingAdapter.ViewHolder>() {
    //Stretching web View 어뎁터
    var url: List<String> = listOf(
        "https://www.youtube.com/embed/t70t-sklypk",
        "https://www.youtube.com/embed/0L5gAT1fJaM",
        "https://www.youtube.com/embed/gMaB-fG4u4g",
        "https://www.youtube.com/embed/myNjmnvI6x0",
        "https://www.youtube.com/embed/ZKENAzznW5o",
        "https://www.youtube.com/embed/QfCUN-MdvYc",
        "https://www.youtube.com/embed/2Uv1B3kjCOI",
        "https://www.youtube.com/embed/nOGT4lXlSHw"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            StretchingPlayerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.webView.webChromeClient = WebChromeClient()
        holder.webView.settings.apply {   //데이터 생성과 동시에 로드
            javaScriptEnabled = true
            pluginState = WebSettings.PluginState.ON
            useWideViewPort = true
            loadWithOverviewMode = true
        }
        holder.webView.loadUrl(url[position])
    }

    override fun getItemCount(): Int = url.size

    inner class ViewHolder(binding: StretchingPlayerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val webView: WebView = binding.webItem
    }
}