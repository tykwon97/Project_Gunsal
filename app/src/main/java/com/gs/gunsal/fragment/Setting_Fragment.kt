package com.gs.gunsal.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.gs.gunsal.databinding.FragmentSettingBinding
import com.gs.gunsal.databinding.NewsDialogLayoutBinding

class Setting_Fragment : Fragment() {

    var binding: FragmentSettingBinding?= null
    var bindingDia:NewsDialogLayoutBinding?= null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(layoutInflater, container, false)
        binding!!.item.setOnClickListener {
            bindingDia = NewsDialogLayoutBinding.inflate(layoutInflater, container, false)

            val dlg = AlertDialog.Builder(requireContext())
            dlg.setView(bindingDia!!.root)
            dlg.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                val height = bindingDia!!.height.text.toString()
                var weight = bindingDia!!.weight.text.toString()
                Toast.makeText(context, "height : " + height + "weight : " + weight, Toast.LENGTH_SHORT).show()
            })
            dlg.setNegativeButton("취소", null)
            dlg.show()
        }
        binding!!.waterToggle.setOnClickListener {
            with(binding!!.waterToggle){
                if(isChecked==true){
                    Toast.makeText(requireContext(), "check!", Toast.LENGTH_SHORT).show()
                }else{

                    Toast.makeText(requireContext(), "not check!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding!!.walkToggle.setOnClickListener {
            with(binding!!.walkToggle){
                if(isChecked==true){
                    Toast.makeText(requireContext(), "check!", Toast.LENGTH_SHORT).show()
                }else{

                    Toast.makeText(requireContext(), "not check!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}