package com.zibroit.studyfcm

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.datatransport.runtime.dagger.internal.DoubleCheck.lazy
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        intiFirebase( )
        updateResult()
    }

    /*MainActivity에서 MainActivity가 불러와질 경우 발생하는 함수*/
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        setIntent(intent)
        updateResult(true)
    }

    private fun intiFirebase(){
        FirebaseMessaging.getInstance().token.
        addOnCompleteListener {
            if(it.isSuccessful){
                firebase_token_textview.text = it.result
            }
        }
    }

    /*isNewIntent에 따라 분기 처리*/
    /*isNewIntent: 앱을 눌러서 실행했는지, 알림을 눌러서 실행했는지 체크*/
    @SuppressLint("SetTextI18n")
    private fun updateResult(isNewIntent: Boolean = false){
        result_tv.text = (intent.getStringExtra("notificationType") ?: "앱 런처") +
        if(isNewIntent){
            "(으)로 갱신했습니다."
        }else {
            "(으)로 실행했습니다."
        }
    }
}