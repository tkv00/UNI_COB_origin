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
import com.example.uni_cob.utility.Board1
import com.example.uni_cob.utility.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class Board1Adapter(
    private val board1List: List<Board1>,
    private val onClick: (Board1) -> Unit
) : RecyclerView.Adapter<Board1Adapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.class_name)
        val nameTextView: TextView =view.findViewById(R.id.who_class)
        val profileImageView: ImageView =view.findViewById(R.id.register_profile)
        val departmentTextView:TextView=view.findViewById(R.id.et_department)
        // 이곳에 추가적으로 필요한 뷰를 바인딩합니다.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_department, parent, false)
        return ViewHolder(view)
    }

    // ...
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val board1 = board1List[position]
        holder.titleTextView.text = board1.title

        // 게시글 작성자의 uid를 사용하여 사용자 정보를 조회합니다.
        val userRef = board1.userId?.let {
            FirebaseDatabase.getInstance().getReference("users").child(
                it
            )
        }
        if (userRef != null) {
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val user = dataSnapshot.getValue(User::class.java)
                        holder.nameTextView.text = user?.name ?: "이름 미상"
                        holder.departmentTextView.text = user?.department ?: "전공 미상"
                        Glide.with(holder.itemView.context)
                            .load(user?.profileImageUrl)
                            .placeholder(R.drawable.image1)
                            .error(R.drawable.image1)
                            .into(holder.profileImageView)
                    } else {
                        holder.nameTextView.text = "이름 미상"
                        holder.departmentTextView.text = "전공 미상"
                        holder.profileImageView.setImageResource(R.drawable.image1)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("DatabaseError", "Error: ${databaseError.message}")
                    Toast.makeText(holder.itemView.context, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_LONG).show()
                }
            })
        }

        // 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            val intent =
                Intent(holder.itemView.context, Board1Detail::class.java).apply {

                    putExtra("NAME", holder.nameTextView.text.toString()) // 이름
                    putExtra("BOARD_DETAIL1", board1 as Parcelable)
                    putExtra("TITLE",board1.title)
                    putExtra("UID",board1.userId)
                    putExtra("TIME",board1.date)
                    putExtra("CONTENT",board1.content)
                    putExtra("DEPARTMENT",holder.departmentTextView.text.toString())
                    putExtra("POST_ID",board1.postId)
                }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = board1List.size
}


