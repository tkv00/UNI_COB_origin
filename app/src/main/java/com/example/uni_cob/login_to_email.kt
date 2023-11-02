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
import com.example.uni_cob.utility.FirebaseID.email
import com.example.uni_cob.utility.FirebaseID.password
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class login_to_email : AppCompatActivity() {
    private lateinit var et_email:EditText
    private lateinit var et_password:EditText
    private lateinit var btn_login:Button
    private lateinit var btn_findpassword:Button
    private lateinit var btn_signup:Button
    // Firebase 인증 객체 생성
    private var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_to_email)

        et_email = findViewById(R.id.et_login_email)
        et_password = findViewById(R.id.et_login_password)
        btn_login = findViewById(R.id.btn_login_email_login)
        btn_findpassword = findViewById(R.id.btn_login_email_find_password)
        btn_signup = findViewById(R.id.btn_login_email_signup)
        val email = et_email.text.toString()
        val password = et_password.text.toString()

        btn_signup.setOnClickListener{
            val intent=Intent(this,SignUpActivity1::class.java)
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
                val phoneNumber = et_email.text.toString()
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
        et_email.addTextChangedListener(textWatcher)
        et_password.addTextChangedListener(textWatcher)
        btn_login.isEnabled=false
        btn_login.setBackgroundResource(R.drawable.gray_button_background)



        btn_login.setOnClickListener {
            val email=et_email.text.toString()
            val password=et_password.text.toString()
            signIn(email, password)
        }



    }



    private fun signIn(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("User", currentUser)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(this, "이메일 주소 또는 비밀번호를 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "이메일주소 또는 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

}