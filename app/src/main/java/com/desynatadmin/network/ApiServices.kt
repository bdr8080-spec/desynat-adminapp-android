package com.desynatadmin.network

import com.desynatadmin.ui.auth.LoginResponse
import com.desynatadmin.ui.home.affiliateCommission.response.GetAffiliateCommissionResponse
import com.desynatadmin.ui.home.affiliateCommission.response.UpdateAffiliateCommissionResponse
import com.desynatadmin.ui.home.affiliateUserManagement.response.AcceptAffiliateUserResponse
import com.desynatadmin.ui.home.affiliateUserManagement.response.BlockUnblockAffiliateUserResponse
import com.desynatadmin.ui.home.affiliateUserManagement.response.GetAffiliateUserDetailResponse
import com.desynatadmin.ui.home.affiliateUserManagement.response.GetNewAffiliateUserResponse
import com.desynatadmin.ui.home.affiliateWithdrawalRequest.response.GetWithdrawalRequestResponse
import com.desynatadmin.ui.home.affiliateWithdrawalRequest.response.PayoutRequest
import com.desynatadmin.ui.home.affiliateWithdrawalRequest.response.PayoutResponse
import com.desynatadmin.ui.home.banner.response.GetBannerPricePerDayResponse
import com.desynatadmin.ui.home.banner.response.UpdateBannerPriceResponse
import com.desynatadmin.ui.home.bannerRequestManagement.response.GetBannerRequestResponse
import com.desynatadmin.ui.home.bannerRequestManagement.response.RejectionResponse
import com.desynatadmin.ui.home.bannerRequestManagement.response.UpdateBannerRequestStatusResponse
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
import com.desynatadmin.ui.home.brandManagement.response.UpdateBrandStatusResponse
import com.desynatadmin.ui.home.brandManagement.response.UpdateZonResponse
import com.desynatadmin.ui.home.brandManagement.response.UpdateZoneStatusResponse
import com.desynatadmin.ui.home.brandManagement.response.ZoneListResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetCategoryResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.UpdateCategoryResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.UpdateSubCategoryResponse
import com.desynatadmin.ui.home.cmsManagement.response.GetAboutUsResponse
import com.desynatadmin.ui.home.cmsManagement.response.GetCancellationPolicyResponse
import com.desynatadmin.ui.home.cmsManagement.response.GetContactResponse
import com.desynatadmin.ui.home.cmsManagement.response.GetExchangePolicyResponse
import com.desynatadmin.ui.home.cmsManagement.response.GetFaqResponse
import com.desynatadmin.ui.home.cmsManagement.response.GetReturnPolicyResponse
import com.desynatadmin.ui.home.cmsManagement.response.GetTermsAndConditionResponse
import com.desynatadmin.ui.home.cmsManagement.response.UpdateFaqResponse
import com.desynatadmin.ui.home.dealsManagement.response.AddDealRequest
import com.desynatadmin.ui.home.dealsManagement.response.GetActiveDealResponse
import com.desynatadmin.ui.home.dealsManagement.response.GetDealDetailResponse
import com.desynatadmin.ui.home.dealsManagement.response.GetDealProductResponse
import com.desynatadmin.ui.home.dealsManagement.response.UpdateDealStatusResponse
import com.desynatadmin.ui.home.deliveryBoyManagement.response.AddDeliveryBoyRequest
import com.desynatadmin.ui.home.deliveryBoyManagement.response.BlockUnblockDeliveryBoyResponse
import com.desynatadmin.ui.home.deliveryBoyManagement.response.GetAllDeliveryBoyResponse
import com.desynatadmin.ui.home.deliveryBoyManagement.response.GetDeliveryBoyDetailResponse
import com.desynatadmin.ui.home.deliveryBoyManagement.response.GetKuwaitZoneResponse
import com.desynatadmin.ui.home.fullScreenBannerManagement.response.AddFullScreenBannerRequest
import com.desynatadmin.ui.home.fullScreenBannerManagement.response.AddFullScreenBannerResponse
import com.desynatadmin.ui.home.fullScreenBannerManagement.response.AddPromotionalBannerRequest
import com.desynatadmin.ui.home.fullScreenBannerManagement.response.AddPromotionalBannerResponse
import com.desynatadmin.ui.home.fullScreenBannerManagement.response.GetFullScreenBannerResponse
import com.desynatadmin.ui.home.fullScreenBannerManagement.response.GetPromotionalBannerResponse
import com.desynatadmin.ui.home.fullScreenBannerManagement.response.UpdatePromotionalBannerStatusResponse
import com.desynatadmin.ui.home.giftCardManagement.response.GetAllGiftCardResponse
import com.desynatadmin.ui.home.giftCardManagement.response.UpdateGiftCardResponse
import com.desynatadmin.ui.home.liveRequestAndTimeSlotManagement.response.AddTimeSlotRequest
import com.desynatadmin.ui.home.liveRequestAndTimeSlotManagement.response.GetLiveRequestResponse
import com.desynatadmin.ui.home.liveRequestAndTimeSlotManagement.response.GetTimeSlotResponse
import com.desynatadmin.ui.home.liveRequestAndTimeSlotManagement.response.UpdateTimeSlotResponse
import com.desynatadmin.ui.home.notificationManagement.response.AssignSellerResponse
import com.desynatadmin.ui.home.notificationManagement.response.AssignUserResponse
import com.desynatadmin.ui.home.notificationManagement.response.GetAssignAffiliateUserResponse
import com.desynatadmin.ui.home.notificationManagement.response.GetAssignDeliveryBoyResponse
import com.desynatadmin.ui.home.notificationManagement.response.GetSendNotificationListResponse
import com.desynatadmin.ui.home.notificationManagement.response.SendNotificationRequest
import com.desynatadmin.ui.home.notificationManagement.response.SendNotificationResponse
import com.desynatadmin.ui.home.orderManagement.response.GetActiveOrderResponse
import com.desynatadmin.ui.home.orderManagement.response.GetAvailableDeliveryBoyResponse
import com.desynatadmin.ui.home.orderManagement.response.GetSingleOrderDetailResponse
import com.desynatadmin.ui.home.productManagement.response.GetAllProductListResponse
import com.desynatadmin.ui.home.productManagement.response.GetProductDetailResponse
import com.desynatadmin.ui.home.productManagement.response.UpdateProductStatusResponse
import com.desynatadmin.ui.home.promoCodeManagement.response.AddPromoCodeRequest
import com.desynatadmin.ui.home.promoCodeManagement.response.GetPromoCodeResponse
import com.desynatadmin.ui.home.promoCodeManagement.response.UpdatePromoCodeResponse
import com.desynatadmin.ui.home.reasonManagement.response.BlockUnblockReasonResponse
import com.desynatadmin.ui.home.reasonManagement.response.GetReasonResponse
import com.desynatadmin.ui.home.reasonManagement.response.UpdateReasonResponse
import com.desynatadmin.ui.home.response.CommonResponse
import com.desynatadmin.ui.home.shippingAndTaxManagement.response.AddTaxResponse
import com.desynatadmin.ui.home.shippingAndTaxManagement.response.GetDefaultShippingChargeResponse
import com.desynatadmin.ui.home.shippingAndTaxManagement.response.GetShippingChargeResponse
import com.desynatadmin.ui.home.shippingAndTaxManagement.response.GetShippingTaxResponse
import com.desynatadmin.ui.home.shippingAndTaxManagement.response.UpdateDefaultShippingChargeResponse
import com.desynatadmin.ui.home.shippingAndTaxManagement.response.UpdateShippingChargeResponse
import com.desynatadmin.ui.home.storyManagement.response.GetStoryResponse
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
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface ApiServices {

    @Multipart
    @POST(Constants.UPLOAD)
    suspend fun uploadImage(
        @Part file: MultipartBody.Part? = null
    ): Response<FileUploadResponse>

  @Multipart
    @POST(Constants.UPLOAD_VIDEO_IMAGE)
    suspend fun uploadVideoImage(
        @Part file: MultipartBody.Part? = null
    ): Response<FileUploadResponse>


  @Multipart
    @POST(Constants.UPLOAD_WITH_RESOLUTION)
    suspend fun uploadWithResolution(
        @Part file: MultipartBody.Part? = null,
        @Part("type") type: RequestBody? = null
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
        @QueryMap body: HashMap<String,Any>): Response<GetBrandResponse>

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
        @Header("Token") Token: String?=null,
        @Query("country_id1") country_id: String?=null,
        @Query("from_date1") from_date: String?=null,
        @Query("to_date1") to_date: String?=null,
        @Query("limit1") limit1: String?=null,
        @Query("limit") limit: String?=null,
        @Query("search1") search: String?=null,
        @Query("page") page: String?=null,
    ): Response<ApprovedSellerResponse>

    @GET(Constants.APPROVED_SELLER)
    suspend fun assignSellerList(
    ): Response<AssignSellerResponse>

    @GET(Constants.REJECTED_SELLER)
    suspend fun hitRejectedSeller(
        @Header("Token") Token: String,
        @Query("country_id2") country_id: String,
        @Query("from_date2") from_date: String,
        @Query("to_date2") to_date: String,
        @Query("limit2") limit: String,
        @Query("search2") search: String,
        @Query("page") page: String,
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
        @Query("page") page: String?=null,
        @Query("country_id") countryId : String?=null,
        @Query("from_date") fromDate : String?=null,
        @Query("to_date") toDate : String?=null,
        @Query("limit") limit: Int? = 10,

        ): Response<GetUserResponse>

    @GET(Constants.GET_USER)
    suspend fun getAssignUser(

        ): Response<AssignUserResponse>

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
        @Query("page") page: String?=null,
        @Query("from_date") fromDate : String?=null,
        @Query("to_date") toDate : String?=null,
        @Query("limit") limit: Int? = 10, ): Response<GetAllDeliveryBoyResponse>

    @GET(Constants.GET_DELIVERY_BOY)
    suspend fun getAssignDeliveryBoy(): Response<GetAssignDeliveryBoyResponse>

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
        @Query("country_id") countryId: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int? = 10,
    ): Response<GetShippingChargeResponse>

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

    @GET(Constants.BANNER_REQUEST)
    suspend fun getBannerRequest(
        @Query("type") type: Int,
        @Query("search") search : String?=null,
        @Query("from_date") fromDate : String?=null,
        @Query("to_date") toDate : String?=null,
        @Query("country_name") countryId : String?=null,
          @Query("page") page: Int,
          @Query("limit") limit: Int? = 10,
    ): Response<GetBannerRequestResponse>

    @POST(Constants.UPDATE_BANNER_STATUS)
    suspend fun updateBannerStatus(
        @Body body: HashMap<String,Any>
    ): Response<UpdateBannerRequestStatusResponse>

