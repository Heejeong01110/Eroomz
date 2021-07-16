package com.example.eroomz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.eroomz.MainAppActivity
import com.example.eroomz.R
import com.kakao.sdk.auth.LoginClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.rbddevs.splashy.Splashy
import kotlinx.android.synthetic.main.activity_main.*

const val EXTRA_LOGIN_CONFIRM_TEXT = "com.example.eroomz.LOGIN_CONFIRM_TEXT"
var Splashy_Confirm = false
class MainActivity : AppCompatActivity() {
    var loginSuccess = false
    var loginConfirmText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Splash Screen
        //이부분 오류나서 막아뒀음 -heejeong

        if(!Splashy_Confirm) {
            setSplashy()
        }


        // 카카오에서 할당 받을 앱키를 입력 하세요.
        // 우리껀 https://developers.kakao.com/console/app/527267 여기서 네이티브 앱 키로 가져오면 돼
        // 처음 하는거면 https://developers.kakao.com/docs/latest/ko/getting-started/sdk-android-v1#key-hash 참고해서 키 해시 만들어서
        // https://developers.kakao.com/console/app/527267/config/platform 여기에 키 해시 수정 등록 하고 하면 될 거야
        KakaoSdk.init(this, "a0f6efe41c7cb82313b180e9454c51ae")
        startLogin()
    }
    fun runMainAppActivity(view: View){
        val intent = Intent(this, MainAppActivity::class.java).apply {
            putExtra(EXTRA_LOGIN_CONFIRM_TEXT, loginConfirmText)
        }
        startActivity(intent)
    }

    // KakaoTalk Login
    private fun startLogin() {
        button.setOnClickListener {
            // 로그인 공통 callback 구성
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e("TAG", "로그인 실패", error)
                    this.loginConfirmText = "login Failed, maybe click Try it out button?"
                    textView.text = this.loginConfirmText
                } else if (token != null) {
                    Log.i("TAG", "로그인 성공 ${token.accessToken}")
                    this.loginConfirmText = "login Success ${token.accessToken}"
                    textView.text = this.loginConfirmText
                    this.loginSuccess = true
                    this.runMainAppActivity(button)
                }
            }

            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
            if (LoginClient.instance.isKakaoTalkLoginAvailable(this)) {
                LoginClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                LoginClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
    }

    // Splash Screen Configuration
    private fun setSplashy() {
        Splashy_Confirm = true
        Splashy(this)
            .setLogo(R.drawable.ic_launcher_foreground)
            .setTitle(R.string.app_name)
            .setTitleColor("#5B5B5B")
            .setSubTitle("당신의 소중한 이룸터")
            .setProgressColor("#F09D51")
            .setBackgroundColor("#EDF7D2")
            .setFullScreen(true)
            .setDuration(3000)
            .showProgress(true)
            .show()
    }
}
