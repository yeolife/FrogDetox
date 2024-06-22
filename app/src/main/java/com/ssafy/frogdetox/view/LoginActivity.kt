package com.ssafy.frogdetox.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ssafy.frogdetox.R
import com.ssafy.frogdetox.databinding.ActivityLoginBinding
import com.ssafy.frogdetox.common.SharedPreferencesManager
import com.ssafy.frogdetox.common.SharedPreferencesManager.getUId
import com.ssafy.frogdetox.common.SharedPreferencesManager.putUId

private const val TAG = "LoginActivity_싸피"
class LoginActivity : AppCompatActivity() {
    lateinit var binding : ActivityLoginBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val activityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            task.runCatching {
                getResult(ApiException::class.java).idToken
            }.onSuccess { token ->
                token?.let { firebaseAuthWithGoogle(token) }
            }.onFailure { error ->
                Log.w(TAG, "Google sign in failed ${error.message}")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        SharedPreferencesManager.init(this)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSignIn.setOnClickListener { signIn() }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth

        // Intent에서 "state" 값 검사
        val state = intent.getIntExtra("state", 0) // 기본값은 0
        if (state == 1) {
            clearAuthentication()
        }
    }
    private fun clearAuthentication() {
        // Firebase 로그아웃
        auth.signOut()

        // Google 로그아웃
        googleSignInClient.signOut().addOnCompleteListener(this) {
            // 로그아웃 후 UI 업데이트
            updateUI(null)
        }

        // SharedPreferences 내용 삭제
        SharedPreferencesManager.clearPreferences()

        // 필요한 경우 사용자에게 로그아웃 되었음을 알리기
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
    }
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        Log.d(TAG, "firebaseAuthWithGoogle: ")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        Log.d(TAG, "updateUI: ")
        if (user != null) {
            val intent2 = Intent(this, MainActivity::class.java).apply {
                putExtra("url", user.photoUrl.toString())
                putExtra("name", user.displayName)
            }
            Log.d(TAG, "updateUI: ${user.uid}")
            putUId(user.uid)
            Log.d(TAG, "updateUI: !!! ${getUId()}")
            Toast.makeText(this, "환영합니다, ${user.displayName}님", Toast.LENGTH_SHORT).show()
            startActivity(intent2)
            finish()
        }
    }

    private fun signIn() {
        Log.d(TAG, "signIn: ")
        val signInIntent = googleSignInClient.signInIntent
        activityLauncher.launch(signInIntent)
    }

}