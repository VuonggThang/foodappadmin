
package com.example.app2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app2.adapter.PendingOrderAdapter
import com.example.app2.databinding.ActivityPendingOrderBinding
import com.example.app2.model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PendingOrderActivity : AppCompatActivity() , PendingOrderAdapter.OnItemClicked {
    private lateinit var binding:ActivityPendingOrderBinding
    private var listOfName:MutableList<String> = mutableListOf()
    private var listOfTotalPrice:MutableList<String> = mutableListOf()
    private var listOfImageFirstFoodOrder:MutableList<String> = mutableListOf()
    private var listOfOrderItem:ArrayList<OrderDetails> = arrayListOf()
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseOrderDetails: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialization of database
        database = FirebaseDatabase.getInstance()
        //Initialization of databaseReference
        databaseOrderDetails = database.reference.child("OrderDetails")
        getOrdersDetails()
        binding.backimageButton.setOnClickListener {
            finish()
        }


    }

    private fun getOrdersDetails() {
        //retrieve order details from Firebase database
        databaseOrderDetails.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(orderSnapshot in snapshot.children){
                    val orderDetails = orderSnapshot.getValue(OrderDetails::class.java)
                    orderDetails?.let {
                        listOfOrderItem.add(it)
                    }
                }
                addDataToListForRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun addDataToListForRecyclerView() {
        for (orderItem in listOfOrderItem){
            //add data to respective list for populating the recyclerview
            orderItem.userName?.let { listOfName.add(it) }
            orderItem.totalPrice?.let { listOfTotalPrice.add(it) }
            orderItem.foodImages?.filterNot { it.isEmpty() }?.forEach{
                listOfImageFirstFoodOrder.add(it)
            }
        }
        setAdapter()
    }

    private fun setAdapter() {
        binding.pendingOrderRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = PendingOrderAdapter(this,listOfName,listOfTotalPrice,listOfImageFirstFoodOrder,this)
        binding.pendingOrderRecyclerView.adapter = adapter
    }
    override fun onItemClickListener(position: Int){
        val intent = Intent(this,OrderDetailsActivity::class.java)
        val userOrderDetails = listOfOrderItem[position]
        intent.putExtra("UserOrderDetails",userOrderDetails)
        startActivity(intent)
    }

    override fun onItemAcceptClickListener(position: Int) {
        //handle item acceptance and update database
        val childItemPushKey = listOfOrderItem[position].itemPushKey
        val clickItemOrderReference = childItemPushKey?.let {
            database.reference.child("OrderDetails").child(it)
        }
        clickItemOrderReference?.child("orderAccepted")?.setValue(true)
        updateOrderAcceptStatus(position)
    }
//    override fun onItemDispatchClickListener(position: Int) {
//        //handle item dispatch and update database
//        val dispatchItemPushKey = listOfOrderItem[position].itemPushKey
//        val dispatchItemOrderReference = database.reference.child("CompletedOrder").child(dispatchItemPushKey!!)
//        dispatchItemOrderReference.setValue(listOfOrderItem[position])
//            .addOnSuccessListener {
//                deleteThisItemFromOrderDetails(dispatchItemPushKey)
//            }
//    }
    override fun onItemDispatchClickListener(position: Int) {
        // handle item dispatch and update database
        val dispatchItemPushKey = listOfOrderItem[position].itemPushKey
        val dispatchItemOrderReference = database.reference.child("CompletedOrder").child(dispatchItemPushKey!!)

        // Cập nhật đơn hàng vào nhánh "CompletedOrder"
        dispatchItemOrderReference.setValue(listOfOrderItem[position])
            .addOnSuccessListener {
                // Cập nhật orderAccepted = true sau khi chuyển đơn hàng thành công
                dispatchItemOrderReference.child("orderAccepted").setValue(true)
                    .addOnSuccessListener {
                        // Sau khi cập nhật thành công trạng thái orderAccepted, xóa đơn hàng khỏi OrderDetails
                        deleteThisItemFromOrderDetails(dispatchItemPushKey)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Lỗi khi cập nhật trạng thái orderAccepted trong CompletedOrder", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi khi chuyển đơn hàng vào CompletedOrder", Toast.LENGTH_SHORT).show()
            }
    }


    private fun deleteThisItemFromOrderDetails(dispatchItemPushKey: String) {
        val orderDetailsItemsReference = database.reference.child("OrderDetails").child(dispatchItemPushKey)
        orderDetailsItemsReference.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this," Don hang da gui di",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this," Don hang khong duoc gui di",Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateOrderAcceptStatus(position: Int) {
        //update order acceptance in user's BuyHistory and OrderDetails
        val userIdOfClickedItem = listOfOrderItem[position].userUid
        val pushKeyOfClickedItem = listOfOrderItem[position].itemPushKey
        val buyHistoryReference = database.reference.child("user").child(userIdOfClickedItem!!).child("BuyHistory").child(pushKeyOfClickedItem!!)
        buyHistoryReference.child("orderAccepted").setValue(true)
        databaseOrderDetails.child(pushKeyOfClickedItem).child("orderAccepted").setValue(true)
    }
    override fun onItemRejectClickListener(position: Int) {
        val dispatchItemPushKey = listOfOrderItem[position].itemPushKey
        val orderDetailsItem = listOfOrderItem[position]
        val databaseRef = FirebaseDatabase.getInstance().getReference("OrderDetails").child(dispatchItemPushKey!!)
        val cancelOrderRef = FirebaseDatabase.getInstance().getReference("CancelOrders").child(dispatchItemPushKey)

        // Cập nhật giá trị cancelled thành true trước trong OrderDetails
        databaseRef.child("cancelled").setValue(true)
            .addOnSuccessListener {
                // Cập nhật lại giá trị cancelled cho orderDetailsItem
                orderDetailsItem.isCancelled = true

                // Chuyển đơn hàng sang CancelOrders, với cancelled = true
                cancelOrderRef.setValue(orderDetailsItem)
                    .addOnSuccessListener {
                        // Chỉ xoá đơn hàng trong OrderDetails sau khi đã di chuyển thành công vào CancelOrders
                        databaseRef.removeValue()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Đơn hàng đã bị huỷ và chuyển sang CancelOrders", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Lỗi khi huỷ đơn hàng", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Lỗi khi chuyển đơn hàng vào CancelOrders", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi khi cập nhật trạng thái đơn hàng", Toast.LENGTH_SHORT).show()
            }
    }


}