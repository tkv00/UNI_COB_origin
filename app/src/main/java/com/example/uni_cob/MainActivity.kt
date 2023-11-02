package com.example.uni_cob

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uni_cob.Chatting.ChatFragment
import com.example.uni_cob.Chatting.HomeFragment
import com.example.uni_cob.Chatting.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

//private lateinit var homeFragment: HomeFragment
//private lateinit var chatFragment: ChatFragment
//private lateinit var profileFragment: ProfileFragment
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottom_nav=findViewById<BottomNavigationView>(R.id.bottom_nav)
//        bottom_nav.setOnNavigationItemSelectedListener(BottomNavItemSelectedListener)

//        homeFragment = HomeFragment.newInstance()
//        supportFragmentManager.beginTransaction().add(R.id.fragments_frame, homeFragment).commit()

    }
//    private val BottomNavItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener{
//        when(it.itemId){
//            R.id.menu_home -> {
//                homeFragment = HomeFragment.newInstance()
//                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, homeFragment).commit()
//            }
//            R.id.menu_chat -> {
//                chatFragment = ChatFragment.newInstance()
//                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, chatFragment).commit()
//            }
//            R.id.menu_profile -> {
//                profileFragment = ProfileFragment.newInstance()
//                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, profileFragment).commit()
//            }
//        }
//        true
//    }
}

