package com.example.uni_cob
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.firebase.auth.FacebookAuthProvider


class LoginActivity : AppCompatActivity() {
    private var callbackManager: CallbackManager? = null
    private var auth : FirebaseAuth? = null
    private var googleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 9001
    private val TAG = "FacebookLogin"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth=FirebaseAuth.getInstance()

        // 구글 로그인 설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_login_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        // Facebook 로그인 설정
        callbackManager = CallbackManager.Factory.create()
        val facebookLoginButton = findViewById<Button>(R.id.facebookLoginButton)
        facebookLoginButton.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
            // 이후 로그인 프로세스를 처리할 signInWithFacebook() 함수 호출
            signInWithFacebook()
        }


        // 구글 로그인 버튼 클릭 이벤트
        val googleLoginButton = findViewById<Button>(R.id.login_to_google)
        googleLoginButton.setOnClickListener {
            signInWithGoogle()
        }

        val btn_signup=findViewById<Button>(R.id.signupButton)
        val emailLoginButton = findViewById<Button>(R.id.emailLoginButton)
        val phoneLoginButton=findViewById<Button>(R.id.login_to_phone)
        // 휴대폰 번호로 로그인 버튼 클릭 이벤트
        phoneLoginButton.setOnClickListener {
            showLogin_phone_Dialog()
        }

        // 이메일로 로그인 버튼 클릭 이벤트
        emailLoginButton.setOnClickListener {
            showLogin_email_Dialog()
        }
        // 페이스북 로그인 버튼 클릭 이벤트
        facebookLoginButton.setOnClickListener {
            signInWithFacebook()
        }
        //페이지 이동(로그인->회원가입)
        btn_signup.setOnClickListener {moveToLogin01()}



    }
    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val resultCode = result.resultCode
        val data = result.data
        if (resultCode == Activity.RESULT_OK) {
            // Google 로그인 성공 시에 실행할 코드를 여기에 작성하세요.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                val account = task.result
                firebaseAuthWithGoogle(account)
            } else {
                Log.d(TAG, "Google 로그인 실패")
            }
        } else {
            Log.d(TAG, "Google 로그인 취소 또는 실패")
        }
    }
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient?.signInIntent
        if (signInIntent != null) {
            googleSignInLauncher.launch(signInIntent)
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Google 로그인 성공", Toast.LENGTH_SHORT).show()
                    moveMainPage(auth?.currentUser)
                } else {
                    Toast.makeText(this, "Google 로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun moveToLogin01(){
        val intent=Intent(this,SignUpActivity1::class.java)
        startActivity(intent)
    }




    // 로그인 함수
    private fun signIn(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext, "로그인 성공",
                            Toast.LENGTH_SHORT
                        ).show()
                        // 로그인 성공 시 MainActivity로 회원 정보를 전달
                        val currentUser = auth?.currentUser
                        if (currentUser != null) {
                            val intent = Intent(this, MainActivity::class.java)
                            // 여기에서 currentUser를 Intent에 추가하여 전달
                            intent.putExtra("User", currentUser)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            baseContext, "로그인 실패",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(
                baseContext, "이메일과 비밀번호를 입력하세요.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    //유저 정보 넘겨주고 메인 액티비티 호출
    private fun moveMainPage(user: FirebaseUser?) {
        if(user!=null){
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showLogin_phone_Dialog() {
        val dialog = Dialog(this,R.style.CustomDialogTheme)
        dialog.setContentView(R.layout.login_dialog_phone)

        // 배경을 반투명하게 설정
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // 다이얼로그 크기 설정
        val windowLayoutParams = dialog.window?.attributes
        windowLayoutParams?.width = (resources.displayMetrics.widthPixels * 0.8).toInt()
        dialog.window?.attributes = windowLayoutParams
        windowLayoutParams?.height = (resources.displayMetrics.heightPixels * 0.8).toInt()
        dialog.window?.attributes = windowLayoutParams

        // 중앙에 위치하도록 설정
        windowLayoutParams?.gravity = Gravity.CENTER
        dialog.window?.attributes = windowLayoutParams

        // 포커스 설정
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        dialog.show()

        dialog.getWindow()?.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
            WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        // 로그인 버튼 클릭 시
        val loginButton = dialog.findViewById<Button>(R.id.btn_login_to_phone)
        val phoneEditText = dialog.findViewById<EditText>(R.id.login_to_phone)
        val phonepasswordEditText = dialog.findViewById<EditText>(R.id.login_to_phone_password)


        loginButton.setOnClickListener {
            val phone =phoneEditText .text.toString().trim()
            val password = phonepasswordEditText.text.toString().trim()

            // 이메일 및 비밀번호로 로그인 시도
            signIn(phone, password)
        }

    }
    private fun showLogin_email_Dialog() {
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        dialog.setContentView(R.layout.login_dialog_email)

        // 배경을 반투명하게 설정
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // 다이얼로그 크기 설정
        val windowLayoutParams = dialog.window?.attributes
        windowLayoutParams?.width = (resources.displayMetrics.widthPixels * 0.8).toInt()
        dialog.window?.attributes = windowLayoutParams
        windowLayoutParams?.height = (resources.displayMetrics.heightPixels * 0.8).toInt()


        // 포커스 설정
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        dialog.show()

        dialog.getWindow()?.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
            WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        // 로그인 버튼 클릭 시
        val loginButton = dialog.findViewById<Button>(R.id.btn_login_to_email)
        val emailEditText = dialog.findViewById<EditText>(R.id.login_to_email)
        val passwordEditText = dialog.findViewById<EditText>(R.id.login_to_email_password)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // 이메일 및 비밀번호로 로그인 시도
            signIn(email, password)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Facebook 로그인 결과를 CallbackManager로 전달
        callbackManager?.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                val account = task.result
                firebaseAuthWithGoogle(account)
            } else {
                Log.d(TAG, "Google 로그인 실패")
            }
        }
    }
    private fun signInWithFacebook() {
        // 페이스북 로그인 관련 설정
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "Facebook 로그인 성공")

                // Firebase에 Facebook으로부터 얻은 AccessToken을 사용하여 로그인
                val credential = FacebookAuthProvider.getCredential(loginResult.accessToken.token)
                auth?.signInWithCredential(credential)
                    ?.addOnCompleteListener(this@LoginActivity) { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Firebase 로그인 성공")
                            moveMainPage(auth?.currentUser)
                        } else {
                            Log.w(TAG, "Firebase 로그인 실패", task.exception)
                        }
                    }
            }

            override fun onCancel() {
                Log.d(TAG, "Facebook 로그인 취소")
            }

            override fun onError(error: FacebookException) {
                Log.e(TAG, "Facebook 로그인 에러", error)
            }
        })
    }


}