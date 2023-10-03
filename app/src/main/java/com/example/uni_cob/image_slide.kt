package com.example.uni_cob
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.uni_cob.MyAdapter
import com.example.uni_cob.R
import me.relex.circleindicator.CircleIndicator

class image_slide : AppCompatActivity() {

    private lateinit var mPager: ViewPager2
    private lateinit var pagerAdapter: FragmentStateAdapter
    private lateinit var btn_login:Button
    private lateinit var btn_signup:Button
    private val num_page = 4
    private lateinit var mIndicator: CircleIndicator


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




