package com.example.uni_cob.department

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.uni_cob.Chatting.ChatFragment
import com.example.uni_cob.MainActivity
import com.example.uni_cob.R
import com.example.uni_cob.department.Humanities.HumanitiesPlus
import com.example.uni_cob.department.Humanities.LanguageActivity
import com.example.uni_cob.department.NaturalScience.AgricultureActivity
import com.example.uni_cob.department.NaturalScience.BiologicalChemistryActivity
import com.example.uni_cob.department.NaturalScience.HumanEcologyAcctivity
import com.example.uni_cob.department.NaturalScience.MathActivity
import com.example.uni_cob.department.NaturalScience.NaturalSciencePlusActivity
import com.example.uni_cob.department.SocialScience.BusinessActivity
import com.example.uni_cob.department.SocialScience.LawActivity
import com.example.uni_cob.department.SocialScience.SocialPlus
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.bottomnavigation.BottomNavigationView

class find_department : AppCompatActivity() {

    // 각 버튼의 기본 Drawable을 저장하는 Map

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

        //하단바 작동액티비티
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav2)
        bottomNav.setOnItemSelectedListener { item ->


            when (item.itemId) {
                R.id.menu_home -> {
                    // 홈 메뉴 아이템을 눌렀을 때 MainActivity로 이동
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.menu_chat -> {
                    // 채팅 메뉴 아이템을 눌렀을 때 ChatFragment로 교체
                    val chatFragment = ChatFragment.newInstance()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments_frame, chatFragment)
                        .commit()
                    true
                }

                R.id.menu_profile -> {

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
           displayKeywords(educationkeywords)
        }
        btn_mad.setOnClickListener {
            displayKeywords( madicalkeywords)
        }
        btn_soc.setOnClickListener {
           displayKeywords( socialsciencekeywords)
        }
        btn_art.setOnClickListener {
            displayKeywords( artsportskeywords)
        }
        btn_nat.setOnClickListener {
            displayKeywords( naturalsciencekeywords)
        }
        btn_hum.setOnClickListener {
            displayKeywords( humanitieskeywords)
        }
        btn_eng.setOnClickListener {
            displayKeywords(engineeringkeywords)
        }
    }

    private fun displayKeywords(keywords: List<String>) {
        flexboxLayoutKeywords.removeAllViews() // 기존에 있던 뷰들을 제거
        for (keyword in keywords) {
            val keywordButton = createKeywordButton(keyword)
            flexboxLayoutKeywords.addView(keywordButton)
        }
    }



    private fun createKeywordButton(keyword: String): Button {
        return Button(this).apply {
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
            setPadding(
                16.dpToPx(context),
                8.dpToPx(context),
                16.dpToPx(context),
                8.dpToPx(context)
            )
            setOnClickListener {

                navigateToActivity(keyword)
            }
        }
    }

    private fun navigateToActivity(keyword: String) {
        val intent = when (keyword) {
            //예술*체육
            "디자인" -> Intent(this,DesignActivity::class.java)
            "응용예술" -> Intent(this,AppliedArtActivity::class.java)
           "무용•체육"-> Intent(this,DancingActivity::class.java)
           "미술•조형"->Intent(this,ArtisticActivity::class.java)
            "연극영화"-> Intent(this,MovieActivity::class.java)
           "음악"->Intent(this,MusicActivity::class.java)
          "예술•체육기타"->Intent(this,PhsicalActivity::class.java)

            //자연과학
            "농림•수산"->Intent(this,AgricultureActivity::class.java)
            "생물•화학•환경"-> Intent(this,BiologicalChemistryActivity::class.java)
            "생활과학"-> Intent(this,HumanEcologyAcctivity::class.java)
            "수학•물리•천문•지리"-> Intent(this,MathActivity::class.java)
            "자연과학기타"-> Intent(this,NaturalSciencePlusActivity::class.java)

            //인문과학
            "언어•문학"->Intent(this,LanguageActivity::class.java)
            "인문과학기타"-> Intent(this,HumanitiesPlus::class.java)

            //사회과학
            "경영•경제"-> Intent(this,BusinessActivity::class.java)
            "법률"-> Intent(this,LawActivity::class.java)
            "사화과학기타"-> Intent(this,SocialPlus::class.java)



            else -> null

        }
        intent ?. let {
            startActivity(it)
        }?:run{
            Toast.makeText(this,"해당엑티비티를 찾을 수 없습니다 : $keyword",Toast.LENGTH_SHORT).show()
        }
    }


    // dp를 픽셀로 변환하는 확장 함수
    fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }
}


