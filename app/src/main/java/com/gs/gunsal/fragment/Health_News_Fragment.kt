package com.gs.gunsal.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gs.gunsal.R
import com.gs.gunsal.databinding.FragmentHealthNewsListBinding
import com.gs.gunsal.databinding.NewsKeywordItemBinding
import com.ms129.stockPrediction.naverAPI.Items
import com.ms129.stockPrediction.naverAPI.NaverRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * A fragment representing a list of Items.
 */
class Health_News_Fragment : Fragment() {

    var binding: FragmentHealthNewsListBinding?= null
    var binding2: NewsKeywordItemBinding ?= null
    lateinit var adapter: MyNewsRecyclerViewAdapter
    lateinit var titleArrayList: ArrayList<String>
    lateinit var callback: OnBackPressedCallback
    var data1: ArrayList<String> = arrayListOf()
    var griddata: ArrayList<String> = arrayListOf(
        "#운동", "#다이어트_음식", "#다이어트",
        "#건강식품", "#건강", "#트레이닝", "#헬스", "#스트레칭", "#보조제"
    )

    val scope = CoroutineScope(Dispatchers.IO)

    private fun changeFragment(fragment: Fragment) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.news, fragment)
            .commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHealthNewsListBinding.inflate(layoutInflater, container, false)
        binding!!.newsView.webChromeClient = WebChromeClient()
        binding!!.newsView.settings.apply {
            javaScriptEnabled = true
            setAppCacheEnabled(true)
            pluginState = WebSettings.PluginState.ON
            useWideViewPort = true
            loadWithOverviewMode = true

        }
        binding!!.newsView.webViewClient  = WebViewClient()
        if (binding!!.gridrecyclerview is RecyclerView) {
            with(binding!!.gridrecyclerview) {

            }
        }
        binding!!.fab.setOnClickListener {
            if (binding!!.gridrecyclerview.visibility == View.GONE) {
                binding!!.gridrecyclerview.visibility = View.VISIBLE
                binding!!.news.setBackgroundColor(Color.parseColor("#D5DBE0"))
            } else {
                binding!!.gridrecyclerview.visibility = View.GONE
                binding!!.news.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }
        }
        binding!!.gridrecyclerview.layoutManager =
            StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL)
        val myadapter = MyGridViewAdapter(griddata)
        myadapter.mListener = object : MyGridViewAdapter.OnItemClickListener {
            override fun onItemClick(v: MyGridViewAdapter.ViewHolder?, pos: Int) {
                Toast.makeText(
                    context,
                    "my item is " + v?.title?.text.toString() + "position is " + pos.toString(),
                    Toast.LENGTH_SHORT
                ).show()
                binding!!.gridrecyclerview.visibility = View.GONE
            }

        }

        binding!!.gridrecyclerview.adapter = myadapter
        initNaver()
        return binding!!.root
    }

    private fun initNaver() {
        NaverRepository.getSearchNews("건강", ::onSearchNewsFetched, ::onError)
    }

    override fun onDetach() {
        super.onDetach()
    }
    fun onSearchNewsFetched(list: List<Items>) {
        titleArrayList = ArrayList<String>()
        for(n in list){
            titleArrayList.add(n.title)
        }
        adapter = MyNewsRecyclerViewAdapter(titleArrayList)

        adapter.itemOnClickListener = object : MyNewsRecyclerViewAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: RecyclerView.ViewHolder,
                view: View,
                data: String,
                position: Int
            ) {
                onAttach(requireContext())
                changeWebView(list[position].originallink)
            }
        }
        binding!!.list.adapter = adapter
    }

    fun onError() {
        Log.i("error", "error")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                Log.d("frag", "back")
                binding!!.newsView.visibility = View.GONE
                binding!!.list.visibility = View.VISIBLE
                binding!!.fab.visibility = View.VISIBLE
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }
    fun changeWebView(link:String){
        binding!!.newsView.visibility = View.VISIBLE
        binding!!.newsView.loadUrl(link)
        binding!!.list.visibility = View.GONE
        binding!!.fab.visibility = View.GONE
        binding!!.gridrecyclerview.visibility = View.GONE
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        binding2 = null
    }
}