@POST(Constants.APPROVE_BANNER_REQUEST)
    suspend fun approveBannerRequest(
        @Body body: HashMap<String,Any>
    ): Response<CommonResponse>


    @POST(Constants.REJECT_BANNER_REQUEST)
    suspend fun rejectBannerRequest(
        @Body body: HashMap<String,Any>
    ): Response<RejectionResponse>


    @GET(Constants.GET_AFFILIATE_COMMISSION)
    suspend fun getAffiliatedCommission(): Response<GetAffiliateCommissionResponse>

@POST(Constants.UPDATE_AFFILIATE_COMMISSION)
    suspend fun updateAffiliateCommission(
        @Body body: HashMap<String,Any>
    ): Response<UpdateAffiliateCommissionResponse>

    @GET(Constants.ACTIVE_ORDER_LIST)
    suspend fun getActiveOrderList(
        @QueryMap map: HashMap<String,Any>
    ): Response<GetActiveOrderResponse>

    @GET(Constants.ORDER_DETAIL)
    suspend fun getOrderDetail(
        @QueryMap map: HashMap<String,Any>
    ): Response<GetSingleOrderDetailResponse>

    @GET(Constants.COMPLETED_ORDER_LIST)
    suspend fun getCompletedOrderList(
        @QueryMap map: HashMap<String,Any>
    ): Response<GetActiveOrderResponse>

    @GET(Constants.CANCELLED_ORDER_LIST)
    suspend fun getCancelledOrderList(
        @QueryMap map: HashMap<String,Any>
    ): Response<GetActiveOrderResponse>

    @GET(Constants.RETURN_ORDER_LIST)
    suspend fun getReturnOrderList(
        @QueryMap map: HashMap<String,Any>
    ): Response<GetActiveOrderResponse>

    @GET(Constants.GET_DRIVER_TO_ASSIGN)
    suspend fun getDriverToAssign(
        @Query("order_id") orderId: String
        ): Response<GetAvailableDeliveryBoyResponse>

    @POST(Constants.ASSIGN_DRIVER)
    suspend fun assignDriver(
        @Body body: HashMap<String,Any>
    ): Response<CommonResponse>

    @GET("${Constants.INVOICE}/{order_id}")
    suspend fun getInvoice( @Path("order_id") order_id: String): Response<CommonResponse>

    @GET(Constants.GET_ACTIVE_DEALS)
    suspend fun getActiveDeals(
        @Query("search") search : String?=null,
        @Query("page") page: Int,
        @Query("limit") limit: Int? = 10,
    ): Response<GetActiveDealResponse>

    @GET(Constants.GET_EXPIRED_DEALS)
    suspend fun getExpiredDeals(
        @Query("search") search : String?=null,
        @Query("page") page: Int,
        @Query("limit") limit: Int? = 10,
    ): Response<GetActiveDealResponse>

    @POST(Constants.ADD_DEALS)
    suspend fun addDeal(
        @Body body: AddDealRequest
    ): Response<CommonResponse>

    @POST(Constants.UPDATE_DEALS)
    suspend fun updateDeal(
        @Body body: AddDealRequest
    ): Response<UpdateDealStatusResponse>

    @POST(Constants.UPDATE_DEAL_STATUS)
    suspend fun updateDealStatus(
        @Body body: HashMap<String,Any>
    ): Response<UpdateDealStatusResponse>

    @POST(Constants.DELETE_DEAL)
    suspend fun deleteDeal(
        @Body body: HashMap<String,Any>
    ): Response<CommonResponse>

    @GET(Constants.GET_DEAL_DETAIL)
    suspend fun getDealDetail(
        @QueryMap body: HashMap<String,Any>
    ): Response<GetDealDetailResponse>

    @GET(Constants.DEAL_PRODUCT_LIST)
    suspend fun getDealProduct(
        @QueryMap body: HashMap<String,Any>
    ): Response<GetDealProductResponse>

    @GET(Constants.GET_FULL_BANNER)
    suspend fun getFullScreenBanner(
        @Query("search") search : String?=null,
        @Query("country") country : String?=null,
        @Query("type") type : String?=null,
        @Query("from_date") fromDate : String?=null,
        @Query("to_date") toDate : String?=null,
        @Query("page") page: Int,
        @Query("limit") limit: Int? = 10,
    ): Response<GetFullScreenBannerResponse>

    @POST(Constants.ADD_FULL_BANNER)
    suspend fun addFullScreenBanner(
        @Body body: AddFullScreenBannerRequest
    ): Response<AddFullScreenBannerResponse>

    @POST(Constants.UPDATE_FULL_BANNER)
    suspend fun updateFullScreenBanner(
        @Body body: AddFullScreenBannerRequest
    ): Response<AddFullScreenBannerResponse>

    @POST(Constants.DELETE_FULL_BANNER)
    suspend fun deleteFullScreenBanner(
        @Body body: HashMap<String,Any>
    ): Response<CommonResponse>

    @GET(Constants.GET_ADMIN_STORY)
    suspend fun getAdminStory(
        @Query("page") page: Int,
        @Query("limit") limit: Int? = 10,
    ): Response<GetStoryResponse>

    @GET(Constants.GET_SELLER_STORY)
    suspend fun getSellerStory(
        @Query("page") page: Int,
        @Query("limit") limit: Int? = 10,
    ): Response<GetStoryResponse>

    @POST(Constants.ADD_STORY)
    suspend fun addStory(
        @Body body: HashMap<String,Any>
    ): Response<CommonResponse>

    @POST(Constants.DELETE_STORY)
    suspend fun deleteStory(
        @Body body: HashMap<String,Any>
    ): Response<CommonResponse>


    @GET(Constants.GET_LIVE_REQUEST)
    suspend fun getLiveRequest(
        @QueryMap map: HashMap<String,Any>
    ): Response<GetLiveRequestResponse>

    @POST(Constants.ACCEPT_LIVE_REQUEST)
    suspend fun acceptLiveRequest(
        @Body body: HashMap<String,Any>
    ): Response<CommonResponse>

    @POST(Constants.REJECT_LIVE_REQUEST)
    suspend fun rejectLiveRequest(
        @Body body: HashMap<String,Any>
    ): Response<CommonResponse>

    @GET(Constants.GET_TIME_SLOT)
    suspend fun getTimeSlot(
        @Query("country_id") countryId: String,
        @Query("date") date: String
    ): Response<GetTimeSlotResponse>

    @POST(Constants.ADD_TIME_SLOT)
    suspend fun addTimeSlot(
        @Body body: AddTimeSlotRequest
    ): Response<UpdateTimeSlotResponse>

    @POST(Constants.UPDATE_TIME_SLOT)
    suspend fun updateTimeSlot(
        @Body body: AddTimeSlotRequest
    ): Response<UpdateTimeSlotResponse>

    @POST(Constants.DELETE_TIME_SLOT)
    suspend fun deleteTimeSlot(
        @Body body: HashMap<String,Any>
    ): Response<CommonResponse>

    @POST(Constants.UPDATE_TIME_SLOT_STATUS)
    suspend fun updateTimeSlotStatus(
        @Body body: HashMap<String,Any>
    ): Response<UpdateTimeSlotResponse>

    @GET(Constants.GET_NEW_AFFILIATE_USER)
    suspend fun getNewAffiliateUser(
        @QueryMap map: HashMap<String,Any>): Response<GetNewAffiliateUserResponse>

    @GET(Constants.GET_APPROVED_AFFILIATE_USER)
    suspend fun getApprovedAffiliateUser(
        @QueryMap map: HashMap<String,Any>): Response<GetNewAffiliateUserResponse>

    @GET(Constants.GET_APPROVED_AFFILIATE_USER)
    suspend fun getAssignAffiliateUser(
        @QueryMap map: HashMap<String,Any>): Response<GetAssignAffiliateUserResponse>

    @GET(Constants.GET_REJECTED_AFFILIATE_USER)
    suspend fun getRejectedAffiliateUser(
        @QueryMap map: HashMap<String,Any>): Response<GetNewAffiliateUserResponse>

    @GET(Constants.GET_AFFILIATE_USER_DETAIL)
    suspend fun getAffiliateUserDetail(
        @Query("user_id") userId : String,
    ): Response<GetAffiliateUserDetailResponse>

    @POST(Constants.BLOCK_UNBLOCK_AFFILIATE_USER)
    suspend fun blockUnblockAffiliateUser(
        @Body body: HashMap<String, Any>
    ): Response<BlockUnblockAffiliateUserResponse>

    @POST(Constants.ACCEPT_AFFILIATE_USER)
    suspend fun acceptAffiliateUser(
        @Body body: HashMap<String, Any>
    ): Response<AcceptAffiliateUserResponse>

    @POST(Constants.REJECT_AFFILIATE_USER)
    suspend fun rejectAffiliateUser(
        @Body body: HashMap<String, Any>
    ): Response<AcceptAffiliateUserResponse>

    @GET(Constants.GET_PROMOTIONAL_BANNER)
    suspend fun getPromotionalBanner(
        @QueryMap map: HashMap<String,Any>
    ): Response<GetPromotionalBannerResponse>

    @POST(Constants.ADD_PROMOTIONAL_BANNER)
    suspend fun addPromotionalBanner(
        @Body body: AddPromotionalBannerRequest
    ): Response<AddPromotionalBannerResponse>

    @POST(Constants.UPDATE_PROMOTIONAL_BANNER)
    suspend fun updatePromotionalBanner(
        @Body body: AddPromotionalBannerRequest
    ): Response<AddPromotionalBannerResponse>
    @POST(Constants.DELETE_PROMOTIONAL_BANNER)
    suspend fun deletePromotionalBanner(
        @Body body: HashMap<String,Any>
    ): Response<CommonResponse>

    @POST(Constants.UPDATE_PROMOTIONAL_BANNER_STATUS)
    suspend fun updatePromotionalBannerStatus(
        @Body body: HashMap<String,Any>
    ): Response<UpdatePromotionalBannerStatusResponse>

    @POST(Constants.UPDATE_DEFAULT_SHIPPING_FEE)
    suspend fun updateDefaultShippingFee(
        @Body body: HashMap<String,Any>
    ): Response<UpdateDefaultShippingChargeResponse>

    @GET(Constants.GET_DEFAULT_SHIPPING_FEE)
    suspend fun getDefaultShippingFee(
        @Query("country_id") countryId: String,
    ): Response<GetDefaultShippingChargeResponse>

    @GET(Constants.GET_ALL_PRODUCT)
    suspend fun getAllProduct(
        @QueryMap(encoded = true) map: HashMap<String,Any>
    ): Response<GetAllProductListResponse>

    @GET(Constants.PRODUCT_DETAIL)
    suspend fun getProductDetail(
        @QueryMap map: HashMap<String,Any>
    ): Response<GetProductDetailResponse>

    @POST(Constants.UPDATE_PRODUCT_STATUS)
    suspend fun updateProductStatus(
        @Body body: HashMap<String,Any>
    ): Response<UpdateProductStatusResponse>

    @GET(Constants.GET_WITHDRAWAL_REQUEST)
    suspend fun getWithdrawalRequest(
        @QueryMap map: HashMap<String,Any>
    ): Response<GetWithdrawalRequestResponse>

    @POST(Constants.ACCEPT_WITHDRAWAL_REQUEST)
    suspend fun acceptWithdrawalRequest(
        @Body body: HashMap<String,Any>
    ): Response<CommonResponse>


    @POST(Constants.REJECT_WITHDRAWAL_REQUEST)
    suspend fun rejectWithdrawalRequest(
        @Body body: HashMap<String,Any>
    ): Response<CommonResponse>

    @POST(Constants.WITHDRAWAL_TRANSFER)
    suspend fun WithdrawalTransfer(
        @Body body: HashMap<String,Any>
    ): Response<CommonResponse>

    @POST
    suspend fun sendTransfer(
        @Header("Authorization") token: String,
        @Url url: String,
        @Body request: PayoutRequest
    ): Response<PayoutResponse>

    @GET(Constants.GET_TERMS_AND_CONDITION)
    suspend fun getTermsAndCondition(
        @QueryMap map: HashMap<String,Any>
    ): Response<GetTermsAndConditionResponse>

    @POST(Constants.CREATE_UPDATE_TERMS_AND_CONDITION)
    suspend fun createUpdateTermsAndCondition(
        @Body map: HashMap<String,Any>
    ): Response<GetTermsAndConditionResponse>

    @GET(Constants.GET_ABOUT_US)
    suspend fun getAboutUs(
        @QueryMap map: HashMap<String,Any>
    ): Response<GetAboutUsResponse>

    @POST(Constants.CREATE_UPDATE_ABOUT_US)
    suspend fun createUpdateAboutUs(
        @Body map: HashMap<String,Any>
    ): Response<GetAboutUsResponse>

    @GET(Constants.GET_RETURN_POLICY)
    suspend fun getReturnPolicy(
        @QueryMap map: HashMap<String,Any>
    ): Response<GetReturnPolicyResponse>

    @POST(Constants.CREATE_UPDATE_RETURN_POLICY)
    suspend fun createUpdateReturnPolicy(
        @Body map: HashMap<String,Any>
    ): Response<GetReturnPolicyResponse>

    @GET(Constants.GET_CANCELLATION_POLICY)
    suspend fun getCancellationPolicy(
        @QueryMap map: HashMap<String,Any>
    ): Response<GetCancellationPolicyResponse>

    @POST(Constants.CREATE_UPDATE_CANCELLATION_POLICY)
    suspend fun createUpdateCancellationPolicy(
        @Body map: HashMap<String,Any>
    ): Response<GetCancellationPolicyResponse>

    @GET(Constants.GET_EXCHANGE_POLICY)
    suspend fun getExchangePolicy(
        @QueryMap map: HashMap<String,Any>
    ): Response<GetExchangePolicyResponse>

    @POST(Constants.CREATE_UPDATE_EXCHANGE_POLICY)
    suspend fun createUpdateExchangePolicy(
        @Body map: HashMap<String,Any>
    ): Response<GetExchangePolicyResponse>

    @GET(Constants.GET_FAQ)
    suspend fun getFaq(
        @QueryMap map: HashMap<String,Any>
    ): Response<GetFaqResponse>

    @POST(Constants.ADD_FAQ)
    suspend fun addFaq(
        @Body map: HashMap<String,Any>
    ): Response<UpdateFaqResponse>

    @POST(Constants.UPDATE_FAQ)
    suspend fun updateFaq(
        @Body map: HashMap<String,Any>
    ): Response<UpdateFaqResponse>

    @POST(Constants.DELETE_FAQ)
    suspend fun deleteFaq(
        @Body map: HashMap<String,Any>
    ): Response<CommonResponse>

    @GET(Constants.GET_CONTACT_US)
    suspend fun getContactUs(
        @QueryMap map: HashMap<String,Any>
    ): Response<GetContactResponse>

    @POST(Constants.CREATE_UPDATE_CONTACT_US)
    suspend fun createUpdateContactUs(
        @Body map: HashMap<String,Any>
    ): Response<CommonResponse>

    @GET(Constants.GET_MY_NOTIFICATION_LIST)
    suspend fun getMyNotification(
        @QueryMap map: HashMap<String,Any>
    ): Response<CommonResponse>

    @POST(Constants.READ_NOTIFICATION)
    suspend fun readNotification(
        @Body map: HashMap<String,Any>
    ): Response<CommonResponse>

  @POST(Constants.MARK_ALL_READ_NOTIFICATION)
    suspend fun allReadNotification(
        @Body map: HashMap<String,Any>
    ): Response<CommonResponse>

    @GET(Constants.GET_SEND_NOTIFICATION_LIST)
    suspend fun getSendNotification(
        @QueryMap map: HashMap<String,Any>
    ): Response<GetSendNotificationListResponse>


    @POST(Constants.SEND_NOTIFICATION)
    suspend fun sendNotification(
        @Body request: SendNotificationRequest
    ): Response<SendNotificationResponse>


}