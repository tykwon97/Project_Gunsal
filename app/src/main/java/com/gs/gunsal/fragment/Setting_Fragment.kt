package com.gs.gunsal.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.gs.gunsal.FirebaseRepository
import com.gs.gunsal.LoginActivity
import com.gs.gunsal.dataClass.BodyDataDetail
import com.gs.gunsal.dataClass.UserData
import com.gs.gunsal.databinding.FragmentSettingBinding
import com.gs.gunsal.databinding.NewsDialogLayoutBinding

class Setting_Fragment(val userId: String) : Fragment() {
    //firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth
    //google client
    private lateinit var googleSignInClient: GoogleSignInClient
    var binding: FragmentSettingBinding?= null
    var bindingDia:NewsDialogLayoutBinding?= null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(layoutInflater, container, false)
        binding!!.item.setOnClickListener {
            bindingDia = NewsDialogLayoutBinding.inflate(layoutInflater, container, false)

            val dlg = AlertDialog.Builder(requireContext())
            dlg.setView(bindingDia!!.root)
            dlg.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                val height = bindingDia!!.height.text.toString()
                var weight = bindingDia!!.weight.text.toString()
                FirebaseRepository.updateBodyData(userId, height.toDouble(), weight.toDouble())
                Toast.makeText(context, "height : " + height + "weight : " + weight, Toast.LENGTH_SHORT).show()
            })
            dlg.setNegativeButton("취소", null)
            dlg.show()
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAccount()
        initView(userId)

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

        binding!!.withdrawal.setOnClickListener{
            revokeAccess()
        }

        binding!!.logout.setOnClickListener {
            signOut()
        }
    }

    private fun initAccount() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("895742237394-pii5o2pv9ed6ieeri5nr3echckntl46r.apps.googleusercontent.com")
            //'R.string.default_web_client_id' 에는 본인의 클라이언트 아이디를 넣어주시면 됩니다.
            //저는 스트링을 따로 빼서 저렇게 사용했지만 스트링을 통째로 넣으셔도 됩니다.
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity().baseContext, gso)

        //firebase auth 객체
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun signOut() { // 로그아웃
        // Firebase sign out
        //val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()

        // Google sign out
        val googleSignInClient =
            googleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
                //updateUI(null)
                Log.i("signOut in Google", "SUCCESS")
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
    }

    private fun revokeAccess() { //회원탈퇴
        // Firebase sign out
        //googleSignInClient.signOut().addOnCompleteListener {
            firebaseAuth.currentUser!!.delete().addOnCompleteListener {
                FirebaseRepository.removeUser(userId)
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            //firebaseAuth.signOut()

       // }

//        googleSignInClient.revokeAccess().addOnCompleteListener(requireActivity()) {
//            FirebaseRepository.removeUser(userId)
//            firebaseAuth.currentUser!!.delete().
//            firebaseAuth.signOut()
//            val intent = Intent(requireActivity(), LoginActivity::class.java)
//            startActivity(intent)
//            onDestroy()
//        }
    }

    private fun initView(userId: String) {
        FirebaseRepository.getUserInfoByID(userId)
        FirebaseRepository.userDataListener = object: FirebaseRepository.OnUserDataListener{
            override fun onUserDataCaught(userData: UserData, isFirst: Boolean){
                binding!!.apply {
                    userEmail.text = userData.user_id
                    userName.text = userData.nick_name
                }
            }

            override fun onUserDataUncaught(user: FirebaseUser) {
                // Doesn't need
            }
        }
        FirebaseRepository.getBodyData(userId)
        FirebaseRepository.bodyDataListener = object: FirebaseRepository.OnBodyDataListener{
            override fun onBodyDataCaught(bodyDataDetail: BodyDataDetail) {
                binding!!.apply {
                    weightTextView.text = "${bodyDataDetail.weight}kg"
                    heightTextView.text = "${bodyDataDetail.height}cm"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}