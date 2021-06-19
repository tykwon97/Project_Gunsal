package com.gs.gunsal.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * A fragment representing a list of Items.
 */
class Health_News_Fragment : Fragment() {

    var binding: FragmentHealthNewsListBinding?= null
    var binding2: NewsKeywordItemBinding ?= null
    lateinit var adapter: MyNewsRecyclerViewAdapter
    var titleArrayList = ArrayList<String>()
    lateinit var callback: OnBackPressedCallback
    var searchString:ArrayList<String> ?=null
    var griddata: ArrayList<String> = arrayListOf(
        "운동", "다이어트_음식", "다이어트",
        "건강식품", "건강", "트레이닝", "헬스", "스트레칭", "보조제"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHealthNewsListBinding.inflate(layoutInflater, container, false)
        binding2 = NewsKeywordItemBinding.inflate(layoutInflater, container, false)

        titleArrayList.addAll((activity as MainActivity).titleArrayList)  //초기 시작 시 받아온 뉴스를 list에 삽입
        Log.i("list", titleArrayList.toString())
        adapter = MyNewsRecyclerViewAdapter(titleArrayList)
        var linkArray = ArrayList<String>()
        linkArray.addAll((activity as MainActivity).linkArrayList)  //초기 시작 시 받아온 뉴스 Link list에 삽입
        adapter.itemOnClickListener = object : MyNewsRecyclerViewAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: RecyclerView.ViewHolder,
                view: View,
                data: String,
                position: Int
            ) {
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
            filterData.add(MyFilterData("#"+it, false))
        }



        val myadapter = MyGridViewAdapter(filterData)
        myadapter.mListener = object : MyGridViewAdapter.OnItemClickListener { //카테고리 리스트 활성화에 따른 멀티쿼리 검색문 생성 및 삭제, 색변화
            override fun onItemClick(
                v: MyGridViewAdapter.ViewHolder?,
                pos: Int,
                myFilterData: MyFilterData
            ) {
                if(!myFilterData.is_checked){
                    searchString!!.add(myFilterData.title)
                    v!!.title.setTextColor(Color.parseColor("#FFFFFF"))
                    v!!.title.setBackgroundResource(R.drawable.round_shape)
                    filterData[pos].is_checked = true
                }else{
                    searchString!!.remove(myFilterData.title)
                    v!!.title.setTextColor(Color.parseColor("#000000"))
                    v!!.title.setBackgroundResource(R.drawable.unselectshape)
                    filterData[pos].is_checked = false
                }
            }

        }
        binding!!.filter.setOnClickListener {  //선택한 카테고리에 따른 뉴스 정보 재 쿼리
            var search:String = ""
            searchString!!.forEach {
                search+=it+","
            }
            if(search.equals("")){
                search = "건강"
            }
            NaverRepository.getSearchNews(search, ::onReSearchNewsFetched, ::onError)
            binding!!.gridrecyclerview.visibility = View.GONE
            binding!!.gridtext.visibility = View.GONE
            binding!!.filter.visibility = View.GONE
            binding!!.color.visibility = View.GONE

        }
        binding!!.gridrecyclerview.adapter = myadapter
        return binding!!.root
    }

    fun onReSearchNewsFetched(list: List<Items>){  //서칭 성공시 실행할 함수, 뷰 아이템 재배열
        titleArrayList.clear()
        for(n in list){
            titleArrayList.add(n.title)
        }
        adapter.itemOnClickListener = object : MyNewsRecyclerViewAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: RecyclerView.ViewHolder,
                view: View,
                data: String,
                position: Int
            ) {
                changeWebView(list[position].originallink)
            }
        }
        adapter.notifyDataSetChanged()
    }
    fun onError() {
        Log.i("error", "error")
    }

    override fun onAttach(context: Context) {   //카테고리뷰 띄운채 탭바 이동시 카테고리 삭제해주는 코드
        super.onAttach(context)
        callback = object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                Log.d("frag", "back")
                binding!!.list.visibility = View.VISIBLE
                binding!!.fab.visibility = View.VISIBLE
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }
    fun changeWebView(link:String){   //webViewChange
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

