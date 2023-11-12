package com.example.uni_cob.department
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uni_cob.R
import com.example.uni_cob.department.keywords.ApiService
import com.example.uni_cob.department.keywords.LectureDTO
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class ArtisticActivity :AppCompatActivity(){
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var btn: Button
    private lateinit var adapter: KeywordsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_department_design)

        // DrawerLayout과 ActionBarDrawerToggle 설정
        drawerLayout = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // ActionBar 홈 버튼 활성화
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Hamburger 버튼 설정
        btn = findViewById(R.id.hamburger)
        btn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // RecyclerView 어댑터 초기화 및 설정
        adapter = KeywordsAdapter(arrayListOf())
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view1)
        recyclerView.layoutManager = GridLayoutManager(this, 2) // 두 열 그리드 레이아웃
        recyclerView.adapter = adapter


        // BottomNavigationView 설정
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        // 제목 설정
        val textViewTitle = findViewById<TextView>(R.id.text3)
        textViewTitle.text = intent.getStringExtra(ARG_TITLE) ?: "미술•조형"

        // API 데이터 요청
        requestApi()
    }

    private fun requestApi() {
        val service = RetrofitClient.getInstance().create(ApiService::class.java)
        service.getLectureDetail().enqueue(object : Callback<LectureDTO> {
            override fun onResponse(call: Call<LectureDTO>, response: Response<LectureDTO>) {
                if (response.isSuccessful) {
                    // "디자인" 중분류에 해당하는 데이터만 필터링
                    // "디자인" 중분류에 해당하는 데이터만 필터링
                    val designLectures = response.body()?.data?.filter {
                        it.classification == "미술ㆍ조형"
                    } ?: listOf()
                    // 어댑터 데이터 업데이트
                    adapter.updateData(designLectures)

                } else {
                    Log.e("AppliedAtrActivity", "Response not successful: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<LectureDTO>, t: Throwable) {
                Log.e("AppliedAtrActivity", "Network request failed", t)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val ARG_TITLE = "title"
    }
}
