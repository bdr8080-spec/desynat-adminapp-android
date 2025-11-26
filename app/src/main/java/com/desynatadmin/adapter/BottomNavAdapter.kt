package com.desynatadmin.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.desynatadmin.R
import com.desynatadmin.databinding.ItemBottomNavBinding
import com.desynatadmin.ui.home.BottomNavItem

class BottomNavAdapter(var context:Context, var list: ArrayList<BottomNavItem>, var bottomNavItemClick:BottomNavItemClick) : RecyclerView.Adapter<BottomNavAdapter.ViewHolder>() {

    var selected = 0
    inner class ViewHolder(val binding: ItemBottomNavBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface BottomNavItemClick{
        fun onBottomNavItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBottomNavBinding.inflate(
                android.view.LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.tvName.text = list[position].name
        holder.binding.ivImage.setImageResource(list[position].image)

        if (selected==position&&selected!=2){
            holder.binding.ivImage.setImageResource(list[position].selectedImage)
          //  holder.binding.tvName.setTextColor(context.getColor(R.color.brown))
            applyGradientText(holder.binding.tvName)
            holder.binding.tvName.typeface = ResourcesCompat.getFont(context, R.font.poppins_medium)
            holder.binding.ivImage.isVisible = true
            holder.binding.tvName.isVisible = true
            holder.binding.ivScanner.isVisible = false
        }
        else if (position==2){
            holder.binding.ivScanner.setImageResource(list[position].image)
            holder.binding.ivImage.isVisible = false
            holder.binding.tvName.isVisible = false
            holder.binding.ivScanner.isVisible = true
        }
        else{
            holder.binding.ivImage.setImageResource(list[position].image)
            holder.binding.tvName.paint.shader = null
            holder.binding.tvName.setTextColor(context.getColor(R.color.black))
            holder.binding.tvName.typeface = ResourcesCompat.getFont(context, R.font.poppins_regular)
            holder.binding.ivImage.isVisible = true
            holder.binding.tvName.isVisible = true
            holder.binding.ivScanner.isVisible = false
        }

        holder.itemView.setOnClickListener {
            bottomNavItemClick.onBottomNavItemClick(position)
            val previousSelected = selected
            selected = position
            notifyItemChanged(previousSelected)
            notifyItemChanged(selected)
        }

    }

    fun setSelectedItem(position: Int) {
        val oldPos = selected
        selected = position
        notifyItemChanged(oldPos)
        notifyItemChanged(selected)
    }

    fun applyGradientText(
        textView: AppCompatTextView,
        startColor: String = "#DFBF8F",
        endColor: String = "#6E4527"
    ) {

        textView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View?,
                left: Int, top: Int, right: Int, bottom: Int,
                oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
            ) {
                textView.removeOnLayoutChangeListener(this)

                val width = textView.paint.measureText(textView.text.toString())

                val shader = LinearGradient(
                    0f,
                    0f,
                    width,
                    textView.textSize,
                    intArrayOf(
                        Color.parseColor(startColor),
                        Color.parseColor(endColor)
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )
                textView.paint.shader = shader
            }
        })
    }

}
