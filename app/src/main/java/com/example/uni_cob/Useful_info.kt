package com.example.uni_cob

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uni_cob.Chatting.HomeFragment
import com.example.uni_cob.Chatting.ProfileFragment
import com.example.uni_cob.utility.Category
import com.google.android.material.bottomnavigation.BottomNavigationView

class Useful_info : AppCompatActivity() {
    private lateinit var btn_to_find_department: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_useful_info)


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



        btn_to_find_department=findViewById(R.id.button2)
        //세부전공 선택페이지로 이동
        btn_to_find_department.setOnClickListener{
            val intent=Intent(this,find_department::class.java)
            startActivity(intent)
            finish()
        }
        val categories = listOf(
            Category(
                R.drawable.image1,
                "교육학",
                "http://www.kocw.net/home/search/majorCourses.do#subject/05"
            ),
            Category(
                R.drawable.info_image_2,
                "의약학",
                "http://www.kocw.net/home/search/majorCourses.do#subject/06"
            ),
            Category(
                R.drawable.info_image_3,
                "사회과학",
                "http://www.kocw.net/home/search/majorCourses.do#subject/02"
            ),
            Category(
                R.drawable.info_image_4,
                "에술체육",
                "http://www.kocw.net/home/search/majorCourses.do#subject/07"
            )
        )

        findViewById<RecyclerView>(R.id.recyclerViewCategories).apply {
            layoutManager =
                LinearLayoutManager(this@Useful_info, LinearLayoutManager.HORIZONTAL, false)
            adapter = CategoryAdapter(this@Useful_info, categories)
        }
    }
}