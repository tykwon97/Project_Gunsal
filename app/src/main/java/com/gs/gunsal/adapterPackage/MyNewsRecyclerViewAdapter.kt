package com.gs.gunsal.adapterPackage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gs.gunsal.databinding.FragmentHealthNewsBinding

class MyNewsRecyclerViewAdapter(
    private val values: List<String>
) : RecyclerView.Adapter<MyNewsRecyclerViewAdapter.ViewHolder>() {

    var itemOnClickListener: OnItemClickListener?=null


    interface OnItemClickListener{
        fun OnItemClick(holder: RecyclerView.ViewHolder, view: View, data:String, position:Int)
    }

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
        holder.title.text = item

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentHealthNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title: TextView = binding.newsTitle
        init {
            binding.click.setOnClickListener {
                itemOnClickListener?.OnItemClick(this, it, values[adapterPosition], adapterPosition)

            }
        }
    }

}