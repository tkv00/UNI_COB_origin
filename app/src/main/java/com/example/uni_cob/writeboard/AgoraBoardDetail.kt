package com.example.uni_cob.writeboard

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.uni_cob.R
import com.example.uni_cob.utility.Board2
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class AgoraBoardDetail : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agora_board_detail)

        // Intent에서 데이터 추출
        val title = intent.getStringExtra("TITLE")
        val userName = intent.getStringExtra("NAME")
        val time = intent.getStringExtra("TIME")
        val location = intent.getStringExtra("LOCATION")
        val content = intent.getStringExtra("CONTENT")
        val numberOfPeople = intent.getIntExtra("PEOPLE", -1) // Default 값으로 -1을 설정
        val date=intent. getStringExtra("FORMATTED_DATE")
        val dday=intent.getStringExtra("DDAY")



        findViewById<TextView>(R.id.et_time).text = time
        findViewById<TextView>(R.id.et_date).text=date
        findViewById<TextView>(R.id.et_title).text = title
        findViewById<TextView>(R.id.name).text = userName
        findViewById<TextView>(R.id.et_where).text = location
        findViewById<TextView>(R.id.textView31).text = content
        if (numberOfPeople != -1) {
            findViewById<TextView>(R.id.et_people).text = "최대 수용 가능한 인원 : "+numberOfPeople.toString()+"명"
        }
        val ddayTextView = findViewById<TextView>(R.id.et_day)
        ddayTextView.text = dday

        // D-day 값에 따라 레이아웃 변경
        dday?.let {
            if (it.startsWith("D-")) {
                // D-day가 음수일 때 (과거 이벤트)
                ddayTextView.setBackgroundResource(R.drawable.btn_register) // 예시로 et_gray.xml 배경을 설정

            } else if (it.startsWith("D+")) {
                // D-day가 양수일 때 (미래 이벤트)
                ddayTextView.setBackgroundResource(R.drawable.et_gray) // 예시로 et_blue.xml 배경을 설정

            }
        }
        findViewById<TextView>(R.id.et_info).text=numberOfPeople.toString()+"명 이상 모여야 진행해요."
        findViewById<TextView>(R.id.textViewParticipantCount).text="/"+numberOfPeople.toString()
    }




}
