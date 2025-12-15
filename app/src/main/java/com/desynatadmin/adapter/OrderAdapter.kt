package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemOrderBinding

class OrderAdapter(var context:Context, var orderList: ArrayList<String>, var onClick:OnClick) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnClick{
        fun onItemClick(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var orderData = orderList[position]
        holder.binding.apply {

            var type = 1
            when(type){
                1->{
                    btnAssignOrder.isVisible = true
                }
                2->{
                    btnAssignOrder.isVisible = true
                }
                3->{
                    btnAssignOrder.isVisible = false
                }
                4->{
                    btnAssignOrder.isVisible = false
                }

            }
            btnAssignOrder.setOnClickListener {
                onClick.onItemClick(position,1)
            }
        }

        holder.itemView.setOnClickListener {
            onClick.onItemClick(position,2)
        }

    }

    fun setData(newList: ArrayList<String>) {
        orderList = newList
        notifyDataSetChanged()
    }
}
