package com.example.uni_cob

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uni_cob.writeboard.AgoraBoardDetail
import com.example.uni_cob.writeboard.Board3Detail
import com.example.uni_cob.writeboard.Board4Detail
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ShowMyApplication : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_my_application)

        // 사용자가 신청한 게시물 ID 가져오기
        fetchMyApplicationPostIds(onPostIdsFetched = { postIds ->
            // 게시물 ID를 사용하여 게시물 데이터 가져오기
            fetchPostsFromBoards(postIds, onPostsFetched = { posts ->
                setupRecyclerView(posts)
            })
        }, onError = { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        })



    }
    // 게시판별로 사용자가 신청한 목록을 가져오는 메서드


    // 사용자의 신청 정보에서 postId를 가지고 오는 함수
    private fun fetchMyApplicationPostIds(onPostIdsFetched: (List<String>) -> Unit, onError: (String) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid
        if (userId != null) {
            val userApplicationsRef = FirebaseDatabase.getInstance().getReference("users/$userId/applications")

            userApplicationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // 각 application의 고유 키 아래에 있는 실제 postId 값을 가져옵니다.
                    val postIds = snapshot.children.mapNotNull { it.child("postId").getValue(String::class.java) }
                    onPostIdsFetched(postIds)
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("DatabaseError", "Error fetching user application post IDs: ${error.message}")
                    onError("Error fetching user application post IDs: ${error.message}")
                }
            })
        } else {
            onError("User is not logged in.")
        }
    }


    // postId를 사용하여 board2와 board3에서 게시글을 가져오는 함수
    private fun fetchPostsFromBoards(postIds: List<String>, onPostsFetched: (List<Board>) -> Unit) {
        val boardsRef = FirebaseDatabase.getInstance().getReference()
        val fetchedPosts = mutableListOf<Board>()

        postIds.forEach { postId ->
            // board2와 board3에서 게시글을 검색합니다.
            listOf("Board2", "Board3","Board4").forEach { boardName ->
                val boardRef = boardsRef.child(boardName).child(postId)

                boardRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val post = snapshot.getValue(Board::class.java)
                        post?.let { fetchedPosts.add(it) }

                        // 마지막 postId가 처리되었는지 확인합니다.
                        if (fetchedPosts.size == postIds.size) {
                            onPostsFetched(fetchedPosts)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.e("DatabaseError", "Error fetching user application post IDs: ${error.message}")

                    }
                })
            }
        }
    }
    private fun setupRecyclerView(posts: List<Board>) {
        val recyclerView = findViewById<RecyclerView>(R.id.getapplication)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ApplicationAdapter(posts, this)
    }




}
class ApplicationAdapter(
    private val applicationList: List<Board>,
    private val context: Context
) : RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder>() {

    // ViewHolder와 기타 메서드들...

    inner class ApplicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(board: Board) {
            // itemView의 뷰를 설정하는 코드...

            itemView.setOnClickListener {
                // 게시판 타입에 따라 적절한 상세 화면으로 이동
                val intent = when (board.boardType) {
                    "Board2" -> Intent(context, AgoraBoardDetail::class.java)
                    "Board3" -> Intent(context, Board3Detail::class.java)
                    "Board4"-> Intent(context, Board4Detail::class.java)
                    else -> null
                }
                intent?.let {
                    it.putExtra("BOARD_DETAIL", board)
                    context.startActivity(it)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicationViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ApplicationViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}
