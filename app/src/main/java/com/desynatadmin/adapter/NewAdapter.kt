package com.desynatadmin.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.NewItemBinding
import com.desynatadmin.ui.home.brandManagement.response.NewSellerResponse
import com.desynatadmin.utils.formatUtcDateToDdMmYyyy

class NewAdapter(val list: ArrayList<NewSellerResponse.Data.Data>, val click: OnClick) :
    RecyclerView.Adapter<NewAdapter.ViewHolder>() {
    class ViewHolder(val binding: NewItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewAdapter.ViewHolder {
        val binding = NewItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val item = list[position]
            tvName.text = item.first_name
            tvLocation.text = item.store_info.address_line1
            tvCall.text = item.country_code + "-" + item.mobile
            tvEmail.text = item.email
            tvJoinedOn.text = "Joined on ${formatUtcDateToDdMmYyyy(item.store_info.updated_at)}"
            root.setOnClickListener {
                    click.onCLick(position)
                }
            btnAccept.setOnClickListener {
                click.onAccept(position)
            }

            btnReject.setOnClickListener {
                click.onDelete(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClick {
        fun onCLick(position: Int)
        fun onAccept(position: Int)
        fun onDelete(position: Int)
    }

}

