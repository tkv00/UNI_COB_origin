package com.example.uni_cob

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 2000 // 2초 동안 스플래시 화면을 보여줍니다.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 지정된 시간(SPLASH_TIME_OUT)이 지난 후에 메인 액티비티로 이동합니다.
        Handler().postDelayed({
            val intent = Intent(this, image_slide::class.java)
            startActivity(intent)

        }, SPLASH_TIME_OUT)
    }
}
