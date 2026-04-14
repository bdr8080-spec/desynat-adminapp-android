package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemTimeSlotBinding
import com.desynatadmin.ui.home.liveRequestAndTimeSlotManagement.response.GetTimeSlotResponse
import com.desynatadmin.utils.CommonMethod

class TimeSlotAdapter(var context:Context, var timeSLotList: ArrayList<GetTimeSlotResponse.Data>, var onClick:OnClick) : RecyclerView.Adapter<TimeSlotAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemTimeSlotBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            val timeSLotData = timeSLotList[position]
            tvTime.text = "${CommonMethod.customizeChangeDateFormatKuwait(timeSLotData?.from.toString(),"hh:mm a", outputFormat = "hh:mm a")} - ${CommonMethod.customizeChangeDateFormatKuwait(timeSLotData?.to.toString(),"hh:mm a", outputFormat = "hh:mm a")}"

          //  tvTime.text = "${timeSLotData?.from} TO ${timeSLotData?.to}"

            if(timeSLotData?.status==1){
                ivSwitch.setImageResource(R.drawable.switch_on)
            }
            else{
                ivSwitch.setImageResource(R.drawable.switch_off)

            }
            ivDelete.setOnClickListener {
                onClick.click(position,2)
            }

            ivEdit.setOnClickListener {
                onClick.click(position,1)
            }

            ivSwitch.setOnClickListener {
                onClick.click(position,3)
            }
        }
        }

    interface OnClick{
        fun click(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTimeSlotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return timeSLotList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

}
