package com.gs.gunsal

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.gs.gunsal.dataClass.BodyDataDetail
import com.gs.gunsal.dataClass.UserData
import com.gs.gunsal.dataClass.WalkDataDetail
import com.gs.gunsal.dataClass.WaterDataDetail
import com.gs.gunsal.databinding.ActivityIntroBinding

class Intro_Activity : AppCompatActivity() {
    lateinit var binding: ActivityIntroBinding
    //firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth
    //google client
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Google 로그인 옵션 구성. requestIdToken 및 Email 요청
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("895742237394-pii5o2pv9ed6ieeri5nr3echckntl46r.apps.googleusercontent.com")
            //'R.string.default_web_client_id' 에는 본인의 클라이언트 아이디를 넣어주시면 됩니다.
            //저는 스트링을 따로 빼서 저렇게 사용했지만 스트링을 통째로 넣으셔도 됩니다.
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //firebase auth 객체
        firebaseAuth = FirebaseAuth.getInstance()

        initIntro()
    }

    public override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        //signOut()
        if(account != null){ // 이미 로그인 되어있을시 바로 메인 액티비티로 이동
            Log.i("IntroActivity", "account != null, id = ${firebaseAuth.currentUser?.uid}")
            toMainActivity(firebaseAuth.currentUser)
        }
        else{
            Log.i("IntroActivity", "account == null")
            val intent = Intent(this@Intro_Activity, LoginActivity::class.java)
            startActivity(intent)
        }
    } //onStart End

    private fun signOut() { // 로그아웃
        // Firebase sign out
        firebaseAuth.signOut()

        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener(this) {
            //updateUI(null)
            Log.i("IntroActivity", "signOut success")
        }
    }

    fun toMainActivity(user: FirebaseUser?) {
        if(user !=null) { // MainActivity 로 이동
            Log.i("IntroActivity", "toMainActivity")
            FirebaseRepository.getUserInfo(user)
            FirebaseRepository.userDataListener = object : FirebaseRepository.OnUserDataListener {
                override fun onUserDataCaught(userData: UserData, isFirst: Boolean) {
                    Log.d("toMainActivity->onUserDataCaught", "success")
                    if(isFirst){
                        val intent = Intent(this@Intro_Activity, ProfileActivity::class.java)
                        intent.putExtra("USER_ID", user.uid)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        val intent = Intent(this@Intro_Activity, MainActivity::class.java)
                        intent.putExtra("USER_ID", userData.user_id)
//                    intent.putExtra("USER_NICK_NAME", userData.nickName)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onUserDataUncaught(user: FirebaseUser) {
                    FirebaseRepository.enrollUser(user.uid.toString(), user.displayName.toString())
                    FirebaseRepository.getTotalData(user.uid, FirebaseRepository.getCurrentDate())
                    FirebaseRepository.totalDataListener = object: FirebaseRepository.OnTotalDataListener{
                        override fun onTotalDataCaught(
                            userData: UserData,
                            bodyData: BodyDataDetail,
                            waterData: WaterDataDetail,
                            walkData: WalkDataDetail
                        ) {
                        }
                    }
                    val intent = Intent(this@Intro_Activity, ProfileActivity::class.java)
                    intent.putExtra("USER_ID", user.uid)
                    startActivity(intent)
                    finish()
                }
            }
        }
    } // toMainActivity End

    private fun initIntro() {

//            if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this))) {
//                Handler().postDelayed({
//                    startActivity(Intent(this, LoginActivity::class.java))
//                    finish()
//                }, 3000L)
//            } else {
//
//                Handler().postDelayed({
//                    startActivity(Intent(this, MainActivity::class.java))
//                    finish()
//                }, 3000L)
//            }
    }
}