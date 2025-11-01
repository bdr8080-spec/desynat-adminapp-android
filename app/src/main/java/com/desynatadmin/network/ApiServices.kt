package com.desynatadmin.network

import com.desynatadmin.ui.auth.LoginResponse
import com.desynatadmin.ui.home.brandManagement.response.FileUploadResponse
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetCategoryResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse
import com.desynatadmin.ui.home.response.CommonResponse
import com.desynatadmin.utils.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query

interface ApiServices {

    @Multipart
    @POST(Constants.UPLOAD)
    suspend fun uploadImage(
        @Part file: MultipartBody.Part? = null
    ): Response<FileUploadResponse>


    @POST(Constants.LOGIN)
    suspend fun login(
        @Body body: HashMap<String, Any>
    ): Response<LoginResponse>

    @POST(Constants.OTP_VERIFY)
    suspend fun otpVerification(
        @Body body: HashMap<String, Any>
    ): Response<LoginResponse>

    @POST(Constants.RESEND_OTP)
    suspend fun resendOtp(
        @Body body: HashMap<String, Any>
    ): Response<LoginResponse>

    @POST(Constants.FORGOT_PASSWORD)
    suspend fun forgotPassword(
        @Body body: HashMap<String, Any>
    ): Response<LoginResponse>

    @POST(Constants.CREATE_PASSWORD)
    suspend fun createPassword(
        @Body body: HashMap<String, Any>
    ): Response<LoginResponse>

    @POST(Constants.LOGOUT)
    suspend fun logout(
    ): Response<CommonResponse>

    @GET(Constants.CATEGORY)
    suspend fun getCategory(
    ): Response<GetCategoryResponse>

    @GET(Constants.SUB_CATEGORY)
    suspend fun getSubCategory(
        @Query ("category_id") categoryId:String,
        @Query("search") search : String?=null,
        @Query("page") page: Int,
        @Query("limit") limit: Int? = 10,
    ): Response<GetSubCategoryResponse>

    @GET(Constants.BRAND)
    suspend fun getBrand(
        @Query("search") search : String?=null,
        @Query("page") page: Int,
        @Query("limit") limit: Int? = 10,

        ): Response<GetBrandResponse>

    @Multipart
    @POST(Constants.ADD_BRAND)
    suspend fun addBrand(
        @PartMap map : HashMap<String,RequestBody>): Response<CommonResponse>

    @Multipart
    @POST(Constants.UPDATE_BRAND)
    suspend fun editBrand(
        @PartMap map : HashMap<String,RequestBody>): Response<CommonResponse>

    @POST(Constants.UPDATE_BRAND_STATUS)
    suspend fun updateBrandStatus(
        @Body body: HashMap<String,Any>
    ): Response<CommonResponse>

    @POST(Constants.DELETE_BRAND)
    suspend fun deleteBrand(
        @Body body: HashMap<String,Any>
    ): Response<CommonResponse>

    @Multipart
    @POST(Constants.ADD_SUB_CATEGORY)
    suspend fun addSubCategory(
        @PartMap map : HashMap<String,RequestBody>): Response<CommonResponse>

    @Multipart
    @POST(Constants.UPDATE_SUB_CATEGORY)
    suspend fun editSubCategory(
        @PartMap map : HashMap<String,RequestBody>): Response<CommonResponse>

    @POST(Constants.UPDATE_SUB_CATEGORY_STATUS)
    suspend fun updateSubCategoryStatus(
        @Body body: HashMap<String,Any>
    ): Response<CommonResponse>

    @POST(Constants.DELETE_SUB_CATEGORY)
    suspend fun deleteSubCategory(
        @Body body: HashMap<String,Any>
    ): Response<CommonResponse>
}