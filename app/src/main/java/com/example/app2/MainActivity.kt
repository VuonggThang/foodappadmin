package com.example.app2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.app2.databinding.ActivityMainBinding
import com.example.app2.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var database:FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var completedOrderReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        binding.addMenu.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }
        binding.allItemMenu.setOnClickListener {
            val intent = Intent(this, AllItemActivity::class.java)
            startActivity(intent)
        }
        binding.outForDeliveryButton.setOnClickListener {
            val intent = Intent(this, OutForDeliveryActivity::class.java)
            startActivity(intent)
        }
        binding.profile.setOnClickListener {
            val intent = Intent(this, AdminFrofileActivity::class.java)
            startActivity(intent)
        }
        binding.createUser.setOnClickListener {
            val intent = Intent(this, CreateUserActivity::class.java)
            startActivity(intent)
        }
        binding.pendingOrderTextView.setOnClickListener{
            val intent = Intent(this, PendingOrderActivity::class.java)
            startActivity(intent)
        }
        binding.btnThongke.setOnClickListener{
            val intent = Intent(this,StatisticsActivity::class.java)
            startActivity(intent)
        }
        binding.logoutButton.setOnClickListener{
            auth.signOut()
            Log.d("Logout","onData:Logout")
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
        binding.cancelDetails.setOnClickListener {
            val intent = Intent(this,CancelActivity::class.java)
            startActivity(intent)
        }

        pendingOrders()

        competedOrders()

        wholeTimeEarning()

        cancelOrders()
    }

    private fun cancelOrders() {
        val completeOrderReference = database.reference.child("CancelOrders")
        var cancelOrders = 0
        completeOrderReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                cancelOrders = snapshot.childrenCount.toInt()
                binding.cancelOders.text = cancelOrders.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun wholeTimeEarning() {
        val listOfTotalPay = mutableListOf<Int>()
        completedOrderReference = FirebaseDatabase.getInstance().reference.child("CompletedOrder")
        completedOrderReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children){
                    var completeOrder = orderSnapshot.getValue(OrderDetails::class.java)
                    completeOrder?.totalPrice?.replace("$","")?.toIntOrNull()
                        ?.let {i ->
                            listOfTotalPay.add(i)
                        }
                }
                binding.wholeTimeEarning.text = listOfTotalPay.sum().toString() + "$"
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun competedOrders() {
        val completeOrderReference = database.reference.child("CompletedOrder")
        var completeOrderItemCount = 0

        // Lọc các đơn hàng có paymentReceived = true
        completeOrderReference.orderByChild("paymentReceived").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    completeOrderItemCount = snapshot.childrenCount.toInt() // Đếm số đơn hàng
                    binding.completeOrders.text = completeOrderItemCount.toString() // Hiển thị số đơn hàng
                }

                override fun onCancelled(error: DatabaseError) {
                    //Toast.makeText(this@PendingOrderActivity, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun pendingOrders() {
        database = FirebaseDatabase.getInstance()
        var pendingOrderReference = database.reference.child("OrderDetails")
        var pendingOrderItemCount = 0
        pendingOrderReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                pendingOrderItemCount = snapshot.childrenCount.toInt()
                runOnUiThread {
                    binding.pendingOrders.text = pendingOrderItemCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}