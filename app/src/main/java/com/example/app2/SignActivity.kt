package com.example.app2

import android.R.layout.simple_list_item_1
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.app2.databinding.ActivitySignBinding
import com.example.app2.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database


class SignActivity : AppCompatActivity() {

    private lateinit var userName: String
    private lateinit var nameOfRestaurant: String
    private lateinit var email : String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private val binding: ActivitySignBinding by lazy {
        ActivitySignBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //// initialize Firebase Auth
        auth = Firebase.auth
        // initialize Firebase database
        database = Firebase.database.reference

        binding.createUserButton.setOnClickListener {
            //get text from edittext
            userName = binding.name.text.toString().trim()
            nameOfRestaurant = binding.restaurentName.text.toString().trim()
            email = binding.emailOrPhone.text.toString().trim()
            password = binding.password.text.toString().trim()

            if (userName.isBlank() || nameOfRestaurant.isBlank() || email.isBlank() || password.isBlank()){
                Toast.makeText(this,"hay dien day du thong tin",Toast.LENGTH_SHORT).show()
            }else{
                createAccount(email, password)
            }
        }
        binding.dontalreadyhavebutton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        val locationList = arrayOf("ha noi", "ha noi1", "ha noi2", "ha noi3")
        val adapter = ArrayAdapter(this, simple_list_item_1, locationList)
        val autoCompleteTextView = binding.listOfLocation
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun createAccount(email: String, password: String) {
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{task ->
                if(task.isSuccessful){
                    Toast.makeText(this,"Tai khoan tao thanh cong",Toast.LENGTH_SHORT).show()
                    saveUserData()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else{
                    Toast.makeText(this,"Tai khoan tao thanh cong",Toast.LENGTH_SHORT).show()
                    Log.d("Account", "Tao tai khoan: that bai", task.exception)
                }
            }
    }
    //save data in to database
    private fun saveUserData() {
        //get text from edittext
        userName = binding.name.text.toString().trim()
        nameOfRestaurant = binding.restaurentName.text.toString().trim()
        email = binding.emailOrPhone.text.toString().trim()
        password = binding.password.text.toString().trim()
        val user= UserModel(userName, nameOfRestaurant, email, password)
        val userId:String = FirebaseAuth.getInstance().currentUser!!.uid
        //save user data Firebase database
        database.child("user").child(userId).setValue(user)
    }
}