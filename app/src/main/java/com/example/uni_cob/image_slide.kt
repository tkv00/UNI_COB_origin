package com.example.uni_cob
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.adapter.FragmentStateAdapter
import me.relex.circleindicator.CircleIndicator

class image_slide : AppCompatActivity() {

    private lateinit var mPager: ViewPager2
    private lateinit var pagerAdapter: FragmentStateAdapter
    private lateinit var btn_login:Button
    private lateinit var btn_signup:Button
    private val num_page = 4
    private lateinit var mIndicator: CircleIndicator
    private lateinit var BigText:TextView
    private lateinit var SmallText:TextView

    private val bigTexts = arrayOf("전공 관련 문제를 해결해 보세요", "원하는 전공강의를 추천해 드릴게요", "아고라를 통해서 생각을 표현할 수 있어요", "전공자들의 원데이 클래스에 참여해 보세요")
    private val smallTexts = arrayOf("전공대화", "알쓸전잡", "아고라", "원데이 클래스")


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_slide)


        /**
         * 가로 슬라이드 뷰 Fragment
         */

        // ViewPager2
        mPager = findViewById(R.id.viewpager)

        // Adapter
        pagerAdapter = MyAdapter(this, num_page)
        mPager.adapter = pagerAdapter

        // Indicator
        mIndicator = findViewById(R.id.indicator)
        mIndicator.setViewPager(mPager)
        mIndicator.createIndicators(num_page, 0)

        // ViewPager Setting
        mPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        //텍스트뷰초기화
        BigText=findViewById(R.id.textView3)
        SmallText=findViewById(R.id.textView6)

        /**
         * 이 부분 조정하여 처음 시작하는 이미지 설정.
         * 2000장 생성하였으니 현재위치 1002로 설정하여
         * 좌 우로 슬라이딩 할 수 있게 함. 거의 무한대로
         */

        mPager.currentItem = 1 // 시작 지점
        mPager.offscreenPageLimit = 4 // 최대 이미지 수

        mPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (positionOffsetPixels == 0) {
                    mPager.setCurrentItem(position)
                }
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mIndicator.animatePageSelected(position % num_page)

                val realPosition=position%num_page
                BigText.text=bigTexts[realPosition]
                SmallText.text=smallTexts[realPosition]
            }
        })

        //로그인 ,회원가입 버튼 초기화
        btn_login=findViewById(R.id.slide_login)
        btn_signup=findViewById(R.id.slide_signup)
        //회원가입 페이지로 이동
        btn_signup.setOnClickListener{
            val intent=Intent(this,SignUpActivity1::class.java)
            startActivity(intent)
        }
        //로그인 페이지로 이동
        btn_login.setOnClickListener{
            val intent=Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
    }



}

private fun CircleIndicator.setViewPager(mPager: ViewPager2?) {

}




