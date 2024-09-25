package com.example.app2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.app2.databinding.ActivityOrderDetailsBinding

class OrderDetailsActivity : AppCompatActivity() {
    private val binding: ActivityOrderDetailsBinding by lazy {
        ActivityOrderDetailsBinding.inflate(layoutInflater)
    }
    private var userName :String? = null
    private var address :String? = null
    private var phoneNumber :String? = null
    private var totalPrice :String? = null

    private  var foodNames :MutableList<String> = mutableListOf()
    private  var foodImages :MutableList<String> = mutableListOf()
    private  var foodQuantity :MutableList<String> = mutableListOf()
    private  var foodPrices :MutableList<String> = mutableListOf()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            finish()
        }
        getDataFromIntent()
    }

    private fun getDataFromIntent() {

    }
}