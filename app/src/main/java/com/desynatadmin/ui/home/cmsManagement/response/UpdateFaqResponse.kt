package com.desynatadmin.ui.home.cmsManagement.response


import com.google.gson.annotations.SerializedName

data class UpdateFaqResponse(
    @SerializedName("data")
    val `data`: GetFaqResponse.Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("answer_arabic")
        val answerArabic: String,
        @SerializedName("answer_english")
        val answerEnglish: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("question_arabic")
        val questionArabic: String,
        @SerializedName("question_english")
        val questionEnglish: String,
        @SerializedName("type")
        val type: Int,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}