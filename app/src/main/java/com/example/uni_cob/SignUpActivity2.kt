package com.example.uni_cob
//이 클래스 사용은 아직 나중으로 보류중

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.uni_cob.utility.FirebaseID
import com.example.uni_cob.utility.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.Date


class SignUpActivity2 : AppCompatActivity() {

    private lateinit var et_department: EditText
    private lateinit var et_stNumber: EditText
    private lateinit var et_school: EditText
    private lateinit var btn_signup: Button
    private lateinit var btn_check_school: Button
    private lateinit var spinner: Spinner
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var phoneNumber: String
    private lateinit var name: String
    private lateinit var imageURL: String
    private var imageUri: Uri? = null

    private lateinit var launcher: ActivityResultLauncher<Intent>

    private val CAMERA_PERMISSION_REQUEST_CODE = 123

    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up2)
        email = intent.getStringExtra("email") ?: throw IllegalArgumentException("Email is required")
        password = intent.getStringExtra("password") ?: throw IllegalArgumentException("password is required")
        phoneNumber = intent.getStringExtra("phoneNumber") ?: throw IllegalArgumentException("phoneNumber is required")
        name = intent.getStringExtra("name") ?: throw IllegalArgumentException("name is required")
        imageURL = intent.getStringExtra("profileImageUrl") ?: throw IllegalArgumentException("imageURL is required")

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
        spinner = findViewById(R.id.spn_SPList)
        val safekey=imageUri.toString().replace(Regex("[.#$\\[\\]/]"),"")

        val departments = arrayOf("학년 선택", "1학년", "2학년", "3학년", "4학년", "4+학년")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, departments)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedDepartment = departments[position]
                // selectedDepartment를 데이터베이스에 저장하는 코드를 여기에 작성합니다.
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무것도 선택되지 않았을 때의 처리를 여기에 작성합니다.
            }
        }

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intentData: Intent? = result.data
                val imageBitmap = intentData?.extras?.get("data") as? Bitmap
                imageBitmap?.let { bitmap ->
                    val schoolName = et_school.text.toString().trim()
                    val stNumber = et_stNumber.text.toString().trim()
                    uploadImageToFirebaseStorage(bitmap, schoolName, stNumber)
                } ?: showMessage("이미지를 가져오지 못했습니다.")
            }
        }

        btn_check_school.setOnClickListener {
            launchCameraForAuthentication()
        }

        btn_signup.setOnClickListener {
            uploadUserInfoToFirebase()
        }

        btn_signup.isEnabled = false // 초기에 버튼 비활성화

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkInputs()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }

        et_department.addTextChangedListener(textWatcher)
        et_stNumber.addTextChangedListener(textWatcher)
        et_school.addTextChangedListener(textWatcher)
    }

    private fun uploadUserInfoToFirebase() {
        val department = et_department.text.toString()
        val stNumber = et_stNumber.text.toString()
        val schoolName = et_school.text.toString()

        val authUid=FirebaseAuth.getInstance().currentUser?.uid
        if(authUid==null){
            showMessage("인증된 사요자가 아닙니다.")
            return
        }
        val profileImageUrl=imageUri.toString()

        val selectedGrade = spinner.selectedItem.toString()
        val userInfo = User(
            department=department,
            email=email,
            name=name,
            password=password,
            uid = authUid,
            schoolName=schoolName,
            selectedGrade=selectedGrade,
            stNumber=stNumber,
            profileImageUrl =profileImageUrl,
            userPhoneNumber = phoneNumber
        )
        val userId = database.child("users").push().key
        if (userId != null) {
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
    }

    private fun launchCameraForAuthentication() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        launcher.launch(cameraIntent)
    }

    @SuppressLint("SimpleDateFormat")
    private fun generateImageFileName(schoolName: String, stNumber: String): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val currentDateAndTime: String = sdf.format(Date())
        return "image_${schoolName}_${stNumber}_$currentDateAndTime.jpg"
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun uploadImageToFirebaseStorage(imageBitmap: Bitmap,schoolName: String,stNumber: String) {
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        // 이미지 파일 이름을 생성하기 전에 입력값을 살균합니다.
        val sanitizedSchoolName = schoolName.replace(Regex("[.#$\\[\\]/]"), "")
        val sanitizedStNumber = stNumber.replace(Regex("[.#$\\[\\]/]"), "")
        val fileName=generateImageFileName(sanitizedSchoolName,sanitizedStNumber)
        val imageRef = storageRef.child("images/$fileName")
        val uploadTask = imageRef.putBytes(data)

        uploadTask.addOnFailureListener {
            showMessage("이미지 업로드에 실패하였습니다: ${it.message}")
        }.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                imageUri = uri
                showMessage("이미지 업로드에 성공하였습니다.")
            } ?: showMessage("이미지 URL을 가져오는데 실패하였습니다.")
        }
    }

    private fun checkInputs() {
        val department = et_department.text.toString().trim()
        val stNumber = et_stNumber.text.toString().trim()
        val schoolName = et_school.text.toString().trim()

        btn_signup.isEnabled = department.isNotEmpty() && stNumber.isNotEmpty() && schoolName.isNotEmpty() && imageUri != null

        // 버튼의 배경색을 변경합니다.
        if (btn_signup.isEnabled) {
            btn_signup.setBackgroundResource(R.drawable.skyblue_button_background) // 활성화된 버튼의 색
        } else {
            btn_signup.setBackgroundResource( R.drawable.gray_button_background) // 비활성화된 버튼의 색
        }
    }
}



