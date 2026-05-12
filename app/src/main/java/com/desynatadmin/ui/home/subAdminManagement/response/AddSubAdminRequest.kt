package com.desynatadmin.ui.home.subAdminManagement.response


import com.google.gson.annotations.SerializedName

data class AddSubAdminRequest(
    @SerializedName("confirm_password")
    val confirmPassword: String,
    @SerializedName("country_code")
    val countryCode: String,
    @SerializedName("country_id")
    val countryId: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("full_name_ar")
    val fullNameAr: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("modules")
    val modules: List<Module>,
    @SerializedName("password")
    val password: String,
    @SerializedName("profile_pic")
    val profilePic: String,
    @SerializedName("id")
    val id: String?=null
) {
    data class Module(
        @SerializedName("name")
        val name: String,
        @SerializedName("name_ar")
        val nameAr: String,
        @SerializedName("can_action")
        var canAction: Int,
        @SerializedName("can_view")
        var canView: Int,
        @SerializedName("module_id")
        val moduleId: Int,
        @SerializedName("is_selected")
        var isSelected: Boolean=false
    )
}