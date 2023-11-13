package com.example.uni_cob.department

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uni_cob.CategoryAdapter
import com.example.uni_cob.Chatting.ChatFragment
import com.example.uni_cob.MainActivity
import com.example.uni_cob.R
import com.example.uni_cob.department.NaturalScience.ApiService_humanities
import com.example.uni_cob.department.NaturalScience.ApiService_natural_science
import com.example.uni_cob.department.SocialScience.ApiService_SocialScience
import com.example.uni_cob.department.keywords.ApiService
import com.example.uni_cob.department.keywords.Lecture
import com.example.uni_cob.department.keywords.LectureDTO
import com.example.uni_cob.utility.Category
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.JsonParseException
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.io.IOException
import java.util.Collections
import java.util.concurrent.CountDownLatch

class Useful_info : AppCompatActivity() {
    private lateinit var btn_to_find_department: Button
    private val auth = FirebaseAuth.getInstance()
    private lateinit var et_name: TextView
    private lateinit var adapterDepartmentLectures: KeywordsAdapter
    private lateinit var adapterGeneralLectures: KeywordsAdapter
    private lateinit var recyclerViewDepartment: RecyclerView
    private lateinit var recyclerViewGeneral: RecyclerView
    private lateinit var retrofit: Retrofit

    // AuthStateListener 멤버 변수 추가
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        if (firebaseAuth.currentUser != null) {
            // 사용자 정보가 변경되었을 때 UI 업데이트
            updateUserName()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_useful_info)
        // AuthStateListener를 FirebaseAuth 인스턴스에 추가
        auth.addAuthStateListener(authStateListener)

// Initialize UI components and Retrofit
        initializeUI()
        initializeRetrofit()

