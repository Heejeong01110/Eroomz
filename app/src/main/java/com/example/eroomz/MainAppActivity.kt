package com.example.eroomz


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent

import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.eroomz.NearMeActivity
import com.example.eroomz.NearUnivActivity
import com.google.android.gms.location.*
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.activity_app_main.*

const val EXTRA_NEAR_UNIV = "com.example.eroomz.NEAR_UNIV"
const val EXTRA_NEAR_SUBWAY = "com.example.eroomz.NEAR_SUBWAY"

class MainAppActivity  : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_main)
        val loginConfirmation = intent.getStringExtra(EXTRA_LOGIN_CONFIRM_TEXT)
        this.supportActionBar?.hide()
        // 사용자 정보 요청 (기본)
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("TAG", "사용자 정보 요청 실패", error)
                val loginActionResult = findViewById<TextView>(R.id.loginCheck).apply{
                    text = "웰컴, 어서와요!"
                }
            }
            else if (user != null) {
                Log.i("TAG", "사용자 정보 요청 성공" +
                        "\n회원번호: ${user.id}" +
                        "\n이메일: ${user.kakaoAccount?.email}" +
                        "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                        "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}")
                val loginActionResult = findViewById<TextView>(R.id.loginCheck).apply{
                    text = "웰컴! ${user.kakaoAccount?.profile?.nickname} 님"
                }
            }
        }



        nearMe()
        nearUniv()
    }

    fun runNearMeActivity(view: View){
        val intent = Intent(this, NearMeActivity::class.java)
        startActivity(intent)
    }
    fun runNearUnivActivity(view: View){
        val intent = Intent(this, NearUnivActivity::class.java)
        startActivity(intent)
    }
    private fun nearMe(){
        nearMe.setOnClickListener{
            this.runNearMeActivity(nearMe)
        }
    }
    private fun nearUniv(){
        nearUniv.setOnClickListener{
            this.runNearUnivActivity(nearUniv)
        }
    }
}