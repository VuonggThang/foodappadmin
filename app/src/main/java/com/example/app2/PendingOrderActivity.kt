package com.example.app2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.DeadObjectException
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
//
//class PendingOrderActivity : AppCompatActivity() , PendingOrderAdapter.OnItemClicked {
//    private lateinit var binding:ActivityPendingOrderBinding
//    private var listOfName:MutableList<String> = mutableListOf()
//    private var listOfTotalPrice:MutableList<String> = mutableListOf()
//    private var listOfImageFirstFoodOrder:MutableList<String> = mutableListOf()
//    private var listOfOrderItem:ArrayList<OrderDetails> = arrayListOf()
//    private lateinit var database: FirebaseDatabase
//    private lateinit var databaseOrderDetails: DatabaseReference
//    private lateinit var adapter: PendingOrderAdapter
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        //Initialization of database
//        database = FirebaseDatabase.getInstance()
//        //Initialization of databaseReference
//        databaseOrderDetails = database.reference.child("OrderDetails")
//        getOrdersDetails()
//        binding.backimageButton.setOnClickListener {
//            finish()
//        }
//
//
//    }
//
//    private fun getOrdersDetails() {
//        //retrieve order details from Firebase database
//        databaseOrderDetails.addListenerForSingleValueEvent(object :ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for(orderSnapshot in snapshot.children){
//                    val orderDetails = orderSnapshot.getValue(OrderDetails::class.java)
//                    orderDetails?.let {
//                        listOfOrderItem.add(it)
//                    }
//                }
//                addDataToListForRecyclerView()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
//    }
//
//    private fun addDataToListForRecyclerView() {
//        for (orderItem in listOfOrderItem){
//            //add data to respective list for populating the recyclerview
//            orderItem.userName?.let { listOfName.add(it) }
//            orderItem.totalPrice?.let { listOfTotalPrice.add(it) }
//            orderItem.foodImages?.filterNot { it.isEmpty() }?.forEach{
//                listOfImageFirstFoodOrder.add(it)
//            }
//        }
//        setAdapter()
//    }
//
//    private fun setAdapter() {
//        binding.pendingOrderRecyclerView.layoutManager = LinearLayoutManager(this)
//        val adapter = PendingOrderAdapter(this,listOfName,listOfTotalPrice,listOfImageFirstFoodOrder,this)
//        binding.pendingOrderRecyclerView.adapter = adapter
//    }
//    override fun onItemClickListener(position: Int){
//        val intent = Intent(this,OrderDetailsActivity::class.java)
//        val userOrderDetails = listOfOrderItem[position]
//        intent.putExtra("UserOrderDetails",userOrderDetails)
//        startActivity(intent)
//    }
//
//    override fun onItemAcceptClickListener(position: Int) {
//        //handle item acceptance and update database
//        val childItemPushKey = listOfOrderItem[position].itemPushKey
//        val clickItemOrderReference = childItemPushKey?.let {
//            database.reference.child("OrderDetails").child(it)
//        }
//        clickItemOrderReference?.child("orderAccepted")?.setValue(true)
//        updateOrderAcceptStatus(position)
//    }
//    override fun onItemDispatchClickListener(position: Int) {
//        //handle item dispatch and update database
//        val dispatchItemPushKey = listOfOrderItem[position].itemPushKey
//        val dispatchItemOrderReference = database.reference.child("CompletedOrder").child(dispatchItemPushKey!!)
//        dispatchItemOrderReference.setValue(listOfOrderItem[position])
//            .addOnSuccessListener {
//                deleteThisItemFromOrderDetails(dispatchItemPushKey)
//            }
//    }
//
//    private fun deleteThisItemFromOrderDetails(dispatchItemPushKey: String) {
//        val orderDetailsItemsReference = database.reference.child("OrderDetails").child(dispatchItemPushKey)
//        orderDetailsItemsReference.removeValue()
//            .addOnSuccessListener {
//                Toast.makeText(this," Đơn hàng đã được gửi đi",Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener {
//                Toast.makeText(this," Đơn hàng bị từ chối ",Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun updateOrderAcceptStatus(position: Int) {
//        //update order acceptance in user's BuyHistory and OrderDetails
//        val userIdOfClickedItem = listOfOrderItem[position].userUid
//        val pushKeyOfClickedItem = listOfOrderItem[position].itemPushKey
//        val buyHistoryReference = database.reference.child("user").child(userIdOfClickedItem!!).child("BuyHistory").child(pushKeyOfClickedItem!!)
//        buyHistoryReference.child("orderAccepted").setValue(true)
//        databaseOrderDetails.child(pushKeyOfClickedItem).child("orderAccepted").setValue(true)
//    }
////    override fun onItemRejectClickListener(position: Int) {
////        val rejectItemPushKey = listOfOrderItem[position].itemPushKey
////        try {
////            // Tham chiếu đến đơn hàng trong OrderDetails
////            val rejectItemOrderReference = database.reference.child("OrderDetails").child(rejectItemPushKey!!)
////
////            // Cập nhật trạng thái đơn hàng thành 'rejected'
////            rejectItemOrderReference.child("orderStatus").setValue("rejected")
////                .addOnSuccessListener {
////                    // Sau khi cập nhật thành công, xóa đơn hàng
////                    rejectItemOrderReference.removeValue()
////                        .addOnSuccessListener {
////                            // Nếu xóa thành công, thông báo cho người dùng
////                            Toast.makeText(this, "Đơn hàng đã bị từ chối và xóa khỏi danh sách", Toast.LENGTH_SHORT).show()
////                            // Cập nhật danh sách và thông báo cho adapter
////                            listOfOrderItem.removeAt(position)
////                            adapter.notifyItemRemoved(position) // Cập nhật adapter
////                        }
////                        .addOnFailureListener {
////                            // Thông báo lỗi nếu xóa không thành công
////                            Toast.makeText(this, "Lỗi khi xóa đơn hàng", Toast.LENGTH_SHORT).show()
////                        }
////                }
////                .addOnFailureListener {
////                    // Thông báo lỗi nếu cập nhật không thành công
////                    Toast.makeText(this, "Lỗi khi từ chối đơn hàng", Toast.LENGTH_SHORT).show()
////                }
////        } catch (e: Exception) {
////            // Bắt lỗi DeadObjectException và thông báo cho người dùng
////            if (e is DeadObjectException) {
////                Toast.makeText(this, "Lỗi kết nối với dịch vụ", Toast.LENGTH_SHORT).show()
////            } else {
////                Toast.makeText(this, "Đã xảy ra lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
////            }
////        }
////    }
//override fun onItemRejectClickListener(position: Int) {
//    val rejectItemPushKey = listOfOrderItem[position].itemPushKey
//    try {
//        // Tham chiếu đến đơn hàng trong OrderDetails
//        val rejectItemOrderReference = database.reference.child("OrderDetails").child(rejectItemPushKey!!)
//
//        // Cập nhật trạng thái đơn hàng thành 'rejected'
//        rejectItemOrderReference.child("orderStatus").setValue("rejected")
//            .addOnSuccessListener {
//                // Sau khi cập nhật thành công, xóa đơn hàng
//                rejectItemOrderReference.removeValue()
//                    .addOnSuccessListener {
//                        // Nếu xóa thành công, thông báo cho người dùng
//                        Toast.makeText(this, "Đơn hàng đã bị từ chối và xóa khỏi danh sách", Toast.LENGTH_SHORT).show()
//                        // Cập nhật danh sách và thông báo cho adapter
//                        listOfOrderItem.removeAt(position)
//                        listOfName.removeAt(position) // Xóa tên
//                        listOfTotalPrice.removeAt(position) // Xóa giá
//                        listOfImageFirstFoodOrder.removeAt(position) // Xóa hình ảnh
//                        adapter.notifyItemRemoved(position) // Cập nhật adapter
//
//                    }
//                    .addOnFailureListener {
//                        // Thông báo lỗi nếu xóa không thành công
//                        Toast.makeText(this, "Lỗi khi xóa đơn hàng", Toast.LENGTH_SHORT).show()
//                    }
//            }
//            .addOnFailureListener {
//                // Thông báo lỗi nếu cập nhật không thành công
//                Toast.makeText(this, "Lỗi khi từ chối đơn hàng", Toast.LENGTH_SHORT).show()
//            }
//    } catch (e: Exception) {
//        // Bắt lỗi DeadObjectException và thông báo cho người dùng
//        if (e is DeadObjectException) {
//            Toast.makeText(this, "Lỗi kết nối với dịch vụ", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this, "Đã xảy ra lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
//        }
//    }
//}
//
class PendingOrderActivity : AppCompatActivity(), PendingOrderAdapter.OnItemClicked {
    private lateinit var binding: ActivityPendingOrderBinding
    private var listOfName: MutableList<String> = mutableListOf()
    private var listOfTotalPrice: MutableList<String> = mutableListOf()
    private var listOfImageFirstFoodOrder: MutableList<String> = mutableListOf()
    private var listOfOrderItem: ArrayList<OrderDetails> = arrayListOf()
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseOrderDetails: DatabaseReference
    private lateinit var adapter: PendingOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialization of database
        database = FirebaseDatabase.getInstance()
        // Initialization of databaseReference
        databaseOrderDetails = database.reference.child("OrderDetails")
        getOrdersDetails()
        binding.backimageButton.setOnClickListener {
            finish()
        }
    }

    private fun getOrdersDetails() {
        // Retrieve order details from Firebase database
        databaseOrderDetails.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    val orderDetails = orderSnapshot.getValue(OrderDetails::class.java)
                    orderDetails?.let {
                        listOfOrderItem.add(it)
                    }
                }
                addDataToListForRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PendingOrderActivity, "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addDataToListForRecyclerView() {
        for (orderItem in listOfOrderItem) {
            orderItem.userName?.let { listOfName.add(it) }
            orderItem.totalPrice?.let { listOfTotalPrice.add(it) }
            orderItem.foodImages?.filterNot { it.isEmpty() }?.forEach {
                listOfImageFirstFoodOrder.add(it)
            }
        }
        setAdapter()
    }

    private fun setAdapter() {
        binding.pendingOrderRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PendingOrderAdapter(this, listOfName, listOfTotalPrice, listOfImageFirstFoodOrder, this)
        binding.pendingOrderRecyclerView.adapter = adapter
    }

    override fun onItemClickListener(position: Int) {
        val intent = Intent(this, OrderDetailsActivity::class.java)
        val userOrderDetails = listOfOrderItem[position]
        intent.putExtra("UserOrderDetails", userOrderDetails)
        startActivity(intent)
    }

    override fun onItemAcceptClickListener(position: Int) {
        // Handle item acceptance and update database
        val childItemPushKey = listOfOrderItem[position].itemPushKey
        val clickItemOrderReference = childItemPushKey?.let {
            database.reference.child("OrderDetails").child(it)
        }
        clickItemOrderReference?.child("orderAccepted")?.setValue(true)
        updateOrderAcceptStatus(position)
    }

    override fun onItemDispatchClickListener(position: Int) {
        // Handle item dispatch and update database
        val dispatchItemPushKey = listOfOrderItem[position].itemPushKey
        dispatchItemPushKey?.let {
            val dispatchItemOrderReference = database.reference.child("CompletedOrder").child(it)
            dispatchItemOrderReference.setValue(listOfOrderItem[position])
                .addOnSuccessListener {
                    deleteThisItemFromOrderDetails(dispatchItemPushKey)
                }
        }
    }

    private fun deleteThisItemFromOrderDetails(dispatchItemPushKey: String) {
        val orderDetailsItemsReference = database.reference.child("OrderDetails").child(dispatchItemPushKey)
        orderDetailsItemsReference.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Đơn hàng đã được gửi đi", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Đơn hàng bị từ chối", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateOrderAcceptStatus(position: Int) {
        // Update order acceptance in user's BuyHistory and OrderDetails
        val userIdOfClickedItem = listOfOrderItem[position].userUid
        val pushKeyOfClickedItem = listOfOrderItem[position].itemPushKey
        val buyHistoryReference = database.reference.child("user").child(userIdOfClickedItem!!).child("BuyHistory").child(pushKeyOfClickedItem!!)
        buyHistoryReference.child("orderAccepted").setValue(true)
        databaseOrderDetails.child(pushKeyOfClickedItem).child("orderAccepted").setValue(true)
    }

//    override fun onItemRejectClickListener(position: Int) {
//        val rejectItemPushKey = listOfOrderItem[position].itemPushKey ?: return // Ensure push key is not null
//        val rejectItemOrderReference = database.reference.child("OrderDetails").child(rejectItemPushKey)
//
//        // Update order status to 'rejected'
//        rejectItemOrderReference.child("orderStatus").setValue("rejected")
//            .addOnSuccessListener {
//                // After successful update, delete order
//                rejectItemOrderReference.removeValue()
//                    .addOnSuccessListener {
//                        // If deletion is successful, notify user
//                        Toast.makeText(this, "Đơn hàng đã bị từ chối và xóa khỏi danh sách", Toast.LENGTH_SHORT).show()
//                        // Update list and notify adapter
//                        listOfOrderItem.removeAt(position)
//                        listOfName.removeAt(position) // Remove name
//                        listOfTotalPrice.removeAt(position) // Remove price
//                        listOfImageFirstFoodOrder.removeAt(position) // Remove image
//                        adapter.notifyItemRemoved(position) // Update adapter
//                    }
//                    .addOnFailureListener {
//                        // Notify error if deletion fails
//                        Toast.makeText(this, "Lỗi khi xóa đơn hàng", Toast.LENGTH_SHORT).show()
//                    }
//            }
//            .addOnFailureListener {
//                // Notify error if update fails
//                Toast.makeText(this, "Lỗi khi từ chối đơn hàng", Toast.LENGTH_SHORT).show()
//            }
//    }
}