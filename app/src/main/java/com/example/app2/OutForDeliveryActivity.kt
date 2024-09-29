package com.example.app2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app2.adapter.DeliveryAdapter
import com.example.app2.databinding.ActivityOutForDeliveryBinding
import com.example.app2.model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

class OutForDeliveryActivity : AppCompatActivity() {
    private val binding : ActivityOutForDeliveryBinding by lazy{
        ActivityOutForDeliveryBinding.inflate(layoutInflater)
    }
    private lateinit var database: FirebaseDatabase
    private  var listOfCompleteOrderList:ArrayList<OrderDetails> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // Khởi tạo Firebase database
        database = FirebaseDatabase.getInstance()
        binding.backimageButton.setOnClickListener {
            finish()
        }

        // retrieve and display completed order
        retrieveCompleteOrderDetail()

    }

    private fun retrieveCompleteOrderDetail() {
        // Initialize Firebase database
        val completeOrderReference = database.reference.child("CompletedOrder")
            .orderByChild("currentTime")
        completeOrderReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear the list before populating it with new data
                listOfCompleteOrderList.clear()
                for (orderSnapshot in snapshot.children){
                  val completeOrder = orderSnapshot.getValue(OrderDetails::class.java)
                    completeOrder?.let {
                        listOfCompleteOrderList.add(it)
                    }
                }
                //reverse the list to display latest order first
                listOfCompleteOrderList.reverse()
                setDataIntoRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setDataIntoRecyclerView() {
        // Initialization list to hold customers name and payment status
        val customerName = mutableListOf<String>()
        val moneyStatus = mutableListOf<Boolean>()
        for (order in listOfCompleteOrderList){
            order.userName?.let {
                customerName.add(it)
            }
            moneyStatus.add(order.paymentReceived)
        }
        val adapter = DeliveryAdapter(customerName, moneyStatus)
        binding.deliveryRecyclerView.adapter = adapter
        binding.deliveryRecyclerView.layoutManager = LinearLayoutManager(this,)
    }
}