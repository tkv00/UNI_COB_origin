package com.example.uni_cob.writeboard

import android.content.Intent
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uni_cob.R
import com.example.uni_cob.utility.Board2
import com.example.uni_cob.utility.Board3
import com.example.uni_cob.utility.Board4
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
//원데이클래
class Board4Adapter(
    private val board4List: List<Board4>,
    private val onClick: (Board4) -> Unit
) : RecyclerView.Adapter<Board4Adapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.class_name)
        val dateTextView: TextView = view.findViewById(R.id.class_date)
        val ddayTextView: TextView =view.findViewById(R.id.et_day)
        // 이곳에 추가적으로 필요한 뷰를 바인딩합니다.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_board4, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val board4 = board4List[position]
        holder.titleTextView.text = board4.title
        // D-day 계산
        val currentpeople=board4.currentNumberOfPeople
        val numOfpeople=board4.numberOfPeople
        val eventDate = Date(board4.date)
        val currentTime = System.currentTimeMillis()
        val dday = TimeUnit.MILLISECONDS.toDays(eventDate.time - currentTime)
        holder.ddayTextView.text = "모집중"
        // D-day가 0 이하일 경우 레이아웃 변경
        if (currentpeople != null) {
            if (dday < 0||currentpeople>=numOfpeople) {
                holder.ddayTextView.apply {
                    text = "모집완료"
                    // 텍스트 색상, 배경 등의 스타일 변경
                    setBackgroundResource(R.drawable.et_gray)
                }
            }
        }
        // ... 기타 필요한 데이터 바인딩 ...

        // 날짜 포맷팅
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(board4.date))
        holder.dateTextView.text = formattedDate
        // 클릭 리스너를 아이템 뷰에 설정합니다.
        holder.itemView.setOnClickListener {



            // 클릭 리스너 설정
            holder.itemView.setOnClickListener {
                val intent =
                    Intent(holder.itemView.context, Board4Detail::class.java).apply {
                        putExtra("FORMATTED_DATE", holder.dateTextView.text.toString()) // 날짜
                        putExtra("BOARD_DETAIL2", board4 as Parcelable)
                        putExtra("TITLE",board4.title)
                        putExtra("UID",board4.userId)
                        putExtra("TIME",board4.time)
                        putExtra("CONTENT",board4.content)
                        putExtra("PEOPLE",board4.numberOfPeople)
                        putExtra("DDAY",holder.ddayTextView.text.toString())
                        putExtra("POST_ID",board4.postId)

                    }
                holder.itemView.context.startActivity(intent)
            }
        }

        // 사용자 정보 가져오기 및 바인딩
        val userId = board4.userId
        if (userId != null) {
            val usersRef = FirebaseDatabase.getInstance().getReference("users")
            usersRef.orderByChild("uid").equalTo(userId).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (userSnapshot in snapshot.children) {
                        val userName = userSnapshot.child("name").getValue(String::class.java)


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // 로그 기록 및 사용자 피드백
                    Log.e("DatabaseError", "Error: ${error.message}")
                    Toast.makeText(holder.itemView.context, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_LONG).show()
                }
            })
        } else {

        }

    }


    override fun getItemCount() = board4List.size
}