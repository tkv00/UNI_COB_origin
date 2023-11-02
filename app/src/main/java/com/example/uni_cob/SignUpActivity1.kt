package com.example.uni_cob
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.uni_cob.R
import com.example.uni_cob.SignUpActivity2
import com.example.uni_cob.utility.FirebaseID
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import java.util.regex.Pattern

class SignUpActivity1 : AppCompatActivity(), View.OnClickListener {
    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    private val mAuth = FirebaseAuth.getInstance()
    private val mStore = FirebaseFirestore.getInstance()

    private lateinit var et_register_email: EditText
    private lateinit var et_register_pw: EditText
    private lateinit var et_register_name: EditText
    private lateinit var et_register_phone: EditText
    private lateinit var btn_register_button: Button
    private lateinit var et_confirm_password: EditText
    private lateinit var checkEmailButton: Button
    private lateinit var imageView: ImageView
    private lateinit var imageButton: ImageButton

    private var isEmailValid = false
    private var isEmailAvailable = false

    private var ImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up01)

        et_confirm_password = findViewById(R.id.et_correctpassword1)
        et_register_email = findViewById(R.id.et_email)
        et_register_pw = findViewById(R.id.inputPassword)
        et_register_name = findViewById(R.id.inputName)
        et_register_phone = findViewById(R.id.phoneNumber)
        btn_register_button = findViewById(R.id.btn_signup)
        checkEmailButton = findViewById(R.id.btn_checkduplicateEmail)
        imageView = findViewById(R.id.image_profile)
        imageButton = findViewById(R.id.btn_profile_register)
         val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 변경 전 처리
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트 변경 중 처리
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트 변경 후 처리
                updateButtonState()
            }
        }


        // EditText 필드 내용이 변경될 때마다 버튼 상태 업데이트
        et_register_name.addTextChangedListener(textWatcher)
        et_register_phone.addTextChangedListener(textWatcher)
        et_register_email.addTextChangedListener(textWatcher)
        et_register_pw.addTextChangedListener(textWatcher)
        et_confirm_password.addTextChangedListener(textWatcher)

        // 초기에 회원가입 버튼을 비활성화
        btn_register_button.isEnabled = false

        // 전화번호 입력 상자의 포커스가 변경될 때 호출되는 리스너 설정
        et_register_phone.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val phone = et_register_phone.text.toString()
                if (!isPhoneNumberValid(phone)) {
                    et_register_phone.error = "올바른 전화번호 형식을 입력하세요."
                } else {
                    et_register_phone.error = null
                }
            }
        }

        // 이메일 입력 상자의 포커스가 변경될 때 호출되는 리스너 설정
        et_register_email.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val email = et_register_email.text.toString()
                if (!isEmailValid(email)) {
                    et_register_email.error = "올바른 이메일 형식을 입력하세요."
                } else {
                    et_register_email.error = null
                }
            }
        }

        // 이미지 버튼 클릭 리스너 설정
        imageButton.setOnClickListener {
            selectProfileImage()
        }

        // 이메일 유효성 검사 및 중복 확인 버튼 활성화
        et_register_email.addTextChangedListener {
            isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(et_register_email.text.toString()).matches()
            checkEmailButton.isEnabled = isEmailValid
        }

        checkEmailButton.setOnClickListener(this)
        btn_register_button.setOnClickListener(this)

        // SignUpActivity1.kt





    }

    // "가입하기" 버튼 활성화 함수
    private fun updateButtonState() {
        val name = et_register_name.text.toString()
        val email = et_register_email.text.toString()
        val password = et_register_pw.text.toString()
        val cr_password = et_confirm_password.text.toString()
        val phone = et_register_phone.text.toString()

        val isAllFieldsFilled =
            name.isNotBlank() && email.isNotBlank() && password.isNotBlank()
                    && cr_password.isNotBlank() && phone.isNotBlank()

        val isPasswordValid =
            Pattern.matches(
                "^(?=.*[A-Za-z])(?=.*\\d|[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])\\S{8,}\$",
                password
            )

        // EditText 필드에 대한 참조를 가져오기
        et_register_email = findViewById(R.id.et_email)

        // 이메일 유효성 검사 및 중복 확인 버튼 초기 비활성화
        et_register_email.addTextChangedListener {
            isEmailValid =
                android.util.Patterns.EMAIL_ADDRESS.matcher(et_register_email.text.toString())
                    .matches()
            checkEmailButton.isEnabled = isEmailValid

            // 이메일 유효성 검사 결과에 따라 버튼 배경 설정
            if (isEmailValid) {
                checkEmailButton.setBackgroundResource(R.drawable.skyblue_button_background) // 검정색 배경
            } else {
                checkEmailButton.setBackgroundResource(R.drawable.gray_button_background) // 회색 배경
            }
        }


        // 버튼 상태 업데이트
        btn_register_button.isEnabled = isAllFieldsFilled && isPasswordValid

        // 버튼 색상 업데이트
        if (isAllFieldsFilled && isPasswordValid) {
            btn_register_button.setBackgroundResource(R.drawable.skyblue_button_background) // 검정 배경
        } else {
            btn_register_button.setBackgroundResource(R.drawable.gray_button_background) // 회색 배경
        }
    }

    private fun checkDuplicateEmail(email: String) {
        mStore.collection("User")
            .whereEqualTo(FirebaseID.email, email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // 중복되지 않은 이메일
                    isEmailAvailable = true
                    // 버튼을 활성화
                    btn_register_button.isEnabled = true
                    Toast.makeText(this, "사용 가능한 이메일입니다.", Toast.LENGTH_SHORT).show()
                } else {
                    // 중복된 이메일
                    isEmailAvailable = false
                    // 버튼을 비활성화
                    btn_register_button.isEnabled = false
                    Toast.makeText(this, "중복된 이메일 주소입니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "중복 확인 오류: $e", Toast.LENGTH_SHORT).show()
            }
    }

    // 이메일 형식 검사
    private fun isEmailValid(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,3}"
        return Pattern.matches(emailPattern, email)
    }

    // 전화번호 형식 검사
    private fun isPhoneNumberValid(phoneNumber: String): Boolean {
        return phoneNumber.length == 11
    }

    // 프로필 이미지 선택
    private fun selectProfileImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        // 이미지 선택을 위한 액티비티 시작
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {
                ImageUri = data.data
                imageView.setImageURI(ImageUri)
            }
        }
    }

    fun uploadImageToFirebaseStorage(uri: Uri, onSuccess: (imageURL: String) -> Unit) {
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                ref.downloadUrl.addOnSuccessListener { uri ->
                    val imageURL = uri.toString()
                    onSuccess(imageURL)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "이미지 업로드 실패: $e", Toast.LENGTH_LONG).show()
            }
    }

    private fun registerUser(imageUrl: String?) {
        val email = et_register_email.text.toString().trim()
        val password = et_register_pw.text.toString().trim()
        val name = et_register_name.text.toString().trim()
        val phoneNumber = et_register_phone.text.toString().trim()
        val confirmPassword = et_confirm_password.text.toString().trim()

        if (email.isNotEmpty() && password.isNotEmpty() &&
            name.isNotEmpty() && phoneNumber.isNotEmpty() && confirmPassword.isNotEmpty()
        ) {
            if (password == confirmPassword) {
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                        if (task.isSuccessful) {
                            val user = mAuth.currentUser

                            // 회원가입 성공시 이메일 중복 상태 초기화
                            isEmailAvailable = false

                            val userMap = hashMapOf(
                                FirebaseID.userPhoneNumber to phoneNumber,
                                FirebaseID.email to email,
                                FirebaseID.password to password,
                                FirebaseID.name to name,
                                FirebaseID.department to "",
                                FirebaseID.schoolName to "",
                                FirebaseID.stNumber to "",
                                "profileImageUrl" to imageUrl,
                                FirebaseID.selectedGrade to "",
                                FirebaseID.uid to ""
                            )

                            mStore.collection("User")
                                .document(user?.uid ?: "")
                                .set(userMap, SetOptions.merge())
                                .addOnSuccessListener {
                                    Toast.makeText(this, "학교인증하기로 이동", Toast.LENGTH_LONG).show()
                                    // 회원가입이 성공하면 로그인 페이지로 이동
                                    val intent = Intent(this, SignUpActivity2::class.java)
                                    intent.putExtra("email", et_register_email.text.toString())
                                    intent.putExtra("password", et_register_pw.text.toString())
                                    intent.putExtra("phoneNumber", et_register_phone.text.toString())
                                    intent.putExtra("name", et_register_name.text.toString())
                                    intent.putExtra("profileImageUrl",imageUrl)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        this,
                                        "회원가입 실패: $e",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        } else {
                            Toast.makeText(this, "회원가입 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "모든 필수 정보를 입력하세요.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_checkduplicateEmail -> {
                val email = et_register_email.text.toString().trim()
                checkDuplicateEmail(email)
            }

            R.id.btn_signup -> {
                if (isEmailAvailable) {
                    if (ImageUri != null) {
                        uploadImageToFirebaseStorage(ImageUri!!) { imageURL ->
                            registerUser(imageURL)
                        }
                    } else {
                        Toast.makeText(this, "프로필 이미지를 선택해주세요.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "이메일 중복을 확인하세요.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}


