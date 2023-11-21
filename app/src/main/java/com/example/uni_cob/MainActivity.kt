package com.example.uni_cob

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.example.uni_cob.Chatting.HomeFragment
import com.example.uni_cob.Chatting.ProfileFragment
import com.example.uni_cob.department.Useful_info
import com.example.uni_cob.writeboard.Agora
import com.example.uni_cob.writeboard.Board1All
import com.example.uni_cob.writeboard.WriteBoard_base

import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    // 현재 표시되고 있는 프래그먼트를 추적하기 위한 변수
    private var currentFragment: Fragment? = null

    private val keywords = listOf(
        "경영학과", "컴퓨터공학과", "심리학과", "전자공학과", "기계공학과",
        "법학과", "통계학과", "생명과학과", "화학과", "물리학과",
        "경제학과", "교육학과", "사회학과", "영어영문학과", "불어불문학과",
        "독어독문학과", "중어중문학과", "역사학과", "철학과", "수학과",
        "체육교육과", "음악과", "미술학과", "디자인학과", "건축학과",
        "의학과", "간호학과", "약학과", "치의학과", "한의학과", "정보통신학과",
        "행정학과", "국제관계학과", "정치외교학과", "환경과학과", "생태학과",
        "동양학과", "서양학과", "문화인류학과", "신문방송학과", "국어국문학과",
        "사진학과", "영상학과", "무대예술학과", "무용학과", "작곡학과",
        "연극학과", "영상제작학과", "인공지능학과", "데이터과학과", "로봇공학과",
        "우주학과", "항공학과", "해양학과", "조경학과", "도시계획학과",
        "사회복지학과", "심리치료학과", "특수교육학과", "영양학과", "간호학과",
        "보건학과", "안전공학과", "재료공학과", "나노공학과", "생명공학과",
        "경찰학과", "소방학과", "국방학과", "세무학과", "회계학과",
        "물류학과", "유통학과", "관광학과", "호텔경영학과", "레저스포츠학과"
    )

    private lateinit var btn_department:ImageButton //전공대화
    private lateinit var btn_ondayclass:ImageButton//원데이클래스
    private lateinit var btn_usefulinfo: ImageButton//알쓸신잡
    private lateinit var btn_commu:ImageButton//아고라
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_commu=findViewById(R.id.study_together)//아고라
        btn_ondayclass=findViewById(R.id.btn_onedayclass)//원데이클래스
        btn_usefulinfo=findViewById(R.id.btn_usefulinfo)//알쓸신잡
        btn_department=findViewById(R.id.btn_department_communication)//전공대화



        //알쓸신잡
        btn_usefulinfo.setOnClickListener{
            val intent=Intent(this, Useful_info::class.java)
            startActivity(intent)

        }
        //아고라 페이지로 이동
        btn_commu.setOnClickListener{
            val intent=Intent(this, Agora::class.java)
            startActivity(intent)

        }
        //전공대화 페이지로 이동
        btn_department.setOnClickListener{
            val intent=Intent(this, Board1All::class.java)
            startActivity(intent)
        }

        displayRandomKeywords()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { item ->
            val mainContent: NestedScrollView = findViewById(R.id.nestedScrollView)
            val fragmentFrame: FrameLayout = findViewById(R.id.fragments_frame)

            when (item.itemId) {
                R.id.menu_home -> {
                    fragmentFrame.visibility = View.GONE
                    mainContent.visibility = View.VISIBLE
                    true
                }
                R.id.menu_chat -> {
                    val homeFragment = HomeFragment.newInstance()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments_frame, homeFragment).commit()
                    mainContent.visibility = View.GONE
                    fragmentFrame.visibility = View.VISIBLE
                    true
                }
                R.id.menu_profile -> {
                    val profileFragment = ProfileFragment.newInstance()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments_frame, profileFragment).commit()
                    mainContent.visibility = View.GONE
                    fragmentFrame.visibility = View.VISIBLE
                    true
                }
                R.id.menu_notification -> {
                    true
                }
                R.id.menu_register -> {
                    val intent=Intent(this,WriteBoard_base::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun displayRandomKeywords() {
        val firstRowLayout = findViewById<LinearLayout>(R.id.layoutKeywordsFirstRow)
        val secondRowLayout = findViewById<LinearLayout>(R.id.layoutKeywordsSecondRow)

        // 랜덤으로 키워드 섞기
        val shuffledKeywords = keywords.shuffled()
        val firstRowKeywords = shuffledKeywords.take(4)
        val secondRowKeywords = shuffledKeywords.drop(4).take(4)

        firstRowKeywords.forEach { keyword ->
            val textView = createKeywordTextView(keyword)
            firstRowLayout.addView(textView)
        }

        secondRowKeywords.forEach { keyword ->
            val textView = createKeywordTextView(keyword)
            secondRowLayout.addView(textView)
        }
    }

    private fun createKeywordTextView(keyword: String): TextView {
        val density = resources.displayMetrics.density
        val heightInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, resources.displayMetrics)
        return TextView(this).apply {
            text = keyword
            gravity=Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_SP,13f)
            setTextColor(Color.BLACK)

            setBackgroundResource(R.drawable.et_keywords)
            setPadding(30, 30, 30, 30)
            setOnClickListener {
                // 클릭 이벤트 처리
            }
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, // 너비: 부모에 맞춤
                LinearLayout.LayoutParams.WRAP_CONTENT // 높이: 40dp를 픽셀로 변환

            ).apply {
                setMargins(15, 0, 15, 0)
            }
            this.layoutParams = layoutParams
        }
    }
}








