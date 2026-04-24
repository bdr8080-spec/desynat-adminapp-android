package com.desynatadmin.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemCategoryBinding
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetCategoryResponse
import com.desynatadmin.utils.SharedPreference

class CategoryAdapter(var context:Context, private var categoryList: ArrayList<GetCategoryResponse.Data>, var categoryClick:CategoryClick) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) = with(binding){
            var actionAccess = true
            val categoryData = categoryList[position]
            val userInfo = SharedPreference.get(context).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(context).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("category_management", true)
                    }

                actionAccess = module?.canAction == 1
            }

                val params = root.layoutParams
                params.height = itemHeight
                root.layoutParams = params


                if (position % 2 == 0) {
                    clRight.isVisible = true
                    clLeft.isVisible = false
                } else {
                    clRight.isVisible = false
                    clLeft.isVisible = true
                }

                tvCategoryName.text = categoryData.name
                tvDescription.text = categoryData.description
                ivCategory.load(categoryData.image)

                relCategory.backgroundTintList = ColorStateList.valueOf(Color.parseColor(categoryData.colorCode))

                tvCategoryName1.text = categoryData.name
                tvDescription1.text = categoryData.description
                ivCategory1.load(categoryData.image)

                relCategory1.backgroundTintList = ColorStateList.valueOf(Color.parseColor(categoryData.colorCode))

                if(categoryData.status==1){
                    tvSwitch.text = "Active"
                    tvSwitch.setTextColor(Color.parseColor("#1A900D"))
                    tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_on, 0)
                }
                else{
                    tvSwitch.text = "Inactive"
                    tvSwitch.setTextColor(Color.parseColor("#666666"))
                    tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_off, 0)

                }

                if(categoryData.status==1){
                    tvSwitch1.text = "Active"
                    tvSwitch1.setTextColor(Color.parseColor("#1A900D"))
                    tvSwitch1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_on, 0)
                }
                else{
                    tvSwitch1.text = "Inactive"
                    tvSwitch1.setTextColor(Color.parseColor("#666666"))
                    tvSwitch1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_off, 0)
                }

                tvSwitch.setOnClickListener {
                    if (actionAccess){
                        categoryClick.onCategoryClick(position,1)
                    }

                }

                tvSwitch1.setOnClickListener {
                    if (actionAccess) {
                        categoryClick.onCategoryClick(position, 1)
                    }
                }

            root.setOnClickListener {
                categoryClick.onCategoryClick(position,0)

            }
        }
        }

    interface CategoryClick{
        fun onCategoryClick(position: Int,type:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCategoryBinding.inflate(
                android.view.LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(position)
    }

    private val itemHeight: Int by lazy {
        val displayMetrics = context.resources.displayMetrics
        displayMetrics.heightPixels / 4
    }


}
