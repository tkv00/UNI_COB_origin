package com.example.uni_cob

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private var auth : FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn_chat=findViewById<Button>(R.id.btn_chat)
        val btn_logout = findViewById<Button>(R.id.logout)
        val currentUser = auth?.currentUser
        //구글 로그아웃  (나중에 추가로 이 버튼 하나에 구글,페북,휴대폰,이메일 로그아웃 만들어야함)
            btn_logout.setOnClickListener {
                // Firebase에서 로그아웃
                FirebaseAuth.getInstance().signOut()

                // Google 계정에서 로그아웃
                val googleSignInClient =
                    GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
                googleSignInClient.signOut().addOnCompleteListener(this) {
                    // 로그아웃이 완료되면 여기에 코드를 추가할 수 있습니다.
                    val intent=Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this,"로그아웃 되었습니다.",Toast.LENGTH_LONG).show()

                }
            }
        btn_chat.setOnClickListener{
            val intent = Intent(this, chatActivity::class.java)
            // 여기에서 currentUser를 Intent에 추가하여 전달
            intent.putExtra("User", currentUser)
            startActivity(intent)
            finish()
        }
    }
}
