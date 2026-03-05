package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemTicketBinding

class TicketAdapter(var context:Context, private var ticketList: ArrayList<String>, var ticketClick: TicketClick) : RecyclerView.Adapter<TicketAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemTicketBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val ticketData = ticketList[position]


            root.setOnClickListener {
                ticketClick.onTicketClick(position,0)

            }
        }
        }

    interface TicketClick{
        fun onTicketClick(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTicketBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return ticketList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(position)
    }

}
