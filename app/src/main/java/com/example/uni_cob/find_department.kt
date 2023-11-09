package com.example.uni_cob

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.example.uni_cob.Chatting.HomeFragment
import com.example.uni_cob.Chatting.ProfileFragment
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.bottomnavigation.BottomNavigationView

class find_department : AppCompatActivity() {
    private val educationkeywords = listOf(
        "유아교육", "특수교육", "초등교육", "중등교육", "교육일반"
    )
    private val humanitieskeywords = listOf(
        "언어•문학", "인문과학기타"
    )
    private val socialsciencekeywords = listOf(
        "경영•경제", "법률", "사화과학기타"
    )
    private val engineeringkeywords = listOf(
        "건축", "토목•도시", "기계•금속", "전기•전자", "정밀•에너지", "컴퓨터•통신", "산업", "화공", "기타공학"
    )
    private val naturalsciencekeywords = listOf(
        "농림•수산", "생물•화학•환경", "생활과학", "수학•물리•천문•지리", "자연과학기타"
    )
    private val madicalkeywords = listOf(
        "의료", "간호", "역학", "치료보건"
    )
    private val artsportskeywords = listOf(
        "디자인", "응용예술", "무용•체육", "미술•조형", "연극영화", "음악", "예술•체육기타"
    )

    private lateinit var btn_edu: Button//교육학
    private lateinit var btn_mad: Button//의학학
    private lateinit var btn_eng: Button//공학
    private lateinit var btn_art: Button//예술체육
    private lateinit var btn_soc: Button//사회과학
    private lateinit var btn_hum: Button//인문과학
    private lateinit var btn_nat: Button//자연과학


    private lateinit var flexboxLayoutKeywords: FlexboxLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_department)
        //하단바 작동액티비티

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
                    true
                }
                else -> false
            }
        }
        flexboxLayoutKeywords = findViewById(R.id.flexboxLayoutKeywords)
        //버튼 초기화
        btn_edu = findViewById(R.id.btn_dep_01)
        btn_mad = findViewById(R.id.btn_dep_02)
        btn_soc = findViewById(R.id.btn_dep_03)
        btn_art = findViewById(R.id.btn_dep_04)
        btn_nat = findViewById(R.id.btn_dep_05)
        btn_hum = findViewById(R.id.btn_dep_6)
        btn_eng = findViewById(R.id.btn_dep_07)

        //각 버튼 토클
        btn_edu.setOnClickListener {
            toggleKeywords(educationkeywords,flexboxLayoutKeywords)
        }
        btn_mad.setOnClickListener {
            toggleKeywords(madicalkeywords,flexboxLayoutKeywords)
        }
        btn_soc.setOnClickListener {
            toggleKeywords(socialsciencekeywords,flexboxLayoutKeywords)
        }
        btn_art.setOnClickListener {
            toggleKeywords(artsportskeywords,flexboxLayoutKeywords)
        }
        btn_nat.setOnClickListener {
            toggleKeywords(naturalsciencekeywords,flexboxLayoutKeywords)
        }
        btn_hum.setOnClickListener {
            toggleKeywords(humanitieskeywords,flexboxLayoutKeywords)
        }
        btn_eng.setOnClickListener {
            toggleKeywords(engineeringkeywords,flexboxLayoutKeywords)
        }
    }

    // 키워드를 표시하거나 숨기는 함수
    private fun toggleKeywords(keywords: List<String>, flexboxLayout: FlexboxLayout) {
        flexboxLayout.removeAllViews() // 기존 키워드 제거

        keywords.forEach { keyword ->
            val textView = createKeywordTextView(keyword)
            flexboxLayout.addView(textView)
        }
    }

    // 키워드 TextView를 생성하는 함수
    private fun createKeywordTextView(keyword: String): TextView {
        return TextView(this).apply {
            text = keyword
            textSize = 14f
            setTextColor(ContextCompat.getColor(context, R.color.black))
            background = ContextCompat.getDrawable(context, R.drawable.textview_border)
            layoutParams = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                val margin = 8.dpToPx(context)
                setMargins(margin, margin, margin, margin)
            }
            setPadding(16.dpToPx(context), 8.dpToPx(context), 16.dpToPx(context), 8.dpToPx(context))
            setOnClickListener {
                onKeywordClicked(keyword)
            }
        }
    }

    // 키워드 클릭 시 호출되는 함수
    private fun onKeywordClicked(keyword: String) {
        Toast.makeText(this, "Selected: $keyword", Toast.LENGTH_SHORT).show()
        // 여기에 클릭된 키워드에 따라 실행할 작업을 정의합니다.
    }


    // dp를 픽셀로 변환하는 확장 함수
    fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}


