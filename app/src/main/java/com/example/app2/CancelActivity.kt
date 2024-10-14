package com.example.app2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.example.app2.adapter.CancelOrderAdapter
import com.example.app2.model.Cancel
import com.example.app2.databinding.ActivityCancelBinding // Import View Binding

class CancelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCancelBinding // Khai báo binding
    private lateinit var cancelOrderList: MutableList<Cancel>
    private lateinit var adapter: CancelOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCancelBinding.inflate(layoutInflater) // Khởi tạo binding
        setContentView(binding.root) // Sử dụng root view từ binding

        // Khởi tạo RecyclerView
        binding.cancelRecyclerView.layoutManager = LinearLayoutManager(this) // Sử dụng binding
        cancelOrderList = mutableListOf()
        adapter = CancelOrderAdapter(cancelOrderList)
        binding.cancelRecyclerView.adapter = adapter // Sử dụng binding
        binding.backimageButton.setOnClickListener {
            finish()
        }

        // Lấy dữ liệu từ Firebase
        val databaseReference = FirebaseDatabase.getInstance().getReference("CancelOrders")
        databaseReference.get().addOnSuccessListener { dataSnapshot ->
            for (snapshot in dataSnapshot.children) {
                val customerName = snapshot.child("userName").getValue(String::class.java)
                val reason = snapshot.child("reason").getValue(String::class.java)
                val cancelOrder = Cancel(customerName, reason)
                cancelOrderList.add(cancelOrder)
            }
            // Cập nhật adapter sau khi lấy dữ liệu
            adapter.notifyDataSetChanged()
        }
    }
}
