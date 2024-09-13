package com.example.app2
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.app2.databinding.ActivityLoginBinding
import com.example.app2.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class LoginActivity : AppCompatActivity() {
    private var userName: String? = null
    private var nameOfRestaurant: String? = null
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // initialize Firebase Auth
        auth = Firebase.auth
        // initialize Firebase database
        database = Firebase.database.reference
        // initialize google sign in
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.loginbutton.setOnClickListener {
            // get text form edit text
            email = binding.email.text.toString().trim()
            password = binding.password.text.toString().trim()
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "hay dien day du thong tin", Toast.LENGTH_SHORT).show()
            } else {
                createUserAccount(email, password)
            }
        }
        binding.googleButton.setOnClickListener{
            val signIntent = googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }
        binding.donthaveaccount.setOnClickListener {
            val intent = Intent(this, SignActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createUserAccount(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                updateUi(user)
            } else {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        Toast.makeText(
                            this,
                            "Tạo người dùng và đăng nhập thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                        saveUserData()
                        updateUi(user)
                    } else {
                        Toast.makeText(this, "Xac thuc khong thanh cong", Toast.LENGTH_SHORT).show()
                        Log.d("Account", "Tao tai khoan nguoi dung: that bai", task.exception)
                    }
                }
            }
        }
    }

    private fun saveUserData() {
        email = binding.email.text.toString().trim()
        password = binding.password.text.toString().trim()
        val user = UserModel(userName, nameOfRestaurant, email, password)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            database.child("user").child(it).setValue(user)
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                val account :GoogleSignInAccount= task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken,null)
                auth.signInWithCredential(credential).addOnCompleteListener{
                    authTask ->
                    if (authTask.isSuccessful){
                        //successfully sign in with google
                        Toast.makeText(this,"Đăng nhập thành công với google",Toast.LENGTH_SHORT).show()
                        updateUi(authTask.result?.user)
                        finish()
//                        startActivity(Intent(this,MainActivity::class.java))
                    }else{
                        Toast.makeText(this,"Đăng nhập không thành công với google",Toast.LENGTH_SHORT).show()
                        Log.e("GoogleSignIn", "signInResult:failed code=" + authTask.exception)
                    }
                }
            }
            else{
                Toast.makeText(this,"Đăng nhập không thành công với google",Toast.LENGTH_SHORT).show()
            }
        }
    }
    //Check if user is already logged in
    override fun onStart(){
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser!=null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
        }
    }
    private fun updateUi(user: FirebaseUser?) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}