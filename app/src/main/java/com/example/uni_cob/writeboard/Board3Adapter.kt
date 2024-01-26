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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
//원데이클래
class Board3Adapter(
    private val board3List: List<Board3>,
    private val onClick: (Board3) -> Unit
) : RecyclerView.Adapter<Board3Adapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.class_name)
        val dateTextView: TextView = view.findViewById(R.id.class_date)
        // 이곳에 추가적으로 필요한 뷰를 바인딩합니다.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_board3, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val board3 = board3List[position]
        holder.titleTextView.text = board3.title






        // 날짜 포맷팅
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(board3.date))
        holder.dateTextView.text = formattedDate
        // 클릭 리스너를 아이템 뷰에 설정합니다.
        holder.itemView.setOnClickListener {
            // 클릭 리스너 설정
            holder.itemView.setOnClickListener {
                val intent =
                    Intent(holder.itemView.context, Board3Detail::class.java).apply {
                        putExtra("FORMATTED_DATE", holder.dateTextView.text.toString()) // 날짜
                        putExtra("BOARD_DETAIL2", board3 as Parcelable)
                        putExtra("TITLE",board3.title)
                        putExtra("UID",board3.userId)
                        putExtra("TIME",board3.time)
                        putExtra("CONTENT",board3.content)
                        putExtra("PEOPLE",board3.numberOfPeople)
                        putExtra("POST_ID",board3.postId)
                        putExtra("MONEY",board3.money)
                        putExtra("LOCATION",board3.location)
                    }
                holder.itemView.context.startActivity(intent)
            }
        }

        // 사용자 정보 가져오기 및 바인딩
        val userId = board3.userId
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


    override fun getItemCount() = board3List.size
}