package com.gs.gunsal.adapterPackage

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gs.gunsal.databinding.NewsKeywordItemBinding

class MyGridViewAdapter(
private val values: List<String>
) : RecyclerView.Adapter<MyGridViewAdapter.ViewHolder>() {

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
        fun onItemClick(v: ViewHolder?, pos: Int)
    }


    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: NewsKeywordItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title: TextView = binding.item
        override fun toString(): String {
            return super.toString() + " '" + title.text + "'"
        }
        init{
            binding.item.setOnClickListener {
                mListener?.onItemClick(this, adapterPosition)
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.title.text = item
    }
}