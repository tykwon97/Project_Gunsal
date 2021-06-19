package com.gs.gunsal.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gs.gunsal.MainActivity
import com.gs.gunsal.R
import com.gs.gunsal.adapterPackage.MyGridViewAdapter
import com.gs.gunsal.databinding.FragmentHealthNewsListBinding
import com.gs.gunsal.databinding.NewsKeywordItemBinding
import com.ms129.stockPrediction.naverAPI.Items
import com.ms129.stockPrediction.naverAPI.NaverRepository


/**
 * A fragment representing a list of Items.
 */
class Health_News_Fragment : Fragment() {

    var binding: FragmentHealthNewsListBinding? = null
    var binding2: NewsKeywordItemBinding? = null
    lateinit var adapter: MyNewsRecyclerViewAdapter
    var titleArrayList = ArrayList<String>()
    var linkArray = ArrayList<String>()
    lateinit var callback: OnBackPressedCallback
    var searchString: ArrayList<String>? = null
    var griddata: ArrayList<String> = arrayListOf(
        "운동", "다이어트_음식", "다이어트",
        "건강식품", "건강", "트레이닝", "헬스", "스트레칭", "보조제"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Thread.sleep(2000)

        binding = FragmentHealthNewsListBinding.inflate(layoutInflater, container, false)
        binding2 = NewsKeywordItemBinding.inflate(layoutInflater, container, false)


        binding!!.webView.settings.apply {
            javaScriptEnabled = true // 웹페이지 자바스클비트 허용 여부
            setAppCacheEnabled(true) // 브라우저 캐시 허용 여부
            setDomStorageEnabled(true) // 로컬저장소 허용 여부
            setSupportMultipleWindows(false)
        }
        binding!!.webView.webChromeClient = WebChromeClient()
        init()
        return binding!!.root
    }


    private fun init() {


        titleArrayList.addAll((activity as MainActivity).titleArrayList)  //초기 시작 시 받아온 뉴스를 list에 삽입
        linkArray.addAll((activity as MainActivity).linkArrayList)  //초기 시작 시 받아온 뉴스 Link list에 삽입
        adapter = MyNewsRecyclerViewAdapter(titleArrayList)

        adapter.itemOnClickListener = object : MyNewsRecyclerViewAdapter.OnItemClickListener {
            override fun OnItemClick(
                holder: RecyclerView.ViewHolder,
                view: View,
                data: String,
                position: Int
            ) {
                Log.i("Link", linkArray[position])
                onAttach(requireContext())
                changeWebView(linkArray[position])
            }
        }
        binding!!.list.adapter = adapter

        binding!!.fab.setOnClickListener {    //flaoting 버튼 클릭시 카테고리 리스트 Visible
            if (binding!!.gridrecyclerview.visibility == View.GONE) {
                binding!!.color.visibility = View.VISIBLE
                searchString = ArrayList()
                binding!!.gridrecyclerview.visibility = View.VISIBLE
                binding!!.gridtext.visibility = View.VISIBLE
                binding!!.filter.visibility = View.VISIBLE
                binding!!.fab.visibility=View.GONE
            } else {
                binding!!.gridrecyclerview.visibility = View.GONE
                binding!!.color.visibility = View.GONE
                binding!!.gridtext.visibility = View.GONE
                binding!!.filter.visibility = View.GONE
            }
        }


        binding!!.gridrecyclerview.layoutManager =
            StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL)
        var filterData = ArrayList<MyFilterData>()
        griddata.forEach {
            filterData.add(MyFilterData("#" + it, false))
        }


        val myadapter = MyGridViewAdapter(filterData)
        myadapter.mListener = object :
            MyGridViewAdapter.OnItemClickListener { //카테고리 리스트 활성화에 따른 멀티쿼리 검색문 생성 및 삭제, 색변화
            override fun onItemClick(
                v: MyGridViewAdapter.ViewHolder?,
                pos: Int,
                myFilterData: MyFilterData
            ) {
                if (!myFilterData.is_checked) {
                    searchString!!.add(myFilterData.title)
                    v!!.title.setTextColor(Color.parseColor("#FFFFFF"))
                    v!!.title.setBackgroundResource(R.drawable.round_shape)
                    filterData[pos].is_checked = true
                } else {
                    searchString!!.remove(myFilterData.title)
                    v!!.title.setTextColor(Color.parseColor("#000000"))
                    v!!.title.setBackgroundResource(R.drawable.unselectshape)
                    filterData[pos].is_checked = false
                }
            }

        }

        binding!!.filter.setOnClickListener {  //선택한 카테고리에 따른 뉴스 정보 재 쿼리
            var search: String = ""
            searchString!!.forEach {
                search += it + ","
            }
            if (search.equals("")) {
                search = "건강"
            }
            NaverRepository.getSearchNews(search, ::onReSearchNewsFetched, ::onError)
            binding!!.gridrecyclerview.visibility = View.GONE
            binding!!.gridtext.visibility = View.GONE
            binding!!.filter.visibility = View.GONE
            binding!!.color.visibility = View.GONE
            binding!!.fab.visibility=View.VISIBLE
        }
        binding!!.gridrecyclerview.adapter = myadapter
    }

    fun onReSearchNewsFetched(list: List<Items>) {  //서칭 성공시 실행할 함수, 뷰 아이템 재배열
        titleArrayList.clear()
        for (n in list) {
            titleArrayList.add(n.title)
            linkArray.add(n.link)
        }
        adapter.itemOnClickListener = object : MyNewsRecyclerViewAdapter.OnItemClickListener {
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
        adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        Log.i("resume", "onResume")
        binding!!.webView.visibility = View.GONE
        binding!!.list.visibility = View.VISIBLE
        binding!!.fab.visibility = View.VISIBLE
    }

    fun onError() {
        Log.i("error", "error")
    }

    override fun onAttach(context: Context) {   //카테고리뷰 띄운채 탭바 이동시 카테고리 삭제해주는 코드
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding!!.webView.visibility = View.GONE
                binding!!.list.visibility = View.VISIBLE
                binding!!.fab.visibility = View.VISIBLE
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    fun changeWebView(link: String) {   //webViewChange

        binding!!.webView.webChromeClient = WebChromeClient()
        binding!!.webView.loadUrl(link)
        binding!!.list.visibility = View.GONE
        binding!!.fab.visibility = View.GONE
        binding!!.gridrecyclerview.visibility = View.GONE
        binding!!.webView.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        binding2 = null
    }
}