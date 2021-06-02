package com.gs.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.gs.gunsal.databinding.FragmentHealthNewsListBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class Health_News_Fragment : Fragment() {

    lateinit var binding: FragmentHealthNewsListBinding
    lateinit var callback: OnBackPressedCallback
    lateinit var adapter: MyNewsRecyclerViewAdapter
    var data1: ArrayList<String> = arrayListOf()

    val scope = CoroutineScope(Dispatchers.IO)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHealthNewsListBinding.inflate(layoutInflater, container, false)
        initParsing()
        binding.fab.setOnClickListener {
            if (binding.floatingViewLinear.visibility == View.GONE) {
                binding.floatingViewLinear.visibility = View.VISIBLE
            } else {
                binding.floatingViewLinear.visibility = View.GONE
            }
        }

        return binding.root
    }


    private fun getnews() {
        scope.launch {
        }
    }

    private fun initParsing() {

    }
}