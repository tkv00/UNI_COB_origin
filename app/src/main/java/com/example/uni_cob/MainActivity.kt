package com.example.uni_cob

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.example.uni_cob.Chatting.ChatFragment
import com.example.uni_cob.Chatting.HomeFragment
import com.example.uni_cob.Chatting.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.checkerframework.common.subtyping.qual.Bottom

class MainActivity : AppCompatActivity() {

    // 현재 표시되고 있는 프래그먼트를 추적하기 위한 변수
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener(BottomNavItemSelectedListener)

        // 처음 시작할 때 homeFragment를 보여줌
        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.menu_home
        }
    }

    private val BottomNavItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val mainContent:NestedScrollView=findViewById(R.id.nestedScrollView)
        val fragmentFrame:FrameLayout=findViewById(R.id.fragments_frame)


        when (item.itemId) {
            R.id.menu_home -> {
               //프래그먼트가 보여지고 있으면 숨김
                fragmentFrame.visibility= View.GONE
                //메인 컨텐츠 보여줌
                mainContent.visibility=View.VISIBLE
                true
            }
            R.id.menu_chat -> {
                // HomeFragment로 이동
                val homeFragment = HomeFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, homeFragment).commit()
                //메인 컨텐츠 숨김
                mainContent.visibility=View.GONE
                //프래그먼트 보여줨
                fragmentFrame.visibility=View.VISIBLE
                true
            }
            R.id.menu_profile -> {
                // ProfileFragment로 이동
                val profileFragment = ProfileFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, profileFragment).commit()
                //메인 컨텐츠 숨김
                mainContent.visibility=View.GONE
                //프래그먼트 보여줌
                fragmentFrame.visibility=View.VISIBLE
                true
            }
            else->false
        }

    }
}

