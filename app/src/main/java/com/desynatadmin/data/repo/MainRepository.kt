package com.desynatadmin.data.repo


import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.desynatadmin.network.ApiServices
import com.desynatadmin.ui.auth.LoginResponse
import com.desynatadmin.ui.home.affiliateCommission.response.GetAffiliateCommissionResponse
import com.desynatadmin.ui.home.affiliateCommission.response.UpdateAffiliateCommissionResponse
import com.desynatadmin.ui.home.affiliateUserManagement.response.AcceptAffiliateUserResponse
import com.desynatadmin.ui.home.affiliateUserManagement.response.BlockUnblockAffiliateUserResponse
import com.desynatadmin.ui.home.affiliateUserManagement.response.GetAffiliateUserDetailResponse
import com.desynatadmin.ui.home.affiliateUserManagement.response.GetNewAffiliateUserResponse
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
import com.desynatadmin.utils.toMultipartPart
import com.desynatadmin.utils.toPlainRequestBody
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val api: ApiServices
) : BaseRepository(context) {


    val responseUploadImage = SingleLiveEvent<FileUploadResponse>()
    suspend fun uploadImage(
        imageFile: File? = null
    ) {
        handleApiCall(
            api = {
                api.uploadImage(imageFile?.toMultipartPart("image")
                )
            },
            responseLiveData = responseUploadImage
        )
    }

    val responseUploadVideoImage = SingleLiveEvent<FileUploadResponse>()
    suspend fun uploadVideoImage(
        imageFile: File? = null
    ) {
        handleApiCall(
            api = {
                api.uploadVideoImage(imageFile?.toMultipartPart("file")
                )
            },
            responseLiveData = responseUploadVideoImage
        )
    }

    val responseUploadWithResolution = SingleLiveEvent<FileUploadResponse>()
    suspend fun uploadWithResolution(
        imageFile: File? = null,type:String
    ) {
        handleApiCall(
            api = {
                api.uploadWithResolution(imageFile?.toMultipartPart("image"),type.toPlainRequestBody()
                )
            },
            responseLiveData = responseUploadWithResolution
        )
    }

    val loginLiveData = SingleLiveEvent<LoginResponse>()
    suspend fun login(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.login(map)
            },
            responseLiveData = loginLiveData
        )
    }

    val otpVerifyLiveData = SingleLiveEvent<LoginResponse>()
    suspend fun otpVerification(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.otpVerification(map)
            },
            responseLiveData = otpVerifyLiveData
        )
    }

    val resendOtpLiveData = SingleLiveEvent<LoginResponse>()
    suspend fun resendOtp(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.resendOtp(map)
            },
            responseLiveData = resendOtpLiveData
        )
    }

    val forgotPasswordLiveData = SingleLiveEvent<LoginResponse>()
    suspend fun forgotPassword(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.forgotPassword(map)
            },
            responseLiveData = forgotPasswordLiveData
        )
    }

    val createPasswordLiveData = SingleLiveEvent<LoginResponse>()
    suspend fun createPassword(
        map: HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.createPassword(map)
            },
            responseLiveData = createPasswordLiveData
        )
    }

val logoutLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun logout(
    ) {
        handleApiCall(
            api = {
                api.logout()
            },
            responseLiveData = logoutLiveData
        )
    }

val categoryLiveData = SingleLiveEvent<GetCategoryResponse>()
    suspend fun getCategory(
    ) {
        handleApiCall(
            api = {
                api.getCategory()
            },
            responseLiveData = categoryLiveData
        )
    }
val subCategoryLiveData = SingleLiveEvent<GetSubCategoryResponse>()
    suspend fun getSubCategory(categoryId:String,search:String?=null, page:Int
    ) {
        handleApiCall(
            api = {
                api.getSubCategory(categoryId,search,page)
            },
            responseLiveData = subCategoryLiveData
        )
    }

