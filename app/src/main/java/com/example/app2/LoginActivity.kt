package com.example.app2
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.app2.databinding.ActivityLoginBinding
import com.example.app2.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class LoginActivity : AppCompatActivity() {
    private lateinit var email:String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
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
        binding.loginbutton.setOnClickListener {
            // get text form edit text
            email = binding.email.text.toString().trim()
            password = binding.password.text.toString().trim()
            if (email.isBlank()||password.isBlank()) {
                Toast.makeText( this,  "hay dien day du thong tin", Toast.LENGTH_SHORT).show()
            }else{
                createUserAccount(email, password)
            }

        }
        binding.donthaveaccount.setOnClickListener {
            val intent = Intent(this, SignActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createUserAccount(email: String, password: String) {
        auth.signInWithEmailAndPassword (email, password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                val user: FirebaseUser? = auth.currentUser
                updateUi(user)
            }else{
                auth.createUserWithEmailAndPassword (email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user: FirebaseUser? = auth.currentUser
                        saveUserData()
                        updateUi(user)
                    }else{
                        Toast.makeText(this,"Xac thuc khong thanh cong",Toast.LENGTH_SHORT).show()
                        Log.d("Account", "Tao tai khoan nguoi dung: that bai", task.exception)
                    }
                }
            }
        }
    }

    private fun saveUserData() {
        email = binding.email.text.toString().trim()
        password = binding.password.text.toString().trim()
        val user = UserModel(email,password)
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            database.child("user").child(it).setValue(user)
        }
    }

    private fun updateUi(user: FirebaseUser?) {
        startActivity(Intent ( this, MainActivity::class.java))
        finish()
    }
}