package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemEnrolledSellerBinding

class EnrolledAdapter(var context:Context, var enrolledList: ArrayList<String>, var clickOnItem:ClickOnItem) : RecyclerView.Adapter<EnrolledAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemEnrolledSellerBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface ClickOnItem{
        fun onClickItem(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEnrolledSellerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return enrolledList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var enrolledData = enrolledList[position]

        holder.binding.apply {

        }




    }

}
