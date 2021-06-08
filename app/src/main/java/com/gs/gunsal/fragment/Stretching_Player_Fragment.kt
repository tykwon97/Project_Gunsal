package com.gs.gunsal.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.gs.gunsal.adapterPackage.MyStretChingAdapter
import com.gs.gunsal.databinding.FragmentStretchingPlayerListBinding

/**
 * A fragment representing a list of Items.
 */
class Stretching_Player_Fragment : Fragment() {

    lateinit var binding: FragmentStretchingPlayerListBinding
    var start: Long = 0
    lateinit var callback: OnBackPressedCallback


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        start = System.currentTimeMillis()
        binding = FragmentStretchingPlayerListBinding.inflate(layoutInflater, container, false)
        binding.stretchingView.adapter = MyStretChingAdapter()
        return binding!!.root
    }

    /*fun changeWebView(position: Int) {
        binding.webView.loadUrl(url[position])
        binding.webView.visibility = View.VISIBLE
        binding.linear.visibility = View.GONE
    }*/

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun onResume() {
        super.onResume()
        Log.i("onResume", "onResume")
        start = System.currentTimeMillis()
    }
    override fun onPause() {
        super.onPause()
        Log.i("onDetach", "onDetach")
        var end = System.currentTimeMillis()
        Log.i("onDetach", ((end - start) / 1000).toString())
        start = 0
    }

}