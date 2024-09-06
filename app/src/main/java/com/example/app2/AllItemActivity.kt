package com.example.app2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app2.adapter.AllItemAdapter
import com.example.app2.databinding.ActivityAllItemBinding

class AllItemActivity : AppCompatActivity() {
    private val binding:ActivityAllItemBinding by lazy {
        ActivityAllItemBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val menuFoodName = listOf("Bun","Chao","My","Com","Bun cha")
        val menuItemPrice = listOf("$4","$5","$7","$9","$10")
        val menuImage = listOf(
            R.drawable.menu1,
            R.drawable.menu2,
            R.drawable.menu3,
            R.drawable.menu4,
            R.drawable.menu5,
        )
        binding.backimageButton.setOnClickListener {
            finish()
        }
        val adapter = AllItemAdapter(ArrayList(menuFoodName), ArrayList(menuItemPrice),
            ArrayList(menuImage)
        )
        binding.MenuRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.MenuRecyclerView.adapter = adapter
    }
}