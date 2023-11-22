package com.example.uni_cob.writeboard

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.uni_cob.R
import com.example.uni_cob.utility.Board2
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AgoraBoardDetail : AppCompatActivity() {
    private lateinit var  et_name:EditText
    private lateinit var  et_phone:EditText
    private lateinit var et_email:EditText
    private lateinit var close:Button
    private lateinit var submit:Button

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agora_board_detail)

        // Intent에서 데이터 추출
        val title = intent.getStringExtra("TITLE")
        val userName = intent.getStringExtra("NAME")
        val time = intent.getStringExtra("TIME")
        val location = intent.getStringExtra("LOCATION")
        val content = intent.getStringExtra("CONTENT")
        val registerPeople = intent.getIntExtra("PEOPLE", -1) // Default 값으로 -1을 설정
        val date=intent. getStringExtra("FORMATTED_DATE")
        val dday=intent.getStringExtra("DDAY")
        val postid=intent.getStringExtra("POST_ID")
        //신청 제출
        submit=findViewById(R.id.submit)
        submit.setOnClickListener{
            checkApplicationAndSubmit()
        }
        checkIfRegistrationIsOpen()





        close=findViewById(R.id.close4)
        close.setOnClickListener{
            val intent= Intent(this,Agora::class.java)
            startActivity(intent)
        }
        et_name=findViewById(R.id.et_01)
        et_phone=findViewById(R.id.et_02)
        et_email=findViewById(R.id.et_03)
        findViewById<TextView>(R.id.et_time).text = time
        findViewById<TextView>(R.id.et_date).text=date
        findViewById<TextView>(R.id.et_title).text = title
        findViewById<TextView>(R.id.name).text = userName
        findViewById<TextView>(R.id.et_where).text = location
        findViewById<TextView>(R.id.textView31).text = content
        if (registerPeople != -1) {
            findViewById<TextView>(R.id.et_people).text = "최대 수용 가능한 인원 : "+registerPeople.toString()+"명"
        }
        val ddayTextView = findViewById<TextView>(R.id.et_day)
        ddayTextView.text = dday

        // D-day 값에 따라 레이아웃 변경
        dday?.let {
            if (it.startsWith("-")) {
                // D-day가 음수일 때 (과거 이벤트)
                ddayTextView.setBackgroundResource(R.drawable.btn_register) // 예시로 et_gray.xml 배경을 설정

            } else if (it.startsWith("+")) {
                // D-day가 양수일 때 (미래 이벤트)
                ddayTextView.setBackgroundResource(R.drawable.et_gray) // 예시로 et_blue.xml 배경을 설정

            }
        }
        findViewById<TextView>(R.id.et_info).text=registerPeople.toString()+"명 이상 모여야 진행해요."

        if (postid != null) {
            updateCurrentParticipantsDisplay(postid)
        }

    }

    private fun checkApplicationAndSubmit() {
        et_name=findViewById(R.id.et_01)
        et_phone=findViewById(R.id.et_02)
        et_email=findViewById(R.id.et_03)

        // EditText에서 텍스트를 가져옵니다.
        val name = et_name.text.toString()
        val email = et_email.text.toString()
        val phone = et_phone.text.toString()



        // 게시판 ID와 사용자 ID를 가져옵니다.
        val postId = intent.getStringExtra("POST_ID") // 인텐트로부터 게시판 ID를 가져옵니다.
        Log.e("FirebaseError", "$postId")
        val userId = Firebase.auth.currentUser?.uid // 현재 로그인된 사용자의 UID를 가져옵니다.

        if (userId == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_LONG).show()
            return
        }

        // 게시판 신청 정보를 저장할 경로
        val boardRef = postId?.let { Firebase.database.getReference("Board2").child(it).child("applications") }


        // 사용자 신청 정보를 저장할 경로
        val userRef = Firebase.database.getReference("users").child(userId).child("applications")

        // 유저의 신청 정보
        val applicationInfo = mapOf(
            "name" to name,
            "email" to email,
            "phone" to phone,
            "postId" to postId
        )

        // This is the correct place to generate a new key for a database entry.
        val applicationKey = boardRef?.push()?.key
        if (applicationKey == null) {
            // You cannot add a listener here because there's no database operation involved.
            // Instead, you should just log the error and show a toast message.
            Log.e("FirebaseError", "Error: applicationKey is null")
            Toast.makeText(this, "오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show()
            return
        }

        // 게시판 노드에 신청 정보를 저장합니다.
        boardRef.child("applications").child(applicationKey).setValue(applicationInfo)
            .addOnSuccessListener {
                // 성공적으로 신청 정보가 저장되면, 사용자 노드에도 저장합니다.
                val userUpdates = hashMapOf<String, Any>(
                    "/users/$userId/applications/$applicationKey" to applicationInfo,
                    "/Board2/$postId/application/$applicationKey" to applicationInfo
                )
                Firebase.database.reference.updateChildren(userUpdates)
                    .addOnSuccessListener {
                        Toast.makeText(this, "신청이 완료되었습니다.", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "사용자 신청 정보 저장에 실패했습니다.", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseError", "Error writing to database", exception)
                Toast.makeText(this, "오류가 발생했습니다. 다시 시도해주세요: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        if (userId == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_LONG).show()
            return
        }

        if (postId.isNullOrEmpty()) {
            Toast.makeText(this, "게시글 정보가 없습니다.", Toast.LENGTH_LONG).show()
            return
        }

        // 해당 게시판에 대한 사용자의 신청을 확인합니다.
        val boardApplicationsRef = Firebase.database.getReference("Board2").child(postId).child("applications")
        boardApplicationsRef.child(userId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // 사용자가 이미 신청했습니다.
                    Toast.makeText(applicationContext, "이미 신청하셨습니다.", Toast.LENGTH_LONG).show()
                } else {
                    // 사용자가 아직 신청하지 않았으므로 신청을 처리합니다.
                    submitApplication(userId, postId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "데이터베이스 오류: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun submitApplication(userId: String, postId: String) {
        et_name=findViewById(R.id.et_01)
        et_phone=findViewById(R.id.et_02)
        et_email=findViewById(R.id.et_03)
        val name = et_name.text.toString()
        val email = et_email.text.toString()
        val phone = et_phone.text.toString()

        // 유저의 신청 정보
        val applicationInfo = mapOf(
            "name" to name,
            "email" to email,
            "phone" to phone
        )

        // 신청 정보를 저장합니다.
        val boardApplicationsRef = Firebase.database.getReference("Board2").child(postId).child("applications")
        boardApplicationsRef.child(userId).setValue(applicationInfo)
            .addOnSuccessListener {
                updateParticipantCount(postId, userId)
            }
            .addOnFailureListener {
                Toast.makeText(this, "신청에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show()
            }}
    private fun updateParticipantCount(postId: String?, userId: String) {
        val boardRef = postId?.let { Firebase.database.getReference("Board2").child(it) }
        if (boardRef != null) {
            boardRef.runTransaction(object : Transaction.Handler {
                override fun doTransaction(mutableData: MutableData): Transaction.Result {

                    val p = mutableData.getValue(Board2::class.java)
                        ?: return Transaction.success(mutableData) // 데이터가 없으면 새로운 Board2 인스턴스를 사용합니다.

                    // 현재 신청한 사람의 수를 증가시킵니다. 필드가 null이면 0으로 시작합니다.
                    val currentNumberOfPeople = p.currentNumberOfPeople ?: 0
                    p.currentNumberOfPeople = currentNumberOfPeople + 1

                    // 변경된 객체를 다시 MutableData에 설정합니다.
                    mutableData.value = p
                    return Transaction.success(mutableData)
                }

                override fun onComplete(
                    databaseError: DatabaseError?,
                    completed: Boolean,
                    dataSnapshot: DataSnapshot?
                ) {
                    // 오류가 있는 경우 처리합니다.
                    if (databaseError != null) {
                        Toast.makeText(
                            applicationContext,
                            "Error updating participant count: ${databaseError.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        return
                    }

                    // 성공적으로 신청자 수를 업데이트했다면 사용자에게 알립니다.
                    val currentCount = dataSnapshot?.getValue(Board2::class.java)?.currentNumberOfPeople ?: 0
                    Toast.makeText(
                        applicationContext,
                        "신청이 완료되었습니다. 현재 신청자 수: $currentCount",
                        Toast.LENGTH_LONG
                    ).show()
                }

            })
        }
    }
    private fun checkIfRegistrationIsOpen() {
        val postId = intent.getStringExtra("POST_ID")
        if (postId != null) {
            val boardRef = Firebase.database.getReference("Board2").child(postId)
            boardRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val board = snapshot.getValue(Board2::class.java)
                    board?.let {
                        val currentTime = System.currentTimeMillis()
                        val eventDate = it.eventDate?.toLong() ?: Long.MAX_VALUE // eventDate가 null이면 미래의 어떤 시간으로 설정
                        val currentNumberOfPeople = it.currentNumberOfPeople ?: 0
                        val maximumNumberOfPeople = it.numberOfPeople ?: Int.MAX_VALUE


                        if (currentTime > eventDate || currentNumberOfPeople >= maximumNumberOfPeople) {
                            submit.isEnabled = false
                            val message = if (currentTime > eventDate) {
                                "신청 기간이 아닙니다."
                            } else {
                                "신청 인원을 초과했습니다."
                            }
                            Toast.makeText(this@AgoraBoardDetail, message, Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseError", "Failed to read board details", error.toException())
                }
            })
        } else {
            Toast.makeText(this, "게시글 정보를 가져오는데 실패했습니다.", Toast.LENGTH_LONG).show()
        }
    }
    private fun updateCurrentParticipantsDisplay(postId: String) {
        val boardRef = Firebase.database.getReference("Board2").child(postId)
        boardRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val board = snapshot.getValue(Board2::class.java)
                board?.let {
                    val currentParticipants = it.currentNumberOfPeople ?: 0
                    findViewById<TextView>(R.id.textViewParticipantCount).text =
                        "$currentParticipants/${it.numberOfPeople.toString()}"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 에러 처리
                Log.e("FirebaseError", "Failed to read current number of people", error.toException())
            }
        })
    }



}
