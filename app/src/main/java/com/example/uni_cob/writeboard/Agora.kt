
package com.example.uni_cob.writeboard
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uni_cob.R
import com.example.uni_cob.utility.Board2
import com.example.uni_cob.utility.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class Agora : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var board2List: MutableList<Board2>
    private lateinit var adapter: Board2Adapter
    private lateinit var btn_show_more:Button
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agora)

        recyclerView = findViewById(R.id.agora_list_view_)
        btn_show_more=findViewById(R.id.btn_show_more)
        recyclerView.layoutManager = LinearLayoutManager(this)
        board2List = mutableListOf()

        adapter = Board2Adapter(board2List) { board2 ->
            val intent = Intent(this@Agora, AgoraBoardDetail::class.java).apply {
                putExtra("BOARD_DETAIL2", board2 as Parcelable)
                putExtra("TITLE",board2.title)
                putExtra("UID",board2.userId)
                putExtra("TIME",board2.time)
                putExtra("LOCATION",board2.location)
                putExtra("CONTENT",board2.content)
                putExtra("PEOPLE",board2.numberOfPeople)
            }
            startActivity(intent)
        }
        recyclerView.adapter=adapter
        btn_show_more.setOnClickListener{
            val intent=Intent(this,AgoraBoardAll::class.java)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
        dbRef = FirebaseDatabase.getInstance().getReference("Board2")

// 현재 시간을 기준으로 가장 가까운 3개의 이벤트를 가져옵니다.
        dbRef.orderByChild("eventDate").startAt(System.currentTimeMillis().toDouble())
            .limitToFirst(3).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    board2List.clear()
                    for (dataSnapshot in snapshot.children) {
                        dataSnapshot.getValue(Board2::class.java)?.let { board2 ->
                            board2List.add(board2)
                        }
                    }
                    // 리스트를 현재 시간과 가장 가까운 D-day 순으로 정렬합니다.
                    board2List.sortBy { it.eventDate?.let { date -> Math.abs(System.currentTimeMillis() - date.toLong()) } }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "Error fetching data", Toast.LENGTH_LONG).show()
                }
            })






    }

    private  fun fetchUserInfo(
        uid: String,
        onUserInfoFetched: (userName: String?, userProfileImageUrl: String?) -> Unit
    ) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")

        usersRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    val userName = user?.name
                    val userProfileImageUrl = user?.profileImageUrl
                    onUserInfoFetched(userName, userProfileImageUrl)
                } else {
                    // 일치하는 사용자 정보가 없을 경우 처리
                    onUserInfoFetched(null, null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 오류 처리
                Log.e("FetchUserInfoError", "Error fetching user info: ${error.message}")
                onUserInfoFetched(null, null)
            }
        })
    }

}





