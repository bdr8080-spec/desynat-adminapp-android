package com.desynatadmin.ui.home.productManagement.response


import com.google.gson.annotations.SerializedName

data class UpdateProductStatusResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("added_by")
        val addedBy: String,
        @SerializedName("affiliated_commission")
        val affiliatedCommission: Any,
        @SerializedName("affiliated_product")
        val affiliatedProduct: String,
        @SerializedName("brand_id")
        val brandId: String,
        @SerializedName("cash_on_delivery")
        val cashOnDelivery: String,
        @SerializedName("category_id")
        val categoryId: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("customized_product")
        val customizedProduct: String,
        @SerializedName("description_ar")
        val descriptionAr: String,
        @SerializedName("description_en")
        val descriptionEn: String,
        @SerializedName("exchangable_days")
        val exchangableDays: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("max_card_quantity")
        val maxCardQuantity: String,
        @SerializedName("min_card_quantity")
        val minCardQuantity: String,
        @SerializedName("other_ar")
        val otherAr: Any,
        @SerializedName("other_en")
        val otherEn: Any,
        @SerializedName("product_active_status")
        val productActiveStatus: Int,
        @SerializedName("product_exchangable")
        val productExchangable: String,
        @SerializedName("product_name_ar")
        val productNameAr: String,
        @SerializedName("product_name_en")
        val productNameEn: String,
        @SerializedName("product_return")
        val productReturn: String,
        @SerializedName("ready_to_ship")
        val readyToShip: String,
        @SerializedName("return_days")
        val returnDays: String,
        @SerializedName("review_rating")
        val reviewRating: String,
        @SerializedName("seller_id")
        val sellerId: String,
        @SerializedName("store_id")
        val storeId: String,
        @SerializedName("subcategory_id")
        val subcategoryId: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("upload_size_chart")
        val uploadSizeChart: String
    )
}