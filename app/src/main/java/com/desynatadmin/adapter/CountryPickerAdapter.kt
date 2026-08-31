package com.desynatadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.databinding.ItemCountryBinding
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.SharedPreference

class CountryPickerAdapter(
    var context: Context,
    var countryList: ArrayList<GetCountryResponse.Data>,
    var isDialCode: Boolean = false,
    var onClick: ClickOnItem
) : RecyclerView.Adapter<CountryPickerAdapter.ViewHolder>() {

    interface ClickOnItem {
        fun onClickItem(position: Int)
    }

    // Adapter-level selected position
    private var selectedPosition = 1

    inner class ViewHolder(
        val binding: ItemCountryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) = with(binding) {
            val country = countryList[position]

            tvCountry.text = if (isDialCode) {
                "${country.flag}  ${
                    if (Constants.language == "ar") country.nameAr else country.name
                } (${country.dialCode})"
            } else {
                "${country.flag}  ${
                    if (Constants.language == "ar") country.nameAr else country.name
                }"
            }

            ivFlag.isVisible = false

            // Highlight selected item
            root.isSelected = position == selectedPosition

            root.setOnClickListener {
                val oldPosition = selectedPosition

                selectedPosition = position

                SharedPreference.get(context).selectedCountry = country.name

                // Refresh old and new selected items
                notifyItemChanged(oldPosition)
                notifyItemChanged(selectedPosition)

                onClick.onClickItem(position)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ItemCountryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return countryList.size
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }
}
