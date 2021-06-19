package com.gs.gunsal.adapterPackage

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gs.gunsal.databinding.NewsKeywordItemBinding
import com.gs.gunsal.fragment.MyFilterData

class MyGridViewAdapter(var values: ArrayList<MyFilterData>
) : RecyclerView.Adapter<MyGridViewAdapter.ViewHolder>() {
    //건강뉴스 카테고리 리스트 어뎁터
    var mListener: OnItemClickListener? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            NewsKeywordItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    interface OnItemClickListener {
        fun onItemClick(v: ViewHolder?, pos: Int, myFilterData: MyFilterData)
    }


    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: NewsKeywordItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title: TextView = binding.item
        override fun toString(): String {
            return super.toString() + " '" + title.text + "'"
        }
        init{
            binding.item.setOnClickListener {   //카테고리 클릭 시 이벤트
                mListener?.onItemClick(this, adapterPosition, values[adapterPosition])
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position].title
        holder.title.text = item
    }
}