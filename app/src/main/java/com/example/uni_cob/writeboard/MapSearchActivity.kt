package com.example.uni_cob.writeboard

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.uni_cob.R
import com.example.uni_cob.databinding.ActivityMainBinding
import com.example.uni_cob.databinding.ActivityMapSearchBinding
import com.example.uni_cob.utility.Board2
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapSearchActivity :AppCompatActivity() {

    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "9a460109f49b32a05359c93c59b40d69"  // REST API 키
    }

    private lateinit var binding : ActivityMapSearchBinding
    private val listItems = arrayListOf<ListLayout>()   // 리사이클러 뷰 아이템
    private lateinit var listAdapter :ListAdapter   // 리사이클러 뷰 어댑터
    private var pageNumber = 1      // 검색 페이지 번호
    private var keyword = ""        // 검색 키워드

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listAdapter= ListAdapter(listItems,object :ListAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val mapPoint = MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)
                binding.mapView.setMapCenterPointAndZoomLevel(mapPoint, 1, true)
            }
            override fun onSelectClick(position: Int) {
                val selectedLocation = listItems[position].address
                val returnIntent = Intent().apply {
                    putExtra("selectedAddress", selectedLocation)
                }
                setResult(Activity.RESULT_OK, returnIntent)
                finish()

            }
        })

        // 리사이클러 뷰
        binding.rvList.layoutManager = LinearLayoutManager(this)
        binding.rvList.adapter = listAdapter
        // 리스트 아이템 클릭 시 해당 위치로 이동
        binding.etSearchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트가 변경되기 전에 호출됨
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트가 변경될 때마다 호출됨
                // 검색 버튼을 활성화 상태로 설정
                binding.btnSearch.isEnabled = s?.isNotEmpty() == true

                // 레이아웃 변경 로직을 여기에 구현
                if (s?.isNotEmpty() == true) {
                    // 텍스트가 있을 때 원하는 레이아웃으로 변경
                    changeLayoutForSearch()
                } else {
                    // 텍스트가 없을 때 기본 레이아웃으로 변경
                    setDefaultLayout()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트가 변경된 후에 호출됨
            }
        })


        // 검색 버튼
        binding.btnSearch.setOnClickListener {
            keyword = binding.etSearchField.text.toString()
            pageNumber = 1
            searchKeyword(keyword, pageNumber)
        }

        // 이전 페이지 버튼
        binding.btnPrevPage.setOnClickListener {
            pageNumber--
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber)
        }

        // 다음 페이지 버튼
        binding.btnNextPage.setOnClickListener {
            pageNumber++
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber)
        }
    }
    private fun changeLayoutForSearch() {
        // 검색 레이아웃으로 변경하는 코드
    }

    private fun setDefaultLayout() {
        // 기본 레이아웃으로 변경하는 코드
    }

    // 키워드 검색 함수
    private fun searchKeyword(keyword: String, page: Int) {
        try {
            val retrofit = Retrofit.Builder() // Retrofit 구성
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val api = retrofit.create(KakaoAPI::class.java) // 통신 인터페이스를 객체로 생성
            val call = api.getSearchKeyword("KakaoAK $API_KEY", keyword, page) // 검색 조건 입력

            // API 서버에 요청
            call.enqueue(object : Callback<ResultSearchKeyword> {
                override fun onResponse(call: Call<ResultSearchKeyword>, response: Response<ResultSearchKeyword>) {
                    if (response.isSuccessful) {
                        // 통신 성공, 응답 코드가 200-300 사이일 때
                        response.body()?.let {
                            // 응답 바디가 null이 아닐 때
                            Log.d("API Response", "Response: $it")
                            addItemsAndMarkers(it)
                        } ?: run {
                            // 응답 바디가 null일 때
                            Log.w("API Response", "Response body is null")
                        }
                    } else {
                        // 통신은 성공했지만, 응답 코드가 200-300 사이가 아닐 때 (예: 404, 500 등)
                        Log.e("API Response", "Response not successful: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                    // 통신 실패 (예: 인터넷 연결 문제, 서버 다운 등)
                    Log.e("API Response", "Communication error: ${t.localizedMessage}", t)
                }

            })
        } catch (e: Exception) {
            Log.e("LocalSearch", "searchKeyword 함수에서 오류 발생: ${e.localizedMessage}")
        }
    }


    // 검색 결과 처리 함수
    private fun addItemsAndMarkers(searchResult: ResultSearchKeyword?) {
        try{
        if (!searchResult?.documents.isNullOrEmpty()) {
            // 검색 결과 있음
            listItems.clear()                   // 리스트 초기화
            binding.mapView.removeAllPOIItems() // 지도의 마커 모두 제거
            for (document in searchResult!!.documents) {
                // 결과를 리사이클러 뷰에 추가
                val item = ListLayout(
                    document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble(),
                    )
                listItems.add(item)

                // 지도에 마커 추가
                val point = MapPOIItem()
                point.apply {
                    itemName = document.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(document.y.toDouble(),
                        document.x.toDouble())
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }
                binding.mapView.addPOIItem(point)
            }
            listAdapter.notifyDataSetChanged()

            binding.btnNextPage.isEnabled = !searchResult.meta.is_end // 페이지가 더 있을 경우 다음 버튼 활성화
            binding.btnPrevPage.isEnabled = pageNumber != 1             // 1페이지가 아닐 경우 이전 버튼 활성화

        } else {
            // 검색 결과 없음
            Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }catch (e:Exception){
        Log.e("LocalSearch", "addItemsAndMarkers 함수에서 오류 발생: ${e.localizedMessage}")
        }}

}






