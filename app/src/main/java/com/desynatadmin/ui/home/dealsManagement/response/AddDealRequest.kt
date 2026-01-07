package com.desynatadmin.ui.home.dealsManagement.response


import com.google.gson.annotations.SerializedName

data class AddDealRequest(
    @SerializedName("category_id")
    val categoryId: String,
    @SerializedName("discount")
    val discount: String,
    @SerializedName("from")
    val from: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("name_arabic")
    val nameArabic: String,
    @SerializedName("name_english")
    val nameEnglish: String,
    @SerializedName("sub_category_id")
    val subCategoryId: String,
    @SerializedName("to")
    val to: String,
    @SerializedName("deal_id")
    val dealId: String?=null
)