val brandLiveData = SingleLiveEvent<GetBrandResponse>()
    suspend fun getBrand(map:HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.getBrand(map)
            },
            responseLiveData = brandLiveData
        )
    }

    val addBrandLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun addBrand(map:HashMap<String, RequestBody>
    ) {
        handleApiCall(
            api = {
                api.addBrand(map)
            },
            responseLiveData = addBrandLiveData
        )
    }

    val editBrandLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun editBrand(map:HashMap<String, RequestBody>
    ) {
        handleApiCall(
            api = {
                api.editBrand(map)
            },
            responseLiveData = editBrandLiveData
        )
    }

    val addSubCategoryLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun addSubCategory(map:HashMap<String, RequestBody>
    ) {
        handleApiCall(
            api = {
                api.addSubCategory(map)
            },
            responseLiveData = addSubCategoryLiveData
        )
    }

    val updateSubCategoryLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun editSubCategory(
        map: HashMap<String, RequestBody>
    ) {
        handleApiCall(
            api = {
                api.editSubCategory(map)
            },
            responseLiveData = updateSubCategoryLiveData
        )
    }

    val deleteBrandLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun deleteBrand(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.deleteBrand(map)
            },
            responseLiveData = deleteBrandLiveData
        )
    }

    val deleteSubCategoryLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun deleteSubCategory(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.deleteSubCategory(map)
            },
            responseLiveData = deleteSubCategoryLiveData
        )

    }

    val getServiceListData = SingleLiveEvent<ServiceCityListResponse>()
    suspend fun hitServiceCityList(
        token: String,
        country_id: String,
        limit: String,
        search: String,
        page : String

    ) {
        handleApiCall(
            api = {
                api.hitServiceCityList(
                    token,
                    country_id,
                    limit,
                    search,
                    page
                )
            },
            responseLiveData = getServiceListData
        )

    }


    val getCityData = SingleLiveEvent<CityResponse>()
    suspend fun hitCityList(
        token: String, id: String
    ) {
        handleApiCall(
            api = {
                api.getCity(token, id)
            },
            responseLiveData = getCityData
        )

    }


    val getAddServiceCity = SingleLiveEvent<AddServiceCityResponse>()
    suspend fun hitAddServiceCity(
        token: String, map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.hitAddServiceCity(token, map)
            },
            responseLiveData = getAddServiceCity
        )

    }


    val updateBrandStatusLiveData = SingleLiveEvent<UpdateBrandStatusResponse>()
    suspend fun updateBrandStatus(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.updateBrandStatus(map)
            },
            responseLiveData = updateBrandStatusLiveData
        )
    }

    val updateSubCategoryStatusLiveData = SingleLiveEvent<UpdateSubCategoryResponse>()
    suspend fun updateSubCategoryStatus(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.updateSubCategoryStatus(map)
            },
            responseLiveData = updateSubCategoryStatusLiveData
        )

    }

    val updateCategoryStatusLiveData = SingleLiveEvent<UpdateCategoryResponse>()
    suspend fun updateCategoryStatus(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.updateCategoryStatus(map)
            },
            responseLiveData = updateCategoryStatusLiveData
        )

    }


    val subscriptionPlanLiveData = SingleLiveEvent<GetAllPlanResponse>()
    suspend fun getSubscriptionPlan(countryId:String?=null
    ) {
        handleApiCall(
            api = {
                api.getSubscriptionPlan(countryId)
            },
            responseLiveData = subscriptionPlanLiveData
        )
    }

 val subscriptionPlanDetailLiveData = SingleLiveEvent<UpdatePlanStatusResponse>()
    suspend fun getSubscriptionPlanDetail(planId:String?=null
    ) {
        handleApiCall(
            api = {
                api.getSubscriptionPlanDetail(planId)
            },
            responseLiveData = subscriptionPlanDetailLiveData
        )
    }

    val addSubscriptionPlanLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun addSubscriptionPlan(request: AddPlanRequest
    ) {
        handleApiCall(
            api = {
                api.addSubscriptionPlan(request)
            },
            responseLiveData = addSubscriptionPlanLiveData
        )
    }

    val editSubscriptionPlanLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun updateSubscriptionPlan(request: AddPlanRequest
    ) {
        handleApiCall(
            api = {
                api.updateSubscriptionPlan(request)
            },
            responseLiveData = editSubscriptionPlanLiveData
        )
    }

    val updateSubscriptionPlanStatusLiveData = SingleLiveEvent<UpdatePlanStatusResponse>()
    suspend fun updateSubscriptionPlanStatus(map:HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.updateSubscriptionPlanStatus(map)
            },
            responseLiveData = updateSubscriptionPlanStatusLiveData
        )
    }

    val deleteSubscriptionPlanLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun deleteSubscriptionPlan(map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.deleteSubscriptionPlan(map)
            },
            responseLiveData = deleteSubscriptionPlanLiveData
        )
    }

    val countryPlanLiveData = SingleLiveEvent<GetCountryResponse>()
    suspend fun getCountry(
    ) {
        handleApiCall(
            api = {
                api.getCountry()
            },
            responseLiveData = countryPlanLiveData
        )
    }


    val getChangeStatus = SingleLiveEvent<ChangeCityStatusResponse>()
    suspend fun hitChangeCityStatus(
        token : String,map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.hitChangeStatus(token,map)
            },
            responseLiveData = getChangeStatus
        )
    }

    val getDeleteServiceCity = SingleLiveEvent<CommonResponse>()
    suspend fun hitDeleteCityService(
        token : String,map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.hitDeleteServiceCity(token,map)
            },
            responseLiveData = getDeleteServiceCity
        )
    }

    val getAddZoneData = SingleLiveEvent<AddZoneResponse>()
    suspend fun hitAddZone(
        token : String,map:AddZoneRequest
    ) {
        handleApiCall(
            api = {
                api.hitAddZone(token,map)
            },
            responseLiveData = getAddZoneData
        )
    }

    val getNewSellerList = SingleLiveEvent<NewSellerResponse>()
    suspend fun hitNewSellerList(
        token : String,
        country_id : String,
        from_date : String,
        to_date : String,
        limit : String,
        page : String,
        search : String,
    ) {
        handleApiCall(
            api = {
                api.hitNewSelleList(token,country_id,from_date, to_date,limit,page,search)
            },
            responseLiveData = getNewSellerList
        )
    }

    val getApprovedData = SingleLiveEvent<ApprovedSellerResponse>()
    suspend fun hitApprovedSellerList(
        token : String,
        country_id : String,
        from_date : String,
        to_date : String,
        limit : String,
        search : String,
    ) {
        handleApiCall(
            api = { api.hitApprovedSeller(token,country_id,from_date, to_date,limit,search) },
            responseLiveData = getApprovedData
        )
    }

    val getRejectSellerList = SingleLiveEvent<RejectedListResponse>()
    suspend fun hitRejectedSellerList(
        token : String,
        country_id : String,
        from_date : String,
        to_date : String,
        limit : String,
        search : String,
    ) {
        handleApiCall(
            api = {
                api.hitRejectedSeller(token,country_id,from_date, to_date,limit,search)
            },
            responseLiveData = getRejectSellerList
        )
    }


    val getSellerDetail = SingleLiveEvent<SellerDetailResponse>()
    suspend fun hitSellerDetail(
        token : String,
        seller_id : String,
    ) {
        handleApiCall(
            api = {
                api.hitSellerDetail(token,seller_id)
            },
            responseLiveData = getSellerDetail
        )
    }

    val getAcceptSeller = SingleLiveEvent<AcceptSellerResponse>()
    suspend fun hitAcceptSeller(
        token : String,
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.hitAcceptSeller(token,map)
            },
            responseLiveData = getAcceptSeller
        )
    }

    val getRejectSeller = SingleLiveEvent<AcceptSellerResponse>()
    suspend fun hitRejectSeller(
        token : String,
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.hitRejectSeller(token,map)
            },
            responseLiveData = getRejectSeller
        )
    }

    val getDeleteZone = SingleLiveEvent<AcceptSellerResponse>()
    suspend fun hitDeleteZone(
        token : String,
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.hitDeleteZone(token,map)
            },
            responseLiveData = getDeleteZone
        )
    }

    val getUpdateSellerData = SingleLiveEvent<SellerStatusResponse>()
    suspend fun hitUpdateSeller(
        token : String,
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.hitSellerStatus(token,map)
            },
            responseLiveData = getUpdateSellerData
        )
    }

    val getLocation = SingleLiveEvent<PlacesResponse>()
    suspend fun hitPlaceResponse(
        input:String,key:String
    ) {
        handleApiCall(
            api = {
                api.getPlaceSuggestions(input = input,apiKey=key,"en")
            },
            responseLiveData = getLocation
        )
    }

    val getZoneList = SingleLiveEvent<ZoneListResponse>()
    suspend fun hitZoneList(
        token : String,
        country_id : String,
        city_id : String,
    ) {
        handleApiCall(
            api = {
                api.hitZoneList(token,country_id, city_id)
            },
            responseLiveData = getZoneList
        )
    }

    val getUpdateZoneData = SingleLiveEvent<UpdateZonResponse>()
    suspend fun hitUpdateZone(
        token : String,
       req : AddZoneRequest
    ) {
        handleApiCall(
            api = {
                api.hitUpdateZone(token,req)
            },
            responseLiveData = getUpdateZoneData
        )
    }
    val getUpdateZoneStatusData = SingleLiveEvent<UpdateZoneStatusResponse>()
    suspend fun hitUpdateZoneStatus(
        token : String,
       map : HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.hitUpdateZoneStatus(token,map)
            },
            responseLiveData = getUpdateZoneStatusData
        )
    }

    val filterNewSeller = SingleLiveEvent<UpdateZonResponse>()
    suspend fun hitNewSeller(
        token : String,
        country_id: String,from_date: String,to_date: String
    ) {
        handleApiCall(
            api = {
                api.filter(Token =token,country_id = country_id, from_date = from_date, to_date = to_date)
            },
            responseLiveData = filterNewSeller
        )
    }

    val fetchLocation = SingleLiveEvent<PlaceDetailsResponse>()
    suspend fun hitPlaceDetail(
        placeId:String,key:String
    ) {
        handleApiCall(
            api = {
                api.getPlaceDetails(
                    placeId = placeId,
                    apiKey = key
                )
            },
            responseLiveData = fetchLocation
        )
    }


    val userLiveData = SingleLiveEvent<GetUserResponse>()
    suspend fun getAllUser(search:String?=null, page:Int,countryId:String?=null,fromDate:String?=null,toDate:String?=null
    ) {
        handleApiCall(
            api = {
                api.getUser(search,page,countryId,fromDate,toDate)
            },
            responseLiveData = userLiveData
        )
    }

    val userDetailLiveData = SingleLiveEvent<GetUserDetailResponse>()
    suspend fun getUserDetail(userId:String
    ) {
        handleApiCall(
            api = {
                api.getUserDetail(userId)
            },
            responseLiveData = userDetailLiveData
        )
    }

    val updateUserStatusLiveData = SingleLiveEvent<BlockUnblockUserResponse>()
    suspend fun blockUnblockUser(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.blockUnblockUser(map)
            },
            responseLiveData = updateUserStatusLiveData
        )
    }

    val walletBalanceLiveData = SingleLiveEvent<AddWalletBalanceResponse>()
    suspend fun addWalletAmount(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.addWalletAmount(map)
            },
            responseLiveData = walletBalanceLiveData
        )
    }

    val reasonLiveData = SingleLiveEvent<GetReasonResponse>()
    suspend fun getReason(type:Int,search:String?=null, page:Int
    ) {
        handleApiCall(
            api = {
                api.getReason(type,search,page)
            },
            responseLiveData = reasonLiveData
        )
    }

    val addReasonLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun addReason(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.addReason(map)
            },
            responseLiveData = addReasonLiveData
        )
    }

    val updateReasonLiveData = SingleLiveEvent<UpdateReasonResponse>()
    suspend fun updateReason(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.updateReason(map)
            },
            responseLiveData = updateReasonLiveData
        )
    }

 val kuwaitLiveData = SingleLiveEvent<GetKuwaitZoneResponse>()
    suspend fun getKuwaitZone(
    ) {
        handleApiCall(
            api = {
                api.getKuwaitZone()
            },
            responseLiveData = kuwaitLiveData
        )
    }

    val userCountryLiveData = SingleLiveEvent<GetCountryResponse>()
    suspend fun getUserCountry(
    ) {
        handleApiCall(
            api = {
                api.getUserCountry()
            },
            responseLiveData = userCountryLiveData
        )
    }

    val updateReasonStatusLiveData = SingleLiveEvent<BlockUnblockReasonResponse>()
    suspend fun blockUnblockReason(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.blockUnblockReason(map)
            },
            responseLiveData = updateReasonStatusLiveData
        )
    }

    val deleteReasonLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun deleteReason(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.deleteReason(map)
            },
            responseLiveData = deleteReasonLiveData
        )
    }

    val deliveryBoyLiveData = SingleLiveEvent<GetAllDeliveryBoyResponse>()
    suspend fun getDeliveryBoy(search:String?=null, page:Int,fromDate:String?=null,toDate:String?=null
    ) {
        handleApiCall(
            api = {
                api.getDeliveryBoy(search,page,fromDate,toDate)
            },
            responseLiveData = deliveryBoyLiveData
        )
    }

    val deliveryBoyDetailLiveData = SingleLiveEvent<GetDeliveryBoyDetailResponse>()
    suspend fun getDeliveryBoyDetail(userId:String
    ) {
        handleApiCall(
            api = {
                api.getDeliveryBoyDetail(userId)
            },
            responseLiveData = deliveryBoyDetailLiveData
        )
    }

    val addDeliveryBoyLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun addDeliveryBoy(request: AddDeliveryBoyRequest
    ) {
        handleApiCall(
            api = {
                api.addDeliveryBoy(request)
            },
            responseLiveData = addDeliveryBoyLiveData
        )
    }

    val updateDeliveryBoyLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun updateDeliveryBoy(request: AddDeliveryBoyRequest
    ) {
        handleApiCall(
            api = {
                api.updateDeliveryBoy(request)
            },
            responseLiveData = updateDeliveryBoyLiveData
        )
    }

    val updateDeliveryBoyStatusLiveData = SingleLiveEvent<BlockUnblockDeliveryBoyResponse>()
    suspend fun blockUnblockDeliveryBoy(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.blockUnblockDeliveryBoy(map)
            },
            responseLiveData = updateDeliveryBoyStatusLiveData
        )
    }

    val deleteDeliveryBoyLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun deleteDeliveryBoy(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.deleteDeliveryBoy(map)
            },
            responseLiveData = deleteDeliveryBoyLiveData
        )
    }


    val promoCodeLiveData = SingleLiveEvent<GetPromoCodeResponse>()
    suspend fun getPromoCode(search:String?=null,fromDate:String?=null,toDate:String?=null, countryId :String?=null
    ) {
        handleApiCall(
            api = {
                api.getPromoCode(search,fromDate,toDate,countryId)
            },
            responseLiveData = promoCodeLiveData
        )
    }

    val addPromoCodeLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun addPromoCode(request: AddPromoCodeRequest
    ) {
        handleApiCall(
            api = {
                api.addPromoCode(request)
            },
            responseLiveData = addPromoCodeLiveData
        )
    }

    val updatePromoCodeLiveData = SingleLiveEvent<UpdatePromoCodeResponse>()
    suspend fun updatePromoCode(request: AddPromoCodeRequest
    ) {
        handleApiCall(
            api = {
                api.updatePromoCode(request)
            },
            responseLiveData = updatePromoCodeLiveData
        )
    }

    val updatePromoCodeStatusLiveData = SingleLiveEvent<UpdatePromoCodeResponse>()
    suspend fun updatePromoCodeStatus(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.updatePromoCodeStatus(map)
            },
            responseLiveData = updatePromoCodeStatusLiveData
        )
    }

    val deletePromoCodeLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun deletePromoCode(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.deletePromoCode(map)
            },
            responseLiveData = deletePromoCodeLiveData
        )
    }

    val giftCardLiveData = SingleLiveEvent<GetAllGiftCardResponse>()
    suspend fun getGiftCard(search:String?=null, page:Int
    ) {
        handleApiCall(
            api = {
                api.getGiftCard(search,page)
            },
            responseLiveData = giftCardLiveData
        )
    }

    val addGiftCardLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun addGiftCard(map:HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.addGiftCard(map)
            },
            responseLiveData = addGiftCardLiveData
        )
    }

    val editGiftCardLiveData = SingleLiveEvent<UpdateGiftCardResponse>()
    suspend fun editGiftCard(map:HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.editGiftCard(map)
            },
            responseLiveData = editGiftCardLiveData
        )
    }

    val updateGiftCardStatusLiveData = SingleLiveEvent<UpdateGiftCardResponse>()
    suspend fun updateGiftCardStatus(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.updateGiftCardStatus(map)
            },
            responseLiveData = updateGiftCardStatusLiveData
        )
    }

    val deleteGiftCardLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun deleteGiftCard(
        map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.deleteGiftCard(map)
            },
            responseLiveData = deleteGiftCardLiveData
        )
    }

    val shippingChargeLiveData = SingleLiveEvent<GetShippingChargeResponse>()
    suspend fun getShippingCharge(countryId:String,page:Int
    ) {
        handleApiCall(
            api = {
                api.getShippingCharge(countryId,page)
            },
            responseLiveData = shippingChargeLiveData
        )
    }

  val shippingTaxLiveData = SingleLiveEvent<GetShippingTaxResponse>()
    suspend fun getShippingTax(
    ) {
        handleApiCall(
            api = {
                api.getShippingTax()
            },
            responseLiveData = shippingTaxLiveData
        )
    }


  val bannerPerDayPriceLiveData = SingleLiveEvent<GetBannerPricePerDayResponse>()
    suspend fun getBannerPerDay(type:Int
    ) {
        handleApiCall(
            api = {
                api.getBannerPerDay(type)
            },
            responseLiveData = bannerPerDayPriceLiveData
        )
    }

 val updateBannerPerDayPriceLiveData = SingleLiveEvent<UpdateBannerPriceResponse>()
    suspend fun updateBannerPerDayPrice(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.updateBannerPerDayPrice(map)
            },
            responseLiveData = updateBannerPerDayPriceLiveData
        )
    }


 val addTaxLiveData = SingleLiveEvent<AddTaxResponse>()
    suspend fun addTax(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.addTax(map)
            },
            responseLiveData = addTaxLiveData
        )
    }

 val addShippingChargeLiveData = SingleLiveEvent<UpdateShippingChargeResponse>()
    suspend fun addShippingCharge(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.addShippingCharge(map)
            },
            responseLiveData = addShippingChargeLiveData
        )
    }

 val updateShippingChargeLiveData = SingleLiveEvent<UpdateShippingChargeResponse>()
    suspend fun updateShippingCharge(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.updateShippingCharge(map)
            },
            responseLiveData = updateShippingChargeLiveData
        )
    }
 val deleteShippingChargeLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun deleteShippingCharge(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.deleteShippingCharge(map)
            },
            responseLiveData = deleteShippingChargeLiveData
        )
    }

 val updateShippingChargeStatusLiveData = SingleLiveEvent<UpdateShippingChargeResponse>()
    suspend fun updateShippingChargeStatus(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.updateShippingChargeStatus(map)
            },
            responseLiveData = updateShippingChargeStatusLiveData
        )
    }


    val bannerRequestLiveData = SingleLiveEvent<GetBannerRequestResponse>()
    suspend fun getBannerRequest(type:Int,search: String?=null,fromDate:String?=null,toDate:String?=null, countryId :String?=null,page:Int
    ) {
        handleApiCall(
            api = {
                api.getBannerRequest(type,search,fromDate, toDate, countryId, page)
            },
            responseLiveData = bannerRequestLiveData
        )
    }

    val updateBannerStatusLiveData = SingleLiveEvent<UpdateBannerRequestStatusResponse>()
    suspend fun updateBannerStatus(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.updateBannerStatus(map)
            },
            responseLiveData = updateBannerStatusLiveData
        )
    }
    val approveBannerRequestLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun approveBannerRequest(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.approveBannerRequest(map)
            },
            responseLiveData = approveBannerRequestLiveData
        )
    }

    val rejectBannerRequestLiveData = SingleLiveEvent<RejectionResponse>()
    suspend fun rejectBannerRequest(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.rejectBannerRequest(map)
            },
            responseLiveData = rejectBannerRequestLiveData
        )
    }

    val getAffiliatedCommissionLiveData = SingleLiveEvent<GetAffiliateCommissionResponse>()
    suspend fun getAffiliatedCommission(
    ) {
        handleApiCall(
            api = {
                api.getAffiliatedCommission()
            },
            responseLiveData = getAffiliatedCommissionLiveData
        )
    }

    val updateAffiliateCommissionLiveData = SingleLiveEvent<UpdateAffiliateCommissionResponse>()
    suspend fun updateAffiliateCommission(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.updateAffiliateCommission(map)
            },
            responseLiveData = updateAffiliateCommissionLiveData
        )
    }

    val activeOrderListLiveData = SingleLiveEvent<GetActiveOrderResponse>()
    suspend fun getActiveOrderList(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.getActiveOrderList(map)
            },
            responseLiveData = activeOrderListLiveData
        )
    }

    val getOrderDetailLiveData = SingleLiveEvent<GetSingleOrderDetailResponse>()
    suspend fun getOrderDetail(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.getOrderDetail(map)
            },
            responseLiveData = getOrderDetailLiveData
        )
    }

    val completedOrderListLiveData = SingleLiveEvent<GetActiveOrderResponse>()
    suspend fun getCompletedOrderList(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.getCompletedOrderList(map)
            },
            responseLiveData = completedOrderListLiveData
        )
    }

    val cancelledOrderListLiveData = SingleLiveEvent<GetActiveOrderResponse>()
    suspend fun getCancelledOrderList(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.getCancelledOrderList(map)
            },
            responseLiveData = cancelledOrderListLiveData
        )
    }

    val returnOrderListLiveData = SingleLiveEvent<GetActiveOrderResponse>()
    suspend fun getReturnOrderList(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.getReturnOrderList(map)
            },
            responseLiveData = returnOrderListLiveData
        )
    }

    val getAssignDriverListLiveData = SingleLiveEvent<GetAvailableDeliveryBoyResponse>()
    suspend fun getDriverToAssign(orderId:String
    ) {
        handleApiCall(
            api = {
                api.getDriverToAssign(orderId)
            },
            responseLiveData = getAssignDriverListLiveData
        )
    }

    val assignDriverLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun assignDriver(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.assignDriver(map)
            },
            responseLiveData = assignDriverLiveData
        )
    }

    val invoiceLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun getInvoice(id:String)
    {
        handleApiCall(
            api = {
                api.getInvoice(id)
            },
            responseLiveData = invoiceLiveData
        )
    }


    val getActiveDealsLiveData = SingleLiveEvent<GetActiveDealResponse>()
    suspend fun getActiveDeals(search:String?=null, page:Int
    ) {
        handleApiCall(
            api = {
                api.getActiveDeals(search,page)
            },
            responseLiveData = getActiveDealsLiveData
        )
    }

    val getExpiredDealsLiveData = SingleLiveEvent<GetActiveDealResponse>()
    suspend fun getExpiredDeals(search:String?=null, page:Int
    ) {
        handleApiCall(
            api = {
                api.getExpiredDeals(search,page)
            },
            responseLiveData = getExpiredDealsLiveData
        )
    }

    val addDealLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun addDeal(request:AddDealRequest)
    {
        handleApiCall(
            api = {
                api.addDeal(request)
            },
            responseLiveData = addDealLiveData
        )
    }

    val updateDealLiveData = SingleLiveEvent<UpdateDealStatusResponse>()
    suspend fun updateDeal(request:AddDealRequest)
    {
        handleApiCall(
            api = {
                api.updateDeal(request)
            },
            responseLiveData = updateDealLiveData
        )
    }

    val updateDealStatusLiveData = SingleLiveEvent<UpdateDealStatusResponse>()
    suspend fun updateDealStatus(map:HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.updateDealStatus(map)
            },
            responseLiveData = updateDealStatusLiveData
        )
    }

    val deleteDealLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun deleteDeal(map:HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.deleteDeal(map)
            },
            responseLiveData = deleteDealLiveData
        )
    }

    val getDealDetailLiveData = SingleLiveEvent<GetDealDetailResponse>()
    suspend fun getDealDetail(map:HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.getDealDetail(map)
            },
            responseLiveData = getDealDetailLiveData
        )
    }

    val getDealProductLiveData = SingleLiveEvent<GetDealProductResponse>()
    suspend fun getDealProduct(map:HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.getDealProduct(map)
            },
            responseLiveData = getDealProductLiveData
        )
    }
    val getFullScreenBannerLiveData = SingleLiveEvent<GetFullScreenBannerResponse>()
    suspend fun getFullScreenBanner(search:String?=null,country:String?=null ,type:String?=null ,startDate: String?=null,enDate: String?=null,page:Int
    ) {
        handleApiCall(
            api = {
                api.getFullScreenBanner(search,country,type,startDate,enDate,page)
            },
            responseLiveData = getFullScreenBannerLiveData
        )
    }

    val addFullScreenBannerLiveData = SingleLiveEvent<AddFullScreenBannerResponse>()
    suspend fun addFullScreenBanner(request:AddFullScreenBannerRequest)
    {
        handleApiCall(
            api = {
                api.addFullScreenBanner(request)
            },
            responseLiveData = addFullScreenBannerLiveData
        )
    }

    val updateFullScreenBannerLiveData = SingleLiveEvent<AddFullScreenBannerResponse>()
    suspend fun updateFullScreenBanner(request:AddFullScreenBannerRequest)
    {
        handleApiCall(
            api = {
                api.updateFullScreenBanner(request)
            },
            responseLiveData = updateFullScreenBannerLiveData
        )
    }
    val deleteFullScreenBannerLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun deleteFullScreenBanner(map:HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.deleteFullScreenBanner(map)
            },
            responseLiveData = deleteFullScreenBannerLiveData
        )
    }
    val getAdminStoryLiveData = SingleLiveEvent<GetStoryResponse>()
    suspend fun getAdminStory(page:Int)
    {
        handleApiCall(
            api = {
                api.getAdminStory(page)
            },
            responseLiveData = getAdminStoryLiveData
        )
    }

    val getSellerStoryLiveData = SingleLiveEvent<GetStoryResponse>()
    suspend fun getSellerStory(page:Int)
    {
        handleApiCall(
            api = {
                api.getSellerStory(page)
            },
            responseLiveData = getSellerStoryLiveData
        )
    }

    val addStoryLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun addStory(map:HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.addStory(map)
            },
            responseLiveData = addStoryLiveData
        )
    }

    val deleteStoryLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun deleteStory(map:HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.deleteStory(map)
            },
            responseLiveData = deleteStoryLiveData
        )
    }


    val getLiveRequestLiveData = SingleLiveEvent<GetLiveRequestResponse>()
    suspend fun getLiveRequest(map: HashMap<String, Any>)
    {
        handleApiCall(
            api = {
                api.getLiveRequest(map)
            },
            responseLiveData = getLiveRequestLiveData
        )
    }

    val acceptLiveRequestLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun acceptLiveRequest(map:HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.acceptLiveRequest(map)
            },
            responseLiveData = acceptLiveRequestLiveData
        )
    }

    val rejectLiveRequestLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun rejectLiveRequest(map:HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.rejectLiveRequest(map)
            },
            responseLiveData = rejectLiveRequestLiveData
        )
    }

    val getTimeSlotLiveData = SingleLiveEvent<GetTimeSlotResponse>()
    suspend fun getTimeSlot(countryId:String,date:String)
    {
        handleApiCall(
            api = {
                api.getTimeSlot(countryId,date)
            },
            responseLiveData = getTimeSlotLiveData
        )
    }

    val addTimeSlotLiveData = SingleLiveEvent<UpdateTimeSlotResponse>()
    suspend fun addTimeSlot(request:AddTimeSlotRequest)
    {
        handleApiCall(
            api = {
                api.addTimeSlot(request)
            },
            responseLiveData = addTimeSlotLiveData
        )
    }

    val updateTimeSlotLiveData = SingleLiveEvent<UpdateTimeSlotResponse>()
    suspend fun updateTimeSlot(request:AddTimeSlotRequest)
    {
        handleApiCall(
            api = {
                api.updateTimeSlot(request)
            },
            responseLiveData = updateTimeSlotLiveData
        )
    }

    val updateTimeSlotStatusLiveData = SingleLiveEvent<UpdateTimeSlotResponse>()
    suspend fun updateTimeSlotStatus(map:HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.updateTimeSlotStatus(map)
            },
            responseLiveData = updateTimeSlotStatusLiveData
        )
    }

    val deleteTimeSlotLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun deleteTimeSlot(map:HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.deleteTimeSlot(map)
            },
            responseLiveData = deleteTimeSlotLiveData
        )
    }

    val newAffiliateUserLiveData = SingleLiveEvent<GetNewAffiliateUserResponse>()
    suspend fun getNewAffiliateUser(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.getNewAffiliateUser(map)
            },
            responseLiveData = newAffiliateUserLiveData
        )
    }

    val approvedAffiliateUserLiveData = SingleLiveEvent<GetNewAffiliateUserResponse>()
    suspend fun getApprovedAffiliateUser(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.getApprovedAffiliateUser(map)
            },
            responseLiveData = approvedAffiliateUserLiveData
        )
    }

    val rejectedAffiliateUserLiveData = SingleLiveEvent<GetNewAffiliateUserResponse>()
    suspend fun getRejectedAffiliateUser(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.getRejectedAffiliateUser(map)
            },
            responseLiveData = rejectedAffiliateUserLiveData
        )
    }

    val userAffiliateDetailLiveData = SingleLiveEvent<GetAffiliateUserDetailResponse>()
    suspend fun getAffiliateUserDetail(userId:String
    ) {
        handleApiCall(
            api = {
                api.getAffiliateUserDetail(userId)
            },
            responseLiveData = userAffiliateDetailLiveData
        )
    }

    val updateAffiliateUserStatusLiveData = SingleLiveEvent<BlockUnblockAffiliateUserResponse>()
    suspend fun blockUnblockAffiliateUser(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.blockUnblockAffiliateUser(map)
            },
            responseLiveData = updateAffiliateUserStatusLiveData
        )
    }

 val acceptAffiliateUserLiveData = SingleLiveEvent<AcceptAffiliateUserResponse>()
    suspend fun acceptAffiliateUser(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.acceptAffiliateUser(map)
            },
            responseLiveData = acceptAffiliateUserLiveData
        )
    }

 val rejectAffiliateUserLiveData = SingleLiveEvent<AcceptAffiliateUserResponse>()
    suspend fun rejectAffiliateUser(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.rejectAffiliateUser(map)
            },
            responseLiveData = rejectAffiliateUserLiveData
        )
    }

    val getPromotionalBannerLiveData = SingleLiveEvent<GetPromotionalBannerResponse>()
    suspend fun getPromotionalBanner(map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.getPromotionalBanner(map)
            },
            responseLiveData = getPromotionalBannerLiveData
        )
    }

    val addPromotionalBannerLiveData = SingleLiveEvent<AddPromotionalBannerResponse>()
    suspend fun addPromotionalBanner(request: AddPromotionalBannerRequest)
    {
        handleApiCall(
            api = {
                api.addPromotionalBanner(request)
            },
            responseLiveData = addPromotionalBannerLiveData
        )
    }
    val updatePromotionalBannerLiveData = SingleLiveEvent<AddPromotionalBannerResponse>()
    suspend fun updatePromotionalBanner(request:AddPromotionalBannerRequest)
    {
        handleApiCall(
            api = {
                api.updatePromotionalBanner(request)
            },
            responseLiveData = updatePromotionalBannerLiveData
        )
    }
    val deletePromotionalBannerLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun deletePromotionalBanner(map:HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.deletePromotionalBanner(map)
            },
            responseLiveData = deletePromotionalBannerLiveData
        )
    }
    val updatePromotionalBannerStatusLiveData = SingleLiveEvent<UpdatePromotionalBannerStatusResponse>()
    suspend fun updatePromotionalBannerStatus(map:HashMap<String,Any>)
    {
        handleApiCall(
            api = {
                api.updatePromotionalBannerStatus(map)
            },
            responseLiveData = updatePromotionalBannerStatusLiveData
        )
    }
    val updateDefaultShippingFeeLiveData = SingleLiveEvent<UpdateDefaultShippingChargeResponse>()
    suspend fun updateDefaultShippingFee(map:HashMap<String,Any>
    ) {
        handleApiCall(
            api = {
                api.updateDefaultShippingFee(map)
            },
            responseLiveData = updateDefaultShippingFeeLiveData
        )
    }
    val getDefaultShippingFeeLiveData = SingleLiveEvent<GetDefaultShippingChargeResponse>()
    suspend fun getDefaultShippingFee(countryId: String
    ) {
        handleApiCall(
            api = {
                api.getDefaultShippingFee(countryId)
            },
            responseLiveData = getDefaultShippingFeeLiveData
        )
    }

    val getAllProductLiveData = SingleLiveEvent<GetAllProductListResponse>()
    suspend fun getAllProduct(map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.getAllProduct(map)
            },
            responseLiveData = getAllProductLiveData
        )
    }
