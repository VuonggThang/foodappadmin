package com.example.app2.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app2.R
import com.example.app2.model.Cancel

class CancelOrderAdapter(private val cancelOrderList: List<Cancel>) :
    RecyclerView.Adapter<CancelOrderAdapter.CancelOrderViewHolder>() {

    class CancelOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val customerNameTextView: TextView = itemView.findViewById(R.id.customerName)
        val reasonTextView: TextView = itemView.findViewById(R.id.reasonCancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CancelOrderViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cancle_item, parent, false)
        return CancelOrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CancelOrderViewHolder, position: Int) {
        val cancelOrder = cancelOrderList[position]
        holder.customerNameTextView.text = cancelOrder.customerName
        holder.reasonTextView.text = cancelOrder.reason
    }

    override fun getItemCount() = cancelOrderList.size
}
