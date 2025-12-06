package com.desynatadmin.network

import com.desynatadmin.ui.auth.LoginResponse
import com.desynatadmin.ui.home.banner.response.GetBannerPricePerDayResponse
import com.desynatadmin.ui.home.banner.response.UpdateBannerPriceResponse
import com.desynatadmin.ui.home.brandManagement.response.AcceptSellerResponse
import com.desynatadmin.ui.home.brandManagement.response.AddServiceCityResponse
import com.desynatadmin.ui.home.brandManagement.response.AddZoneRequest
import com.desynatadmin.ui.home.brandManagement.response.AddZoneResponse
import com.desynatadmin.ui.home.brandManagement.response.ApprovedSellerResponse
import com.desynatadmin.ui.home.brandManagement.response.ChangeCityStatusResponse
import com.desynatadmin.ui.home.brandManagement.response.CityResponse
import com.desynatadmin.ui.home.brandManagement.response.FileUploadResponse
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.brandManagement.response.NewSellerResponse
import com.desynatadmin.ui.home.brandManagement.response.PlaceDetailsResponse
import com.desynatadmin.ui.home.brandManagement.response.PlacesResponse
import com.desynatadmin.ui.home.brandManagement.response.RejectedListResponse
import com.desynatadmin.ui.home.brandManagement.response.SellerDetailResponse
import com.desynatadmin.ui.home.brandManagement.response.SellerStatusResponse
import com.desynatadmin.ui.home.brandManagement.response.ServiceCityListResponse
import com.desynatadmin.ui.home.brandManagement.response.ServiceRequest
import com.desynatadmin.ui.home.brandManagement.response.UpdateBrandStatusResponse
import com.desynatadmin.ui.home.brandManagement.response.UpdateZonResponse
import com.desynatadmin.ui.home.brandManagement.response.UpdateZoneStatusResponse
import com.desynatadmin.ui.home.brandManagement.response.ZoneListResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetCategoryResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.UpdateCategoryResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.UpdateSubCategoryResponse
import com.desynatadmin.ui.home.deliveryBoyManagement.response.AddDeliveryBoyRequest
import com.desynatadmin.ui.home.deliveryBoyManagement.response.BlockUnblockDeliveryBoyResponse
import com.desynatadmin.ui.home.deliveryBoyManagement.response.GetAllDeliveryBoyResponse
import com.desynatadmin.ui.home.deliveryBoyManagement.response.GetDeliveryBoyDetailResponse
import com.desynatadmin.ui.home.deliveryBoyManagement.response.GetKuwaitZoneResponse
import com.desynatadmin.ui.home.giftCardManagement.response.GetAllGiftCardResponse
import com.desynatadmin.ui.home.giftCardManagement.response.UpdateGiftCardResponse
import com.desynatadmin.ui.home.promoCodeManagement.response.AddPromoCodeRequest
import com.desynatadmin.ui.home.promoCodeManagement.response.GetPromoCodeResponse
import com.desynatadmin.ui.home.promoCodeManagement.response.UpdatePromoCodeResponse
import com.desynatadmin.ui.home.reasonManagement.response.BlockUnblockReasonResponse
import com.desynatadmin.ui.home.reasonManagement.response.GetReasonResponse
import com.desynatadmin.ui.home.reasonManagement.response.UpdateReasonResponse
import com.desynatadmin.ui.home.response.CommonResponse
import com.desynatadmin.ui.home.shippingAndTaxManagement.response.AddTaxResponse
import com.desynatadmin.ui.home.shippingAndTaxManagement.response.GetShippingTaxResponse
import com.desynatadmin.ui.home.shippingAndTaxManagement.response.UpdateShippingChargeResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.AddPlanRequest
import com.desynatadmin.ui.home.subscriptionPlan.response.GetAllPlanResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.UpdatePlanStatusResponse
import com.desynatadmin.ui.home.userManagement.response.AddWalletBalanceResponse
import com.desynatadmin.ui.home.userManagement.response.BlockUnblockUserResponse
import com.desynatadmin.ui.home.userManagement.response.GetUserDetailResponse
import com.desynatadmin.ui.home.userManagement.response.GetUserResponse
import com.desynatadmin.utils.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
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
    ): Response<UpdateBrandStatusResponse>

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
    ): Response<UpdateSubCategoryResponse>

    @POST(Constants.UPDATE_CATEGORY_STATUS)
    suspend fun updateCategoryStatus(
        @Body body: HashMap<String,Any>
    ): Response<UpdateCategoryResponse>

    @POST(Constants.DELETE_SUB_CATEGORY)
    suspend fun deleteSubCategory(
        @Body body: HashMap<String,Any>
    ): Response<CommonResponse>

    @GET(Constants.GET_SUBSCRIPTION_PLAN)
    suspend fun getSubscriptionPlan(
        @Query ("country_id") countryId:String?=null
        ): Response<GetAllPlanResponse>

    @GET(Constants.GET_SUBSCRIPTION_PLAN_DETAILS)
    suspend fun getSubscriptionPlanDetail(
        @Query ("plan_id") planId:String?=null
        ): Response<UpdatePlanStatusResponse>

    @POST(Constants.ADD_SUBSCRIPTION_PLAN)
    suspend fun addSubscriptionPlan(
        @Body request: AddPlanRequest): Response<CommonResponse>

    @POST(Constants.UPDATE_SUBSCRIPTION_PLAN)
    suspend fun updateSubscriptionPlan(
        @Body request: AddPlanRequest
    ): Response<CommonResponse>

    @POST(Constants.CHANGE_PLAN_STATUS)
    suspend fun updateSubscriptionPlanStatus(
        @Body body: HashMap<String,Any>): Response<UpdatePlanStatusResponse>

    @POST(Constants.DELETE_SUBSCRIPTION_PLAN)
    suspend fun deleteSubscriptionPlan(
        @Body body: HashMap<String,Any>
    ): Response<CommonResponse>

    @GET(Constants.GET_CITY)
    suspend fun getCity(
        @Header("Token") Token: String,
        @Query("country_id") country_id: String,
        ): Response<CityResponse>

    @POST(Constants.ADD_SERVICE_CITY)
    suspend fun hitAddServiceCity(
        @Header("Token") Token: String,
        @Body body: HashMap<String, Any>
    ): Response<AddServiceCityResponse>

    @GET(Constants.GET_COUNTRY)
    suspend fun getCountry(
    ): Response<GetCountryResponse>

    @GET(Constants.GET_SERVICE_CITY)
    suspend fun hitServiceCityList(
        @Header("Token") Token: String,
        @Query("country_id") country_id: String,
        @Query("limit") limit: String,
        @Query("search") search: String,
        @Query("page") page: String,
    ): Response<ServiceCityListResponse>

    @POST(Constants.GET_CHANGE_SERVICE_STATUS)
    suspend fun hitChangeStatus(
        @Header("Token") Token: String,
        @Body body: HashMap<String, Any>
    ) : Response<ChangeCityStatusResponse>

    @POST(Constants.GET_DELETE_CITY_SERVICE)
    suspend fun hitDeleteServiceCity(
        @Header("Token") Token: String,
        @Body body: HashMap<String, Any>
    ) : Response<CommonResponse>

    @POST(Constants.ADD_ZONE)
    suspend fun hitAddZone(
        @Header("Token") Token: String,
        @Body body: AddZoneRequest
    ) : Response<AddZoneResponse>

    @GET(Constants.NEW_SELLER)
    suspend fun hitNewSelleList(
        @Header("Token") Token: String,
        @Query("country_id") country_id: String,
        @Query("from_date") from_date: String,
        @Query("to_date") to_date: String,
        @Query("limit") limit: String,
        @Query("page") page: String,
        @Query("search") search: String,
    ): Response<NewSellerResponse>

    @GET(Constants.APPROVED_SELLER)
    suspend fun hitApprovedSeller(
        @Header("Token") Token: String,
        @Query("country_id1") country_id: String,
        @Query("from_date1") from_date: String,
        @Query("to_date1") to_date: String,
        @Query("limit1") limit: String,
        @Query("search1") search: String,
    ): Response<ApprovedSellerResponse>

    @GET(Constants.REJECTED_SELLER)
    suspend fun hitRejectedSeller(
        @Header("Token") Token: String,
        @Query("country_id2") country_id: String,
        @Query("from_date2") from_date: String,
        @Query("to_date2") to_date: String,
        @Query("limit2") limit: String,
        @Query("search2") search: String,
    ): Response<RejectedListResponse>

    @GET(Constants.SELLER_DETAIL)
    suspend fun hitSellerDetail(
        @Header("Token") Token: String,
        @Query("seller_id") seller_id: String,
    ): Response<SellerDetailResponse>

    @POST(Constants.ACCEPT_SELLER)
    suspend fun hitAcceptSeller(
        @Header("Token") Token: String,
        @Body body: HashMap<String, Any>
    ): Response<AcceptSellerResponse>

    @POST(Constants.REJECT_SELLER)
    suspend fun hitRejectSeller(
        @Header("Token") Token: String,
        @Body body: HashMap<String, Any>
    ): Response<AcceptSellerResponse>


    @GET("https://maps.googleapis.com/maps/api/place/autocomplete/json")
    suspend fun getPlaceSuggestions(
        @Query("input") input: String,
        @Query("key") apiKey: String,

        @Query("language") language: String = "en"
    ): Response<PlacesResponse>


    @GET("https://maps.googleapis.com/maps/api/place/details/json")
    suspend fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("key") apiKey: String
    ): Response<PlaceDetailsResponse>

    @GET(Constants.GET_ZONE_LIST)
    suspend fun hitZoneList(
        @Header("Token") Token: String,
        @Query("country_id") country_id: String,
        @Query("city_id") city_id: String,
    ): Response<ZoneListResponse>

    @POST(Constants.UPDATE_ZONE)
    suspend fun hitUpdateZone(
        @Header("Token") Token: String,
        @Body req : AddZoneRequest
    ): Response<UpdateZonResponse>

    @POST(Constants.UPDATE_ZONE_STATUS)
    suspend fun hitUpdateZoneStatus(
        @Header("Token") Token: String,
        @Body map : HashMap<String, Any>
    ): Response<UpdateZoneStatusResponse>


    @GET("api/admin/get_new_sellers")
    suspend fun filter(
        @Header("Token") Token: String,
        @Query("country_id")country_id:String,
        @Query("from_date")from_date:String,
        @Query("to_date")to_date:String,

    ): Response<UpdateZonResponse>

    @POST(Constants.DELETE_ZONE)
    suspend fun hitDeleteZone(
        @Header("Token") Token: String,
        @Body body: HashMap<String, Any>
    ): Response<AcceptSellerResponse>

    @POST(Constants.UPDATE_SELLER_STATUS)
    suspend fun hitSellerStatus(
        @Header("Token") Token: String,
        @Body body: HashMap<String, Any>
    ): Response<SellerStatusResponse>

    @GET(Constants.GET_USER)
    suspend fun getUser(
        @Query("search") search : String?=null,
        @Query("page") page: Int,
        @Query("country_id") countryId : String?=null,
        @Query("from_date") fromDate : String?=null,
        @Query("to_date") toDate : String?=null,
        @Query("limit") limit: Int? = 10,

        ): Response<GetUserResponse>

    @GET(Constants.GET_USER_DETAIL)
    suspend fun getUserDetail(
        @Query("user_id") userId : String,
        ): Response<GetUserDetailResponse>

    @POST(Constants.BLOCK_UNBLOCK_USER)
    suspend fun blockUnblockUser(
        @Body body: HashMap<String, Any>
    ): Response<BlockUnblockUserResponse>

    @POST(Constants.ADD_WALLET_AMOUNT)
    suspend fun addWalletAmount(
        @Body body: HashMap<String, Any>
    ): Response<AddWalletBalanceResponse>

    @GET(Constants.GET_REASON)
    suspend fun getReason(
        @Query("type") type : Int=1,
        @Query("search") search : String?=null,
        @Query("page") page: Int,
        @Query("limit") limit: Int? = 10, ): Response<GetReasonResponse>

    @POST(Constants.BLOCK_UNBLOCK_REASON)
    suspend fun blockUnblockReason(
        @Body body: HashMap<String, Any>
    ): Response<BlockUnblockReasonResponse>

    @POST(Constants.DELETE_REASON)
    suspend fun deleteReason(
        @Body body: HashMap<String, Any>
    ): Response<CommonResponse>

    @POST(Constants.ADD_REASON)
    suspend fun addReason(
        @Body body: HashMap<String, Any>
    ): Response<CommonResponse>

    @POST(Constants.UPDATE_REASON)
    suspend fun updateReason(
        @Body body: HashMap<String, Any>
    ): Response<UpdateReasonResponse>

    @GET(Constants.GET_KUWAIT_ZONE)
    suspend fun getKuwaitZone():Response<GetKuwaitZoneResponse>

    @GET(Constants.GET_USER_COUNTRY)
    suspend fun getUserCountry():Response<GetCountryResponse>

    @GET(Constants.GET_DELIVERY_BOY)
    suspend fun getDeliveryBoy(
        @Query("search") search : String?=null,
        @Query("page") page: Int,
        @Query("from_date") fromDate : String?=null,
        @Query("to_date") toDate : String?=null,
        @Query("limit") limit: Int? = 10, ): Response<GetAllDeliveryBoyResponse>

    @POST(Constants.GET_DELIVERY_BOY_DETAIL)
    suspend fun getDeliveryBoyDetail(
        @Query("user_id") userId : String,
    ): Response<GetDeliveryBoyDetailResponse>

    @POST(Constants.BLOCK_UNBLOCK_DELIVERY_BOY)
    suspend fun blockUnblockDeliveryBoy(
        @Body body: HashMap<String, Any>
    ): Response<BlockUnblockDeliveryBoyResponse>

    @POST(Constants.DELETE_DELIVERY_BOY)
    suspend fun deleteDeliveryBoy(
        @Body body: HashMap<String, Any>
    ): Response<CommonResponse>

    @POST(Constants.ADD_DELIVERY_BOY)
    suspend fun addDeliveryBoy(
        @Body request: AddDeliveryBoyRequest
    ): Response<CommonResponse>

    @POST(Constants.UPDATE_DELIVERY_BOY)
    suspend fun updateDeliveryBoy(
        @Body request: AddDeliveryBoyRequest
    ): Response<CommonResponse>

    @GET(Constants.GET_PROMO_CODE)
    suspend fun getPromoCode(
        @Query("search") search : String?=null,
        @Query("from_date") fromDate : String?=null,
        @Query("to_date") toDate : String?=null,
        @Query("country_id") countryId : String?=null,
      /*  @Query("page") page: Int,
        @Query("limit") limit: Int? = 10, */
        ): Response<GetPromoCodeResponse>

    @POST(Constants.UPDATE_STATUS_PROMO_CODE)
    suspend fun updatePromoCodeStatus(
        @Body body: HashMap<String, Any>
    ): Response<UpdatePromoCodeResponse>

    @POST(Constants.DELETE_PROMO_CODE)
    suspend fun deletePromoCode(
        @Body body: HashMap<String, Any>
    ): Response<CommonResponse>

    @POST(Constants.ADD_PROMO_CODE)
    suspend fun addPromoCode(
        @Body request: AddPromoCodeRequest
    ): Response<CommonResponse>

    @POST(Constants.UPDATE_PROMO_CODE)
    suspend fun updatePromoCode(
        @Body request: AddPromoCodeRequest
    ): Response<UpdatePromoCodeResponse>

    @GET(Constants.GIFT_CARD)
    suspend fun getGiftCard(
        @Query("search") search : String?=null,
        @Query("page") page: Int,
        @Query("limit") limit: Int? = 10,
    ): Response<GetAllGiftCardResponse>

    @POST(Constants.ADD_GIFT_CARD)
    suspend fun addGiftCard(
        @Body map : HashMap<String,Any>): Response<CommonResponse>

    @POST(Constants.UPDATE_GIFT_CARD)
    suspend fun editGiftCard(
        @Body map : HashMap<String,Any>): Response<UpdateGiftCardResponse>

    @POST(Constants.UPDATE_GIFT_CARD_STATUS)
    suspend fun updateGiftCardStatus(
        @Body body: HashMap<String,Any>
    ): Response<UpdateGiftCardResponse>

    @POST(Constants.DELETE_GIFT_CARD)
    suspend fun deleteGiftCard(
        @Body body: HashMap<String,Any>
    ): Response<CommonResponse>

    @GET(Constants.SHIPPING_CHARGE)
    suspend fun getShippingCharge(
        @Query("page") page: Int,
        @Query("limit") limit: Int? = 10,
    ): Response<GetBrandResponse>

  @GET(Constants.SHIPPING_TAX)
    suspend fun getShippingTax(
    ): Response<GetShippingTaxResponse>

  @GET(Constants.BANNER_PER_DAY_PRICE)
    suspend fun getBannerPerDay(
      @Query("type") type: Int,
      ): Response<GetBannerPricePerDayResponse>

    @POST(Constants.UPDATE_BANNER_PER_DAY_PRICE)
    suspend fun updateBannerPerDayPrice(
        @Body body: HashMap<String,Any>
    ): Response<UpdateBannerPriceResponse>

    @POST(Constants.ADD_TAX)
    suspend fun addTax(
        @Body body: HashMap<String,Any>
    ): Response<AddTaxResponse>

    @POST(Constants.ADD_SHIPPING_CHARGE)
    suspend fun addShippingCharge(
        @Body body: HashMap<String,Any>
    ): Response<UpdateShippingChargeResponse>

    @POST(Constants.UPDATE_SHIPPING_CHARGE)
    suspend fun updateShippingCharge(
        @Body body: HashMap<String,Any>
    ): Response<UpdateShippingChargeResponse>

    @POST(Constants.UPDATE_SHIPPING_CHARGE_STATUS)
    suspend fun updateShippingChargeStatus(
        @Body body: HashMap<String,Any>
    ): Response<UpdateShippingChargeResponse>

    @POST(Constants.DELETE_SHIPPING_CHARGE)
    suspend fun deleteShippingCharge(
        @Body body: HashMap<String,Any>
    ): Response<CommonResponse>

}