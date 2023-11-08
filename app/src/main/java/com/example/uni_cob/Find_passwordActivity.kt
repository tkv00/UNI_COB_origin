package com.example.uni_cob

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth


class Find_passwordActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var underlineEmail: View
    private lateinit var underlinePhone: View
    private lateinit var find_password:Button
    private var isEmailMode:Boolean=true //현재 이메일 입력 모드인지 확인
    private var defaultEditTextBackground: Int = 0
    private var defaultUnderlineBackground: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_password_phone)

        // EditText와 View(선)를 초기화합니다.
        editText = findViewById(R.id.et_find_password_to_email)
        underlineEmail = findViewById(R.id.underline_email)
        underlinePhone = findViewById(R.id.underline_phone)
        find_password=findViewById(R.id.btn_num1)

        // 기본 색상을 저장합니다. (예를 들어 회색)
        defaultEditTextBackground = ContextCompat.getColor(this, R.color.Gray_02)
        defaultUnderlineBackground = ContextCompat.getColor(this, R.color.Gray_02)

        findViewById<Button>(R.id.find_password_to_email).setOnClickListener {
            setHintAndColor("이메일", underlineEmail)
            underlinePhone.setBackgroundColor(defaultUnderlineBackground) // 다른 선을 기본색으로 변경
            isEmailMode=true //이메일 모드로 설정
        }

        findViewById<Button>(R.id.find_password_to_phone).setOnClickListener {
            setHintAndColor("휴대폰 번호( - 제외)", underlinePhone)
            underlineEmail.setBackgroundColor(defaultUnderlineBackground) // 다른 선을 기본색으로 변경
            isEmailMode=false
        }
        find_password=findViewById(R.id.btn_num1)
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
                val email = editText.text.toString()


                if (email.isNotBlank()) {
                    find_password.isEnabled = true // 버튼 활성화
                    find_password.setBackgroundResource(R.drawable.skyblue_button_background)
                } else {
                    find_password.isEnabled = false // 버튼 비활성화
                    find_password.setBackgroundResource(R.drawable.gray_button_background)
                }
            }
        }
        // TextWatcher를 EditText에 연결
        editText.addTextChangedListener(textWatcher)
        find_password.isEnabled=false
        find_password.setBackgroundResource(R.drawable.gray_button_background)


        // 비밀번호 재설정 요청 처리
       find_password.setOnClickListener {
            val input = editText.text.toString().trim()
            if (isEmailMode) {
                sendPasswordResetEmail(input)
            } else {
                sendPasswordResetSms(input)
            }
        }
    }

    private fun sendPasswordResetSms(input: String) {

    }

    private fun sendPasswordResetEmail(email: String) {
        if (email.isNotEmpty()) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showCustomDialog()
                    } else {
                        Toast.makeText(this, "이메일 전송에 실패했습니다: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setHintAndColor(hint: String, underline: View) {
        // EditText의 힌트와 배경 색상을 변경합니다.
        editText.hint = hint
        underline.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
    }
    private fun showCustomDialog() {
        val dialogView = layoutInflater.inflate(R.layout.find_password_dialog, null)

        // 다이얼로그 설정
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 다이얼로그 크기 조절
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        // 다이얼로그를 보여주기 전에 크기를 조정합니다.
        dialog.setOnShowListener {
            // 다이얼로그의 너비를 화면 너비의 일정 비율로 설정합니다.
            val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
            dialog.window?.setLayout(width, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        }

        // 확인 버튼 클릭 리스너 설정
        dialogView.findViewById<Button>(R.id.dialog_check).setOnClickListener {
            dialog.dismiss()
        }
        Handler(Looper.getMainLooper()).postDelayed({
            if(dialog.isShowing){
                dialog.dismiss()
            }
        },5000)

        // 다이얼로그 표시
        dialog.show()
    }



}
