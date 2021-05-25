package com.gs.gunsal.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.gs.gunsal.databinding.FragmentHealthNewsListBinding

/**
 * A fragment representing a list of Items.
 */
class Health_News_Fragment : Fragment() {

    private var columnCount = 1
    lateinit var binding: FragmentHealthNewsListBinding
    lateinit var callback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHealthNewsListBinding.inflate(layoutInflater, container, false)

        binding.fab.setOnClickListener {
            if(binding.floatingViewLinear.visibility == View.GONE){
                binding.floatingViewLinear.visibility = View.VISIBLE
            }else{
                binding.floatingViewLinear.visibility = View.GONE
            }
        }
        // Set the adapter
        /*
        if (view is RecyclerView) {
            with(view) {
                binding.list.layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                binding.list.adapter = MyNewsRecyclerViewAdapter(PlaceholderContent.ITEMS)
            }
        }
*/
        return binding.root
    }


    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            Health_News_Fragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}