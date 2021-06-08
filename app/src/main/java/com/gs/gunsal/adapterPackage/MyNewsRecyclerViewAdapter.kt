package com.gs.gunsal.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.RecyclerView
import com.gs.gunsal.R
import com.gs.gunsal.databinding.FragmentHealthNewsBinding
import java.util.*

class MyNewsRecyclerViewAdapter(
    private val values: List<String>
) : RecyclerView.Adapter<MyNewsRecyclerViewAdapter.ViewHolder>() {
    var image = listOf<Int>(
        R.drawable.health_1,
        R.drawable.health_2,
        R.drawable.health_3,
        R.drawable.health_4,
        R.drawable.health_5,
        R.drawable.health_6,
        R.drawable.health_7,
        R.drawable.health_8,
        R.drawable.health_9,
        R.drawable.health_10,
        R.drawable.health_11,
        R.drawable.health_12,
        R.drawable.health_13,
        R.drawable.health_14,
        R.drawable.health_15,
        R.drawable.health_16,
        R.drawable.health_17,
        R.drawable.health_18,
        R.drawable.health_19,
        R.drawable.health_20,
        R.drawable.health_21,
        R.drawable.health_22,
        R.drawable.health_23,
        R.drawable.health_24,
        R.drawable.health_25,
        R.drawable.health_26,
        R.drawable.health_27,
        R.drawable.health_28
    )
    val random = Random()
    var itemOnClickListener:OnItemClickListener?=null


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

        holder.title.text = item.parseAsHtml()
        holder.image.setImageResource(image[random.nextInt(28)])
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentHealthNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val title: TextView = binding.newsTitle
        val image:ImageView = binding.newsImage
        init {
            binding.click.setOnClickListener {
                itemOnClickListener?.OnItemClick(this, it, values[adapterPosition], adapterPosition)

            }
        }
    }

}