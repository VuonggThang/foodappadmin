//package com.example.app2.adapter
//
//import android.content.res.ColorStateList
//import android.graphics.Color
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.example.app2.databinding.DeliveryItemBinding
//
//class DeliveryAdapter(private val customerNames :MutableList<String>,
//                      private val moneyStatus :MutableList<Boolean>
//     ) :RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>(){
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
//        val binding = DeliveryItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
//        return DeliveryViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
//        holder.bind(position)
//    }
//
//    override fun getItemCount(): Int = customerNames.size
//    inner class DeliveryViewHolder (private val binding:DeliveryItemBinding) : RecyclerView.ViewHolder(binding.root){
//        fun bind(position: Int) {
//            binding.apply {
//                customerName.text = customerNames[position]
//                if(moneyStatus[position] == true){
//                    statusMoney.text = "Nhận"
//                }else{
//                    statusMoney.text = "Chưa nhận"
//                }
//                val colorMap = mapOf(
//                    true to Color.GREEN, false to Color.RED
//                )
//                statusMoney.setTextColor(colorMap[moneyStatus[position]]?:Color.BLACK)
//                statusColor.backgroundTintList = ColorStateList.valueOf(colorMap[moneyStatus[position]]?:Color.BLACK)
//
//            }
//        }
//
//    }
//}

package com.example.app2.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.app2.OrderDetailsActivity
import com.example.app2.databinding.DeliveryItemBinding
import com.example.app2.model.OrderDetails

class DeliveryAdapter(
    private val context: Context,
    private val orderList: List<OrderDetails> // Thay đổi thành danh sách OrderDetails
) : RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val binding = DeliveryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeliveryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        holder.bind(orderList[position])
    }

    override fun getItemCount(): Int = orderList.size

    inner class DeliveryViewHolder(private val binding: DeliveryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(orderDetails: OrderDetails) {
            // Hiển thị thông tin đơn hàng
            binding.customerName.text = orderDetails.userName
            binding.statusMoney.text = if (orderDetails.paymentReceived) "Nhận" else "Chưa nhận"

            // Đổi màu theo trạng thái thanh toán
            val colorMap = mapOf(true to Color.GREEN, false to Color.RED)
            binding.statusMoney.setTextColor(colorMap[orderDetails.paymentReceived] ?: Color.BLACK)
            binding.statusColor.backgroundTintList = ColorStateList.valueOf(colorMap[orderDetails.paymentReceived] ?: Color.BLACK)

            // Sự kiện click để chuyển sang OrderDetailsActivity
            binding.root.setOnClickListener {
                val intent = Intent(context, OrderDetailsActivity::class.java)
                intent.putExtra("UserOrderDetails", orderDetails) // Truyền OrderDetails qua Intent
                context.startActivity(intent)
            }
        }
    }
}
