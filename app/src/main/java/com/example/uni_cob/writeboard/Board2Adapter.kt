package com.example.uni_cob.writeboard

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uni_cob.R
import com.example.uni_cob.department.find_department
import com.example.uni_cob.utility.Board2
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class Board2Adapter(
    private val board2List: List<Board2>,
    private val onClick: (Board2) -> Unit
) : RecyclerView.Adapter<Board2Adapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.class_name)
        val nameTextView:TextView=view.findViewById(R.id.who_class)
        val dateTextView: TextView = view.findViewById(R.id.class_date)
        val locationTextView: TextView = view.findViewById(R.id.where_class)
        val profileImageView:ImageView=view.findViewById(R.id.register_profile)
        val ddayTextView:TextView=view.findViewById(R.id.et_day)
        // 이곳에 추가적으로 필요한 뷰를 바인딩합니다.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_agoralist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val board2 = board2List[position]
        holder.titleTextView.text = board2.title
        holder.locationTextView.text=board2.location
        // D-day 계산
        val eventDate = Date(board2.date)
        val currentTime = System.currentTimeMillis()
        val dday = TimeUnit.MILLISECONDS.toDays(eventDate.time - currentTime)
        holder.ddayTextView.text = "D-${dday}"
        // D-day가 0 이하일 경우 레이아웃 변경
        if (dday <= 0) {
            holder.ddayTextView.apply {
                text = "D+${-dday}"
                // 텍스트 색상, 배경 등의 스타일 변경
                setBackgroundResource(R.drawable.et_gray)
            }
        }
        // ... 기타 필요한 데이터 바인딩 ...

        // 날짜 포맷팅
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(board2.date))
        holder.dateTextView.text = formattedDate
        // 클릭 리스너를 아이템 뷰에 설정합니다.
        holder.itemView.setOnClickListener {



            // 클릭 리스너 설정
            holder.itemView.setOnClickListener {
                    val intent =
                        Intent(holder.itemView.context, AgoraBoardDetail::class.java).apply {
                            putExtra("FORMATTED_DATE", holder.dateTextView.text.toString()) // 날짜
                            putExtra("NAME", holder.nameTextView.text.toString()) // 이름
                            putExtra("BOARD_DETAIL2", board2 as Parcelable)
                            putExtra("TITLE",board2.title)
                            putExtra("UID",board2.userId)
                            putExtra("TIME",board2.time)
                            putExtra("LOCATION",board2.location)
                            putExtra("CONTENT",board2.content)
                            putExtra("PEOPLE",board2.numberOfPeople)
                            putExtra("DDAY",holder.ddayTextView.text.toString())
                        }
                holder.itemView.context.startActivity(intent)
            }
        }

        // 사용자 정보 가져오기 및 바인딩
        val userId = board2.userId
        if (userId != null) {
            val usersRef = FirebaseDatabase.getInstance().getReference("users")
            usersRef.orderByChild("uid").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (userSnapshot in snapshot.children) {
                        val userName = userSnapshot.child("name").getValue(String::class.java)
                        val profileImageUrl = userSnapshot.child("profileImageUrl").getValue(String::class.java)
                        holder.nameTextView.text = userName
                        if (!profileImageUrl.isNullOrEmpty()) {
                            // 이미지 로드
                            Glide.with(holder.itemView.context)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.user) // 예시 placeholder 이미지
                                .error(R.drawable.image1) // 예시 에러 이미지
                                .into(holder.profileImageView)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // 로그 기록 및 사용자 피드백
                    Log.e("DatabaseError", "Error: ${error.message}")
                    Toast.makeText(holder.itemView.context, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_LONG).show()
                }
            })
        } else {
            // userId가 null인 경우 처리
            holder.nameTextView.text = "알 수 없음"
            holder.profileImageView.setImageResource(R.drawable.image1) // 예시 placeholder 이미지
        }

    }


    override fun getItemCount() = board2List.size
}