        // 사용자의 학과 정보를 가져오고 관련 강의 정보를 로드합니다.
        getUserDepartment { department ->
            loadLecturesForDepartment(department)
            loadAndRandomizeGeneralLecturesDaily()
        }



    }

    private fun initializeRetrofit() {
        retrofit = Retrofit.Builder()
            .baseUrl("https://api.odcloud.kr/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun initializeUI(){
        //하단바 작동액티비티
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
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

        btn_to_find_department = findViewById(R.id.button2)
        //세부전공 선택페이지로 이동
        btn_to_find_department.setOnClickListener {
            val intent = Intent(this, find_department::class.java)
            startActivity(intent)

        }
        // TextView 초기화 및 사용자 이름 설정
        et_name = findViewById(R.id.et_name2)

        //카테고리설정
        val categories = listOf(
            Category(
                R.drawable.info_image_1,
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
            ),
            Category(
                R.drawable.info_image_5,
                "자연과학",
                "http://www.kocw.net/home/search/majorCourses.do#subject/04"
            ),
            Category(
                R.drawable.info_image_6,
                "공학",
                "http://www.kocw.net/home/search/majorCourses.do#subject/03"
            ),
            Category(
                R.drawable.info_image_7,
                "인문과학",
                "http://www.kocw.net/home/search/majorCourses.do#subject/01"
            )
        )
        findViewById<RecyclerView>(R.id.recyclerViewCategories).apply {
            layoutManager =
                LinearLayoutManager(this@Useful_info, LinearLayoutManager.HORIZONTAL, false)
            adapter = CategoryAdapter(this@Useful_info, categories)
        }

        // RecyclerViews 초기화
        recyclerViewDepartment = findViewById(R.id.recycler_view2)
        recyclerViewGeneral = findViewById(R.id.recycler_view3)
        // 어댑터 초기화 및 설정
        adapterDepartmentLectures = KeywordsAdapter(arrayListOf())
        adapterGeneralLectures = KeywordsAdapter(arrayListOf())

        recyclerViewDepartment.apply {
            layoutManager = GridLayoutManager(this@Useful_info, 2)
            adapter = adapterDepartmentLectures
        }

        recyclerViewGeneral.apply {
            layoutManager = GridLayoutManager(this@Useful_info, 2)
            adapter = adapterGeneralLectures
        }

    }


    private fun updateGeneralLecturesRecyclerView(allLectures: MutableList<Lecture>) {
        // 현재 날짜를 기준으로 랜덤한 강의를 선택합니다.
        val dailyRandomLectures = allLectures.shuffled().take(2)

        // RecyclerView 어댑터에 데이터를 설정합니다.
        adapterGeneralLectures.updateData(dailyRandomLectures)
    }




    private fun getUserDepartment(callback: (String) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            callback("기본학과") // 사용자가 로그인하지 않았을 경우 기본값 반환
            return
        }

        val database = FirebaseDatabase.getInstance()
        val userDepartmentRef = database.reference.child("users").child(userId).child("department")

        userDepartmentRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val department = snapshot.value as? String ?: "기본학과"
                callback(department) // 콜백을 통해 학과 정보 반환
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error case
                callback("기본학과")
            }
        })
    }

    // 학과에 맞는 API 호출로 강의 정보를 가져옵니다.
    private fun loadLecturesForDepartment(department: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.odcloud.kr/api/") // 실제 서버 URL로 교체해야 합니다.
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder().addInterceptor { chain ->
                    val original = chain.request()
                    val request = original.newBuilder()
                        .header(
                            "authKey",
                            "BLfPhU3GpfIqwUrvP5dMa8vQ+yqXkP/mX+4Cx/nA64012Y77o9wxwLSNB4JwaaGXQqKe4s56GF1FDauYkhI7UQ=="
                        ) // 인증키 앞에 'Bearer '를 붙이고 <>는 제거합니다.
                        .build()
                    chain.proceed(request)
                }.build()
            )
            .build()


        //Api추가 시 여기에 추가
        val mathApi = retrofit.create(ApiService_natural_science::class.java)//자연과학
        val artApi = retrofit.create(ApiService::class.java)//예술 체육
        val socialApi = retrofit.create(ApiService_SocialScience::class.java)//사회과학
        val humanApi = retrofit.create(ApiService_humanities::class.java)


        // 사용자의 학과에 따른 API 호출

        val call = when (department) {

            //자연과학
            "수학과", "물리학과", "화학과", "천문학과", "환경과학과", "식품영양학과",
            "생명공학과", "생명과학과", "아동가족학과", "원예학과", "지리학과", "지질학과", "통계학과", "호텔조리학과"
            -> mathApi.getNaturalScience()

            //사회과학
            "세무회계학과", "국제통상학과", "항공서비스학과", "아동복지학과", "아동복지과", "호텔경영과", "부동산과", "행정학과", "정치외교학과"
            -> socialApi.getSocialScienceApi()

            //인문과학
            "한국어학과", "일본어과", "스페인어학과", "불어불문학과", "러시어학과", "영어영문학과", "외국어학과", "중국어학과"
            -> humanApi.getHumanitiesApi()

            //예술 체육
            "동양학과", "그래픽디자인과", "패션디자인과", "산업디자인학과", "미디어영상학과", "디자인학과", "광고디자인과", "예술학과"
            -> artApi.getArt()


            // ... 다른 학과별 API 호출 ...
            else -> artApi.getArt()
        }

        call.enqueue(object : Callback<LectureDTO> {
            override fun onResponse(call: Call<LectureDTO>, response: Response<LectureDTO>) {
                if (response.isSuccessful) {
                    val lectures = response.body()?.data?: emptyList()
                    val randomLectures = lectures.shuffled().take(2)
                    adapterDepartmentLectures.updateData(randomLectures)
                    recyclerViewDepartment.adapter = adapterDepartmentLectures
                }
            }

            override fun onFailure(call: Call<LectureDTO>, t: Throwable) {
                // 에러 처리
            }


        })
    }





    private fun loadAndRandomizeGeneralLecturesDaily() {
        val naturalScienceApi = retrofit.create(ApiService_natural_science::class.java)
        val call = naturalScienceApi.getNaturalScience()

        call.enqueue(object : Callback<LectureDTO> {
            override fun onResponse(call: Call<LectureDTO>, response: Response<LectureDTO>) {
                if (response.isSuccessful) {
                    val lectures = response.body() ?.data?: emptyList()
                    val dailyRandomLectures = lectures.shuffled().take(2)
                    runOnUiThread {
                        adapterGeneralLectures.updateData(dailyRandomLectures)
                        recyclerViewGeneral.adapter = adapterGeneralLectures
                    }
                } else {
                    handleApiError(response.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<LectureDTO>, t: Throwable) {
                handleApiError(t.message)
            }
        })
    }

    private fun handleApiError(errorMessage: String?)  {
        errorMessage?.let {
            Log.e("ApiError", "Error: $it")
            Toast.makeText(this, "API 오류가 발생했습니다: $it", Toast.LENGTH_LONG).show()
        } ?: run {
            Log.e("ApiError", "Unknown error occurred")
            Toast.makeText(this, "알 수 없는 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
        }
    }



    private fun updateUserName() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val database = FirebaseDatabase.getInstance().getReference("users")
            database.orderByChild("uid").equalTo(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (userSnapshot in snapshot.children) {
                                val name = userSnapshot.child("name").getValue(String::class.java)
                                et_name.text = name ?: "익명"
                                break // 첫 번째 일치하는 사용자 이름을 찾으면 반복을 중단합니다.
                            }
                        } else {
                            et_name.text = "익명"
                            Log.d("Firebase", "No matching user found.")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            this@Useful_info,
                            "데이터 로드 실패: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } else {
            Toast.makeText(this, "로그인 정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }


}