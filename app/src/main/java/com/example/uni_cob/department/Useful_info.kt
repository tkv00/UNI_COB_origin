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
            "수학과", "물리학과", "화학과", "천문학과", "환경과학과", "식품영양학과", "생명공학과", "생명과학과", "아동가족학과", "원예학과", "지리학과",
            "지질학과", "통계학과", "호텔조리학과", "동물학과", "해양학과", "임학과", "산림학과", "식물학과", "미생물학과", "분자생물학과", "생태학과",
            "유전학과", "신경과학과", "심리학과", "약학과", "간호학과", "물리치료학과", "의학과", "치의학과", "수의학과", "공중보건학과", "사회복지학과",
            "인류학과", "사회학과", "심리치료학과", "언어치료학과", "특수교육학과", "영양학과", "보건학과", "체육학과", "스포츠과학과", "레저스포츠학과",
            "무용학과", "음악학과", "미술학과", "디자인학과", "영문학과", "국문학과", "사학과"
            -> mathApi.getNaturalScience()

            //사회과학
            "경제학과", "정치학과", "사회학과", "인류학과", "심리학과", "교육학과", "법학과", "행정학과", "언론정보학과", "광고홍보학과", "국제관계학과",
            "지역학과", "문화학과", "여성학과", "도시학과", "사회복지학과", "노인복지학과", "청소년학과", "가족학과", "소비자학과", "인사관리학과", "조직학과",
            "의사소통학과", "정보학과",  "아프리카학과", "유럽학과", "라틴아메리카학과", "중동학과", "아시아학과", "평화학과", "개발학과", "환경정책학과", "인권학과",
            "국방학과", "경찰학과", "소방학과", "세무학과", "회계학과", "재무학과", "무역학과", "관광학과", "호텔경영학과", "음식문화학과", "의류학과",  "조경학과", "도시계획학과"
            -> socialApi.getSocialScienceApi()

            //인문과학
            "철학과", "역사학과", "언어학과", "문학과", "한문학과", "영문학과", "불문학과", "독문학과", "중문학과", "일문학과", "러시아학과", "스페인어학과", "이탈리아어학과",
            "포르투갈어학과", "네덜란드어학과", "스칸디나비아어학과", "아랍어학과", "히브리어학과", "터키어학과", "인도어학과", "이란학과", "동남아시아학과", "비교문학과", "문화인류학과",
            "종교학과", "미술사학과", "음악학과", "무용학과", "극예술학과", "영화학과", "신문방송학과", "정보과학과", "라틴어및고대서양어학과", "서양고전학과", "아시아고전학과", "문화콘텐츠학과",
            "서지정보학과", "문헌정보학과", "아프리카학과", "미국학과", "영국학과", "프랑스학과", "독일학과", "러시아및동유럽학과", "스페인및라틴아메리카학과", "이탈리아학과", "포르투갈및브라질학과",
            "북유럽학과", "중동및이슬람학과"
            -> humanApi.getHumanitiesApi()

            //예술 체육
            "미술학과", "회화학과", "조각학과", "판화학과", "서양화학과", "동양화학과", "사진학과", "영상학과", "애니메이션학과", "만화학과", "건축학과", "실내건축학과",
            "산업디자인학과", "시각디자인학과", "패션디자인학과", "섬유디자인학과", "의상디자인학과", "무대디자인학과", "영화학과", "뮤지컬학과", "연극학과", "연기학과",
            "음악학과", "작곡학과", "성악학과", "기악학과", "피아노학과", "현악학과", "관악학과", "타악학과", "국악학과", "민속악학과", "무용학과", "발레학과",
            "현대무용학과", "전통무용학과", "체육학과", "스포츠과학학과", "운동재활학과", "레저스포츠학과", "무도학과", "체육교육학과", "스포츠마케팅학과", "스포츠경영학과",
            "요가학과", "골프학과", "스키학과", "수상스포츠학과", "등산학과", "승마학과"
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