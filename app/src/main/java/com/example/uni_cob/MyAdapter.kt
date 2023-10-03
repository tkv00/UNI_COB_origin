package com.example.uni_cob

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.uni_cob.utility.Fragment_1
import com.example.uni_cob.utility.Fragment_2
import com.example.uni_cob.utility.Fragment_3
import com.example.uni_cob.utility.Fragment_4

class MyAdapter(private val fa: FragmentActivity, private val count: Int) : FragmentStateAdapter(fa) {

    override fun createFragment(position: Int): Fragment {
        val index = getRealPosition(position)

        return when (index) {
            0 -> Fragment_1()
            1 -> Fragment_2()
            2 -> Fragment_3()
            else -> Fragment_4()
        }
    }

    override fun getItemCount(): Int {
        return 4
    }

    private fun getRealPosition(position: Int): Int {
        return position % count
    }
}
