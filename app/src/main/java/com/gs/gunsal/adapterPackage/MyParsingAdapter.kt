package com.gs.gunsal.adapterPackage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gs.gunsal.databinding.FragmentHealthNewsBinding
import com.gs.gunsal.fragment.Mydata

class MyParsingAdapter(val items: ArrayList<Mydata>):RecyclerView.Adapter<MyParsingAdapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun OnItemClick(holder: MyViewHolder, view: View, data: Mydata, position: Int)
    }

    var itemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(val binding: FragmentHealthNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            /*binding.titleNews.setOnClickListener {
                itemClickListener?.OnItemClick(this, it, items[adapterPosition], adapterPosition)
            }*/
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = FragmentHealthNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.newsTitle.text = items[position].title
    }
    override fun getItemCount(): Int {
        return items.size
    }
}

