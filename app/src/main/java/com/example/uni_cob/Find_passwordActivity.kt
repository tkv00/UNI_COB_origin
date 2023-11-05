package com.example.uni_cob


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uni_cob.utility.FirebaseID
import com.example.uni_cob.utility.FirebaseID.Companion.password
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class Find_passwordActivity : AppCompatActivity() {

    // Firebase 인증 인스턴스 초기화
    private lateinit var auth: FirebaseAuth
    private lateinit var PhoneNumber: EditText
    private lateinit var et_correctNum: EditText
    private lateinit var btn_correct: Button
    private lateinit var btn_correct_Num: Button
    private lateinit var et_NewPassword:EditText
    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_password)

        PhoneNumber = findViewById(R.id.etPhoneNumber)
        et_correctNum = findViewById(R.id.etCorrectNum)
        et_NewPassword=findViewById(R.id.New_password)
        btn_correct = findViewById(R.id.btnSendVerification)
        btn_correct_Num = findViewById(R.id.btnSendcorrectVerification)

        // 전화번호 입력 상자의 포커스가 변경될 때 호출되는 리스너 설정
        PhoneNumber.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val phone = PhoneNumber.text.toString()
                if (!isPhoneNumberValid(phone)) {
                    PhoneNumber.error = "올바른 전화번호 형식을 입력하세요."
                } else {
                    PhoneNumber.error = null
                }
            }
        }

        // 이메일 입력 상자의 포커스가 변경될 때 호출되는 리스너 설정
        et_NewPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val Newemail = et_NewPassword.text.toString()
                if (!isPasswordValid(Newemail)) {
                    et_NewPassword.error = "올바른 이메일 형식을 입력하세요."
                } else {
                    et_NewPassword.error = null
                }
            }
        }


        // Firebase 인증 초기화
        auth = FirebaseAuth.getInstance()
        val password=et_NewPassword.toString()


        btn_correct.setOnClickListener {
            val phoneNumber = PhoneNumber.text.toString().trim()
            val formattedPhoneNumber = formatPhoneNumber(phoneNumber)
            if (formattedPhoneNumber.isNotEmpty()) {
                sendVerificationCode(formattedPhoneNumber)
            } else {
                Toast.makeText(this@Find_passwordActivity, "휴대폰 번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        btn_correct_Num.setOnClickListener {
            val code = et_correctNum.text.toString().trim()
            if (code.isNotEmpty() && verificationId != null) {
                verifyVerificationCode(code, verificationId!!)
            } else {
                Toast.makeText(this@Find_passwordActivity, "인증번호를 다시 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // 전화번호 형식 검사
    private fun isPhoneNumberValid(phoneNumber: String): Boolean {
        return phoneNumber.length == 11
    }
    private fun sendVerificationCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // 전화번호 설정
            .setTimeout(60L, TimeUnit.SECONDS) // 타임아웃 설정
            .setActivity(this)                 // 액티비티 설정
            .setCallbacks(callbacks)           // 콜백 설정
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
   private fun isPasswordValid (password:String):Boolean{
        return Pattern.matches(
            "^(?=.*[A-Za-z])(?=.*\\d|[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])\\S{8,}\$",
            FirebaseID.password
        )
    }
    private fun isEmailValid(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,3}"
        return Pattern.matches(emailPattern, email)
    }
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(this@Find_passwordActivity, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(id, token)
            verificationId = id
            Toast.makeText(this@Find_passwordActivity, "Code sent to the number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun verifyVerificationCode(code: String, verificationId: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        et_NewPassword=findViewById(R.id.New_password)
        val Newpassword=et_NewPassword.text.toString()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 인증 성공, 비밀번호 재설정 로직으로 진행
                    resetPassword(Newpassword) // 실제 사용 시 적절한 비밀번호로 변경 필요
                } else {
                    Toast.makeText(this@Find_passwordActivity, "Verification failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun resetPassword(newPassword: String) {
        val user = auth.currentUser
        et_NewPassword=findViewById(R.id.New_password)
        val Newpassword=et_NewPassword.text.toString()
        user?.updatePassword(Newpassword)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@Find_passwordActivity, "Password reset successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@Find_passwordActivity, "Password reset failed", Toast.LENGTH_SHORT).show()
            }
        }

    }
    // 전화번호 형식을 검증하고 포맷하는 함수
    private fun formatPhoneNumber(phoneNumber: String): String {
        // 여기서 전화번호를 국제 형식으로 포맷하는 로직을 구현합니다.
        // 예: 한국 전화번호인 경우 앞에 +82를 붙이고 첫 번째 0을 제거합니다.
        return if (phoneNumber.startsWith("0")) {
            "+82${phoneNumber.substring(1)}"
        } else {
            phoneNumber
        }
    }
}

