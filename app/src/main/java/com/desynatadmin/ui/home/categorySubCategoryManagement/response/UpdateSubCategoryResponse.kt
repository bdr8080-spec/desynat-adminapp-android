package com.desynatadmin.ui.home.categorySubCategoryManagement.response


import com.google.gson.annotations.SerializedName

data class UpdateSubCategoryResponse(
    @SerializedName("data")
    val `data`: GetSubCategoryResponse.Data.Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("category_id")
        val categoryId: Int,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("image")
        val image: String,
        @SerializedName("name_arabic")
        val nameArabic: String,
        @SerializedName("name_english")
        val nameEnglish: String,
        @SerializedName("status")
        val status: Int,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}