val getProductDetailLiveData = SingleLiveEvent<GetProductDetailResponse>()
    suspend fun getProductDetail(map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.getProductDetail(map)
            },
            responseLiveData = getProductDetailLiveData
        )
    }

val updateProductStatusLiveData = SingleLiveEvent<UpdateProductStatusResponse>()
    suspend fun updateProductStatus(map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.updateProductStatus(map)
            },
            responseLiveData = updateProductStatusLiveData
        )
    }

    val getWithdrawalRequestLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun getWithdrawalRequest(map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.getWithdrawalRequest(map)
            },
            responseLiveData = getWithdrawalRequestLiveData
        )
    }

    val acceptWithdrawalRequestLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun acceptWithdrawalRequest(map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.acceptWithdrawalRequest(map)
            },
            responseLiveData = acceptWithdrawalRequestLiveData
        )
    }

    val rejectWithdrawalRequestLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun rejectWithdrawalRequest(map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.rejectWithdrawalRequest(map)
            },
            responseLiveData = rejectWithdrawalRequestLiveData
        )
    }
}

class SingleLiveEvent<T> : MutableLiveData<T>() {
    private val pending = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner) { t ->
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t)
                // Clear the value after it has been delivered
                // value = null
            }
        }

    }

    override fun setValue(value: T?) {
        pending.set(true)
        super.setValue(value)
    }

    fun call() {
        value = null
    }

    fun clear() {
        value = null
        pending.set(false)
    }
}

