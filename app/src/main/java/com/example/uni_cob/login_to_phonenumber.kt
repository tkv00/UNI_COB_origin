package com.example.uni_cob

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.uni_cob.utility.FirebaseID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class login_to_phonenumber : AppCompatActivity() {
    private lateinit var et_phone: EditText
    private lateinit var et_password: EditText
    private lateinit var btn_login: Button
    private lateinit var btn_findpassword: Button
    private lateinit var btn_signup: Button
    private lateinit var btn_NewPassword:Button
    // Firebase 인증 객체 생성
    private var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_to_phonenumber)
        auth = FirebaseAuth.getInstance()

        et_phone = findViewById(R.id.et_login_phone_phonenumber)
        et_password = findViewById(R.id.et_login_phone_password)
        btn_login = findViewById(R.id.btn_login_phone_login)
        btn_findpassword = findViewById(R.id.btn_login_phone_findpassword)
        btn_signup = findViewById(R.id.btn_login_phone_signup1)
        btn_findpassword=findViewById(R.id.btn_login_phone_findpassword)

        //비밀번호찾기
        btn_findpassword.setOnClickListener{
            val intent=Intent(this,Find_passwordActivity::class.java)
            startActivity(intent)
            finish()

        }

        // TextWatcher를 정의하여 텍스트 변경을 감지
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트 변경 이전의 상태
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트 변경 중에 수행할 작업
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트 변경 이후의 상태
                val phoneNumber = et_phone.text.toString()
                val password = et_password.text.toString()
                val isAllFieldsFilled = phoneNumber.isNotBlank() && password.isNotBlank()

                if (isAllFieldsFilled) {
                    btn_login.isEnabled = true // 버튼 활성화
                    btn_login.setBackgroundResource(R.drawable.skyblue_button_background)
                } else {
                    btn_login.isEnabled = false // 버튼 비활성화
                    btn_login.setBackgroundResource(R.drawable.gray_button_background)
                }
            }
        }
        // TextWatcher를 EditText에 연결
        et_phone.addTextChangedListener(textWatcher)
        et_password.addTextChangedListener(textWatcher)
        btn_login.isEnabled=false
        btn_login.setBackgroundResource(R.drawable.gray_button_background)

        btn_signup.setOnClickListener {
            val intent = Intent(this, SignUpActivity1::class.java)
            startActivity(intent)
            finish()
        }

        btn_login.setOnClickListener {
            val phoneNumber=et_phone.text.toString()
            val password=et_password.text.toString()
            signInWithphoneNumber(phoneNumber,password)
        }
    }

    private fun signInWithphoneNumber(phoneNumber: String, password: String) {
        // Firebase Realtime Database에서 사용자 정보를 가져옵니다.
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")  // "users"는 사용자 정보를 저장하는 노드 이름입니다.

        // 전화번호를 사용하여 사용자 정보를 가져오기 위한 쿼리를 생성합니다.
        val query = usersRef.orderByChild("userPhoneNumber").equalTo(phoneNumber)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val userMap=userSnapshot.value as Map<*,*>?
                        val dbPassword=userMap?.get("password") as String?
                        if(database!=null&&dbPassword==password){
                            // 로그인 성공
                            val intent = Intent(this@login_to_phonenumber, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // 에러 처리
                Toast.makeText(this@login_to_phonenumber, "데이터베이스 오류: " + databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

}

