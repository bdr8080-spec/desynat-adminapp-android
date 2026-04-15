package com.desynatadmin.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemTicketBinding
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.ticketManagement.response.GetNewTicketResponse
import com.desynatadmin.utils.CommonMethod

class TicketAdapter(var context:Context, private var ticketList: ArrayList<GetNewTicketResponse.Data.Data>, var ticketClick: TicketClick) : RecyclerView.Adapter<TicketAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemTicketBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val ticketData = ticketList[position]

            when(ticketData?.senderType){
                1-> {
                    tvSubmittedBy.text = "Customer"
                    tvRequestByName.text = ticketData?.senderDetails?.fullName
                }
                2->{
                    tvRequestByName.text = ticketData?.senderDetails?.firstName+" "+ticketData?.senderDetails?.lastName
                 tvSubmittedBy.text = "Seller"}
                3-> {
                    tvRequestByName.text = ticketData?.senderDetails?.fullName
                    tvSubmittedBy.text = "Affiliate"
            }
                4-> {
                    tvRequestByName.text = ticketData?.senderDetails?.fullNameEnglish
                    tvSubmittedBy.text = "Driver"
                }
            }

            tvTicketIdValue.text = ticketData?.ticketId
            tvTicketTypeValue.text = ticketData?.ticketTypeDetails?.name
            tvRaisedOn.text = CommonMethod.customizeChangeDateFormat(ticketData?.createdAt.toString(), outputFormat = "dd MMM yy, hh:mm a")

            when(ticketData?.status){
                0->{
                    tvStatusValue.text = "In Progress"
                    tvStatusValue.setTextColor(Color.parseColor("#D72E04"))

                }
                1->{
                    tvStatusValue.text = "Resolved"
                    tvStatusValue.setTextColor(Color.parseColor("#17854C"))
                }
            }

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


    fun setData(newList: ArrayList<GetNewTicketResponse.Data.Data>) {
        ticketList = newList
        notifyDataSetChanged()
    }

}
