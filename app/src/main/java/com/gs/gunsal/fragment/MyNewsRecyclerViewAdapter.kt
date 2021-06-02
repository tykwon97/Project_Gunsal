package com.gs.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gs.gunsal.databinding.FragmentHealthNewsBinding

class MyNewsRecyclerViewAdapter(
    private val values: List<Mydata>
) : RecyclerView.Adapter<MyNewsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentHealthNewsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.title.text = item.title
        holder.detail.text = item.detail
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentHealthNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title: TextView = binding.newsTitle
        val detail: TextView = binding.newsDetail

    }

}