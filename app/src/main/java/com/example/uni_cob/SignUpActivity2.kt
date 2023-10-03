package com.example.uni_cob

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.uni_cob.utility.FirebaseID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date

class SignUpActivity2 : AppCompatActivity() {

    private lateinit var et_department: EditText
    private lateinit var et_stNumber: EditText
    private lateinit var et_school: EditText
    private lateinit var btn_signup: Button
    private lateinit var btn_check_school: Button
    private lateinit var imageUri: Uri

    private lateinit var launcher: ActivityResultLauncher<Intent>

    private val CAMERA_PERMISSION_REQUEST_CODE = 123

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up2)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        val cameraPermission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(
                this,
                cameraPermission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(cameraPermission),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }

        et_department = findViewById(R.id.et_department)
        et_stNumber = findViewById(R.id.et_stNumber)
        et_school = findViewById(R.id.et_school)
        btn_signup = findViewById(R.id.btn_signup)
        btn_check_school = findViewById(R.id.btn_check_school)

        launcher = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intentData: Intent? = result.data
                val imageBitmap = intentData?.extras?.get("data") as? Bitmap
                imageBitmap?.let { bitmap ->
                    uploadImageToFirebaseStorage(bitmap)
                } ?: showMessage("이미지를 가져오지 못했습니다.")
            }
        }

        btn_check_school.setOnClickListener {
            launchCameraForAuthentication()
        }

        btn_signup.setOnClickListener {
            // 가입 버튼 클릭 시 수행할 작업 추가
            // 여기에서 사용자를 로그인하고 토큰을 가져와서 Firebase 작업을 수행합니다.
            loginUserAndPerformFirebaseOperations()
        }
    }

    private fun loginUserAndPerformFirebaseOperations() {
        val department = et_department.text.toString()
        val stNumber = et_stNumber.text.toString()
        val schoolName = et_school.text.toString()

        val email = "user@example.com" // 사용자 이메일
        val password = "password" // 사용자 비밀번호

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { signInTask ->
                if (signInTask.isSuccessful) {
                    // 로그인 성공
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        // 사용자가 로그인된 상태에서 토큰을 가져옵니다.
                        currentUser.getIdToken(true)
                            .addOnCompleteListener { tokenTask ->
                                if (tokenTask.isSuccessful) {
                                    val idToken = tokenTask.result?.token
                                    if (idToken != null) {
                                        // Firebase 작업 수행
                                        uploadImageAndUserInfoToFirebase(
                                            department,
                                            stNumber,
                                            schoolName,
                                            idToken
                                        )
                                    } else {
                                        showMessage("토큰이 null입니다.")
                                    }
                                } else {
                                    showMessage("토큰을 가져오는 데 실패했습니다.")
                                }
                            }
                    } else {
                        showMessage("사용자가 null입니다.")
                    }
                } else {
                    showMessage("로그인 실패: ${signInTask.exception?.message}")
                }
            }
    }

    private fun uploadImageAndUserInfoToFirebase(
        department: String,
        stNumber: String,
        schoolName: String,
        idToken: String
    ) {
        // Firebase Storage에 이미지 업로드
        val imageRef = storageRef.child("images/${generateImageFileName(schoolName, stNumber)}")
        val baos = ByteArrayOutputStream()
        // 이미지를 바이트 배열로 변환
        imageUri?.let {
            val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, it)
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        }
        val data = baos.toByteArray()

        imageRef.putBytes(data)
            .addOnCompleteListener { uploadTask ->
                if (uploadTask.isSuccessful) {
                    // 이미지 업로드 성공
                    imageRef.downloadUrl.addOnCompleteListener { urlTask ->
                        if (urlTask.isSuccessful) {
                            val imageUrl = urlTask.result.toString()
                            // Firebase Realtime Database에 사용자 정보와 이미지 URL 저장
                            val currentUser = auth.currentUser
                            val userId = currentUser?.uid ?: ""
                            if (userId.isNotEmpty()) {
                                val userInfo = FirebaseID.User(
                                    department,
                                    "",
                                    "",
                                    "",
                                    "",
                                    stNumber,
                                    schoolName
                                )
                                database.child("users").child(userId).setValue(userInfo)
                                    .addOnCompleteListener { databaseTask ->
                                        if (databaseTask.isSuccessful) {
                                            showMessage("가입이 완료되었습니다.")
                                            finish()
                                        } else {
                                            showMessage("사용자 정보 업로드 실패: ${databaseTask.exception?.message}")
                                        }
                                    }
                            } else {
                                showMessage("사용자 정보를 저장할 수 없습니다.")
                            }
                        } else {
                            showMessage("이미지 URL 가져오기 실패: ${urlTask.exception?.message}")
                        }
                    }
                } else {
                    showMessage("이미지 업로드 실패: ${uploadTask.exception?.message}")
                }
            }
    }

    private fun launchCameraForAuthentication() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        launcher.launch(cameraIntent)
    }

    @SuppressLint("SimpleDateFormat")
    private fun generateImageFileName(schoolName: String, stNumber: String): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmm")
        val currentDateAndTime: String = sdf.format(Date())
        return "image_${schoolName}_${stNumber}_$currentDateAndTime.jpg"
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun uploadImageToFirebaseStorage(imageBitmap: Bitmap) {
        // 이미지 업로드를 위한 코드
        // imageBitmap를 Firebase Storage에 업로드하고, 그 후에 Firebase Realtime Database에 사용자 정보 및 이미지 URL 저장
        // 필요한 코드는 이미 작성되어 있습니다.
    }
}
