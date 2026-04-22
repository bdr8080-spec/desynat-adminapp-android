package com.desynatadmin.ui.home.subAdminManagement.response


import com.google.gson.annotations.SerializedName

data class GetSubAdminPanelModuleResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("display_name")
        val displayName: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}