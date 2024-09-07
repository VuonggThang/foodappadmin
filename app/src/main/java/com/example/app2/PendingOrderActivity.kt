package com.example.app2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app2.adapter.DeliveryAdapter
import com.example.app2.adapter.PendingOrderAdapter
import com.example.app2.databinding.ActivityPendingOrderBinding
import com.example.app2.databinding.PendingOrderItemBinding

class PendingOrderActivity : AppCompatActivity() {
    private lateinit var binding:ActivityPendingOrderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backimageButton.setOnClickListener {
            finish()
        }
        val orderedCustomerName = arrayListOf(
            "Thang",
            "Mai",
            "Anh",
        )
        val orderedQuantity = arrayListOf(
            "9",
            "10",
            "11",
        )
        val orderFoodImage = arrayListOf(R.drawable.menu1,R.drawable.menu2,R.drawable.menu3)
        val adapter = PendingOrderAdapter(orderedCustomerName,orderedQuantity,orderFoodImage, this  )
        binding.pendingOrderRecyclerView.adapter = adapter
        binding.pendingOrderRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}