package com.desynatadmin.data.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desynatadmin.adapter.AssignAffiliateUserAdapter
import com.desynatadmin.data.repo.MainRepository
import com.desynatadmin.ui.home.affiliateCommission.response.GetAffiliateCommissionResponse
import com.desynatadmin.ui.home.affiliateCommission.response.UpdateAffiliateCommissionResponse
import com.desynatadmin.ui.home.affiliateUserManagement.response.AcceptAffiliateUserResponse
import com.desynatadmin.ui.home.affiliateUserManagement.response.BlockUnblockAffiliateUserResponse
import com.desynatadmin.ui.home.affiliateUserManagement.response.GetAffiliateUserDetailResponse
import com.desynatadmin.ui.home.affiliateUserManagement.response.GetNewAffiliateUserResponse
import com.desynatadmin.ui.home.affiliateWithdrawalRequest.response.GetWithdrawalRequestResponse
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
import com.desynatadmin.ui.home.notificationManagement.response.GetNotificationCountResponse
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    val progress: LiveData<Boolean> get() = repository.progress
    val error: LiveData<String> get() = repository.error
    fun reset() {
        repository.error.value = null
        repository.progress.value = false
    }

    val uploadImageResult: LiveData<FileUploadResponse>
        get() = repository.responseUploadImage

    fun uploadImage(
        image: File?=null
    ) {
        viewModelScope.launch {
            repository.uploadImage(image)
        }
    }

 val uploadVideoImageResult: LiveData<FileUploadResponse>
        get() = repository.responseUploadVideoImage

    fun uploadVideoImage(
        image: File?=null
    ) {
        viewModelScope.launch {
            repository.uploadVideoImage(image)
        }
    }

 val uploadWithResolutionResult: LiveData<FileUploadResponse>
        get() = repository.responseUploadWithResolution

    fun uploadWithResolution(
        image: File?=null,type:String
    ) {
        viewModelScope.launch {
            repository.uploadWithResolution(image,type)
        }
    }


    val logoutResult: LiveData<CommonResponse>
        get() = repository.logoutLiveData

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    val getAllBrandResult: LiveData<GetBrandResponse>
        get() = repository.brandLiveData

    fun getAllBrand(map:HashMap<String, Any>) {
        viewModelScope.launch {
            repository.getBrand(map)
        }
    }

    val addBrandResult: LiveData<CommonResponse>
        get() = repository.addBrandLiveData

    fun addBrand(map:HashMap<String,RequestBody>) {
        viewModelScope.launch {
            repository.addBrand(map)
        }
    }

    val updateBrandResult: LiveData<CommonResponse>
        get() = repository.editBrandLiveData

    fun updateBrand(map:HashMap<String,RequestBody>) {
        viewModelScope.launch {
            repository.editBrand(map)
        }
    }

    val deleteBrandResult: LiveData<CommonResponse>
        get() = repository.deleteBrandLiveData

    fun deleteBrand(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.deleteBrand(map)
        }
    }

    val updateBrandStatusResult: LiveData<UpdateBrandStatusResponse>
        get() = repository.updateBrandStatusLiveData

    fun updateBrandStatus(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.updateBrandStatus(map)
        }
    }

    val getAllCategoryResult: LiveData<GetCategoryResponse>
        get() = repository.categoryLiveData

    fun getAllCategory() {
        viewModelScope.launch {
            repository.getCategory()
        }
    }

    val getAllSubCategoryResult: LiveData<GetSubCategoryResponse>
        get() = repository.subCategoryLiveData

    fun getAllSUbCategory(categoryId:String,search:String?=null, page:Int) {
        viewModelScope.launch {
            repository.getSubCategory(categoryId,search,page)
        }
    }

    val addSubCategoryResult: LiveData<CommonResponse>
        get() = repository.addSubCategoryLiveData

    fun addSubCategory(map:HashMap<String,RequestBody>) {
        viewModelScope.launch {
            repository.addSubCategory(map)
        }
    }

    val updateSubCategoryResult: LiveData<CommonResponse>
        get() = repository.updateSubCategoryLiveData

    fun updateSubCategory(map:HashMap<String,RequestBody>) {
        viewModelScope.launch {
            repository.editSubCategory(map)
        }
    }

    val deleteSubCategoryResult: LiveData<CommonResponse>
        get() = repository.deleteSubCategoryLiveData

    fun deleteSubCategory(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.deleteSubCategory(map)
        }
    }

    val getServiceCityList: LiveData<ServiceCityListResponse>
        get() = repository.getServiceListData

    fun hitServiceCityList(
        token: String,
        country_id: String,
        limit: String,
        search: String,
        page : String
    ) {
        viewModelScope.launch {
            repository.hitServiceCityList(
                token,
                country_id,
                limit,
                search,
                page
            )
        }
    }

    val hitCityList: LiveData<CityResponse>
        get() = repository.getCityData

    fun hitCity(token : String, id : String) {
        viewModelScope.launch {
            repository.hitCityList(token,id)
        }
    }

    val getAddServiceCity: LiveData<AddServiceCityResponse>
        get() = repository.getAddServiceCity

    fun hitAddServiceCity(token : String, map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.hitAddServiceCity(token,map)
        }
    }

    val updateSubCategoryStatusResult: LiveData<UpdateSubCategoryResponse>
        get() = repository.updateSubCategoryStatusLiveData

    fun updateSubCategoryStatus(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.updateSubCategoryStatus(map)
        }
    }

  val updateCategoryStatusResult: LiveData<UpdateCategoryResponse>
        get() = repository.updateCategoryStatusLiveData

    fun updateCategoryStatus(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.updateCategoryStatus(map)
        }
    }

    val getChangeCityStatus : LiveData<ChangeCityStatusResponse>
        get() = repository.getChangeStatus

    fun hitChangeCityStatus(token : String , map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.
            hitChangeCityStatus(token,map)
        }
    }

    val getDeleteCity : LiveData<CommonResponse>
        get() = repository.getDeleteServiceCity

    fun hitDeleteServiceCity(token : String , map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.
            hitDeleteCityService(token,map)
        }
    }

    val getAddZoneData : LiveData<AddZoneResponse>
        get() = repository.getAddZoneData

    fun hitAddZone(token : String , map: AddZoneRequest) {
        viewModelScope.launch {
            repository.
            hitAddZone(token,map)
        }
    }

    val getNewSellerList : LiveData<NewSellerResponse>
        get() = repository.getNewSellerList

    fun hitNewSellerList(
        token : String,
        country_id : String,
        from_date : String,
        to_date : String,
        limit : String,
        page : String,
        search : String,
    ) {
        viewModelScope.launch {
            repository.
            hitNewSellerList(token,country_id,from_date, to_date,limit,page,search)
        }
    }

    val getApprovedSellerList : LiveData<ApprovedSellerResponse>
        get() = repository.getApprovedData

    fun hitApprovedSellerList(
        token : String?=null,
        country_id : String?=null,
        from_date : String?=null,
        to_date : String?=null,
        limit : String?=null,
        search : String?=null,
        page: String?=null,
    ) {
        viewModelScope.launch {
            repository.
            hitApprovedSellerList(token,country_id,from_date, to_date,limit,search,page)
        }
    }

    val assignSellerListResult : LiveData<AssignSellerResponse>
        get() = repository.getAssignSellerLiveData

    fun assignSellerList(
    ) {
        viewModelScope.launch {
            repository.
            assignSellerList()
        }
    }

    val getRejectedSellerList : LiveData<RejectedListResponse>
        get() = repository.getRejectSellerList

    fun hitRejectedSellerList(
        token : String,
        country_id : String,
        from_date : String,
        to_date : String,
        limit : String,
        search : String,
        page: String
    ) {
        viewModelScope.launch {
            repository.
            hitRejectedSellerList(token,country_id,from_date, to_date,limit,search,page)
        }
    }


    val getSellerDetailData : LiveData<SellerDetailResponse>
        get() = repository.getSellerDetail

    fun hitSellerDetail(
        token : String,
        seller_id : String,

    ) {
        viewModelScope.launch {
            repository.
            hitSellerDetail(token,seller_id)
        }
    }



    val getAcceptSeller : LiveData<AcceptSellerResponse>
        get() = repository.getAcceptSeller
    fun hitAcceptSeller(
        token : String,
        map: HashMap<String, Any>
        ) {
        viewModelScope.launch {
            repository.
            hitAcceptSeller(token,map)
        }
    }


    val getRejectSeller : LiveData<AcceptSellerResponse>
        get() = repository.getRejectSeller
    fun hitRejectSeller(
        token : String,
        map: HashMap<String, Any>
    ) {
        viewModelScope.launch {
            repository.
            hitRejectSeller(token,map)
        }
    }


    val getDeleteZone : LiveData<AcceptSellerResponse>
        get() = repository.getDeleteZone
    fun hitDeleteZone(
        token : String,
        map: HashMap<String, Any>
    ) {
        viewModelScope.launch {
            repository.
            hitDeleteZone(token,map)
        }
    }

    val getUpdateSellerStatus : LiveData<SellerStatusResponse>
        get() = repository.getUpdateSellerData
    fun hitUpdateSellerStaus(
        token : String,
        map: HashMap<String, Any>
    ) {
        viewModelScope.launch {
            repository.
            hitUpdateSeller(token,map)
        }
    }



    val responseLocation : LiveData<PlacesResponse>
        get() = repository.getLocation

    fun httpLocationSearch(input:String,key:String) {
        viewModelScope.launch {
            repository.hitPlaceResponse(input,key)
        }
    }

    val getZoneData : LiveData<ZoneListResponse>
        get() = repository.getZoneList

    fun hitZoneList(input:String,country_id : String , city_id:String) {
        viewModelScope.launch {
            repository.hitZoneList(input,country_id, city_id)
        }
    }

    val getUpdateZone : LiveData<UpdateZonResponse>
        get() = repository.getUpdateZoneData

    fun hitUpdateZone(input:String,req : AddZoneRequest) {
        viewModelScope.launch {
            repository.hitUpdateZone(input,req)
        }
    }
    val getUpdateZoneStatus : LiveData<UpdateZoneStatusResponse>
        get() = repository.getUpdateZoneStatusData

    fun hitUpdateZoneStatus(input:String,map : HashMap<String, Any>) {
        viewModelScope.launch {
            repository.hitUpdateZoneStatus(input,map)
        }
    }

    val responseDetailPlace : LiveData<PlaceDetailsResponse>
        get() = repository.fetchLocation

    fun httpDetailPlace(placeId:String,key:String) {
        viewModelScope.launch {
            repository.
            hitPlaceDetail(placeId,key)
        }
    }


    val subscriptionPlanResult: LiveData<GetAllPlanResponse>
        get() = repository.subscriptionPlanLiveData

    fun getSubscriptionPlan(countryId:String?=null) {
        viewModelScope.launch {
            repository.getSubscriptionPlan(countryId)
        }
    }

    val subscriptionPlanDetailResult: LiveData<UpdatePlanStatusResponse>
        get() = repository.subscriptionPlanDetailLiveData

    fun getSubscriptionPlanDetail(planId:String?=null) {
        viewModelScope.launch {
            repository.getSubscriptionPlanDetail(planId)
        }
    }

    val addSubscriptionPlanResult: LiveData<CommonResponse>
        get() = repository.addSubscriptionPlanLiveData

    fun addSubscriptionPlan(request: AddPlanRequest) {
        viewModelScope.launch {
            repository.addSubscriptionPlan(request)
        }
    }

    val editSubscriptionPlanResult: LiveData<CommonResponse>
        get() = repository.editSubscriptionPlanLiveData

    fun updateSubscriptionPlan(request: AddPlanRequest) {
        viewModelScope.launch {
            repository.updateSubscriptionPlan(request)
        }
    }

    val deleteSubscriptionResult: LiveData<CommonResponse>
        get() = repository.deleteSubscriptionPlanLiveData

    fun deleteSubscriptionPlan(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.deleteSubscriptionPlan(map)
        }
    }

    val updateSubscriptionPlanStatusResult: LiveData<UpdatePlanStatusResponse>
        get() = repository.updateSubscriptionPlanStatusLiveData

    fun updateSubscriptionPlanStatus(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.updateSubscriptionPlanStatus(map)
        }
    }

    val getCountryResult: LiveData<GetCountryResponse>
        get() = repository.countryPlanLiveData

    fun getCountry() {
        viewModelScope.launch {
            repository.getCountry()
        }
    }

    val getCountryListResult: LiveData<GetCountryResponse>
        get() = repository.countryPlanLiveData

    fun getCountryList() {
        viewModelScope.launch {
            repository.getCountry()
        }
    }

    val getUserCountryResult: LiveData<GetCountryResponse>
        get() = repository.userCountryLiveData

    fun getUserCountry() {
        viewModelScope.launch {
            repository.getUserCountry()
        }
    }

    val getFilterData: LiveData<UpdateZonResponse>
        get() = repository.filterNewSeller

    fun httpFilterData(token:String,country_id: String,from_date: String,to_date: String) {
        viewModelScope.launch {
            repository.hitNewSeller(
                token = token,
                country_id = country_id,
                from_date = from_date,
                to_date = to_date
            )
        }
    }

    val getAllUserResult: LiveData<GetUserResponse>
        get() = repository.userLiveData

    fun getAllUser(search: String?=null, page:String?=null,countryId:String?=null,fromDate:String?=null,toDate:String?=null) {
        viewModelScope.launch {
            repository.getAllUser(search,page,countryId,fromDate,toDate)
        }
    }

    val getAssignUserResult: LiveData<AssignUserResponse>
        get() = repository.getAssignUserLiveData

    fun getAssignUser() {
        viewModelScope.launch {
            repository.getAssignUser()
        }
    }

    val getUserDetailResult: LiveData<GetUserDetailResponse>
        get() = repository.userDetailLiveData

    fun getUserDetail(userId:String) {
        viewModelScope.launch {
            repository.getUserDetail(userId)
        }
    }

    val updateUserStatusResult: LiveData<BlockUnblockUserResponse>
        get() = repository.updateUserStatusLiveData

    fun blockUnblockUser(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.blockUnblockUser(map)
        }
    }

    val walletBalanceResult: LiveData<AddWalletBalanceResponse>
        get() = repository.walletBalanceLiveData

    fun addWalletAmount(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.addWalletAmount(map)
        }
    }

    val getReasonResult: LiveData<GetReasonResponse>
        get() = repository.reasonLiveData

    fun getReason(type:Int,search: String?=null, page:Int) {
        viewModelScope.launch {
            repository.getReason(type,search,page)
        }
    }

    val addReasonResult: LiveData<CommonResponse>
        get() = repository.addReasonLiveData

    fun addReason(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.addReason(map)
        }
    }

    val updateReasonResult: LiveData<UpdateReasonResponse>
        get() = repository.updateReasonLiveData

    fun updateReason(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.updateReason(map)
        }
    }

    val kuwaitZoneResult: LiveData<GetKuwaitZoneResponse>
        get() = repository.kuwaitLiveData

    fun getKuwaitZone() {
        viewModelScope.launch {
            repository.getKuwaitZone()
        }
    }

    val updateReasonStatusResult: LiveData<BlockUnblockReasonResponse>
        get() = repository.updateReasonStatusLiveData

    fun blockUnblockReason(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.blockUnblockReason(map)
        }
    }

    val deleteReasonResult: LiveData<CommonResponse>
        get() = repository.deleteReasonLiveData

    fun deleteReason(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.deleteReason(map)
        }
    }

    val getDeliveryBoyResult: LiveData<GetAllDeliveryBoyResponse>
        get() = repository.deliveryBoyLiveData

    fun getDeliveryBoy(search: String?=null, page:String?=null,fromDate:String?=null,toDate:String?=null) {
        viewModelScope.launch {
            repository.getDeliveryBoy(search,page,fromDate,toDate)
        }
    }


    val assignDeliveryBoyResult: LiveData<GetAssignDeliveryBoyResponse>
        get() = repository.assignDeliveryBoyLiveData

    fun getAssignDeliveryBoy() {
        viewModelScope.launch {
            repository.getAssignDeliveryBoy()
        }
    }

    val getDeliveryBoyDetailResult: LiveData<GetDeliveryBoyDetailResponse>
        get() = repository.deliveryBoyDetailLiveData

    fun getDeliveryBoyDetail(userId:String) {
        viewModelScope.launch {
            repository.getDeliveryBoyDetail(userId)
        }
    }

    val addDeliveryBoyResult: LiveData<CommonResponse>
        get() = repository.addDeliveryBoyLiveData

    fun addDeliveryBoy(request: AddDeliveryBoyRequest) {
        viewModelScope.launch {
            repository.addDeliveryBoy(request)
        }
    }

    val updateDeliveryBoyResult: LiveData<CommonResponse>
        get() = repository.updateDeliveryBoyLiveData

    fun updateDeliveryBoy(request: AddDeliveryBoyRequest) {
        viewModelScope.launch {
            repository.updateDeliveryBoy(request)
        }
    }

    val updateDeliveryBoyStatusResult: LiveData<BlockUnblockDeliveryBoyResponse>
        get() = repository.updateDeliveryBoyStatusLiveData

    fun blockUnblockDeliveryBoy(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.blockUnblockDeliveryBoy(map)
        }
    }

    val deleteDeliveryBoyResult: LiveData<CommonResponse>
        get() = repository.deleteDeliveryBoyLiveData

    fun deleteDeliveryBoy(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.deleteDeliveryBoy(map)
        }
    }


    val getPromoCodeListResult: LiveData<GetPromoCodeResponse>
        get() = repository.promoCodeLiveData

    fun getPromoCode(search: String?=null,fromDate:String?=null,toDate:String?=null, countryId :String?=null) {
        viewModelScope.launch {
            repository.getPromoCode(search,fromDate,toDate,countryId)
        }
    }

    val addPromoCodeResult: LiveData<CommonResponse>
        get() = repository.addPromoCodeLiveData

    fun addPromoCode(request: AddPromoCodeRequest) {
        viewModelScope.launch {
            repository.addPromoCode(request)
        }
    }

    val updatePromoCodeResult: LiveData<UpdatePromoCodeResponse>
        get() = repository.updatePromoCodeLiveData

    fun updatePromoCode(request: AddPromoCodeRequest) {
        viewModelScope.launch {
            repository.updatePromoCode(request)
        }
    }

    val updatePromoCodeStatusResult: LiveData<UpdatePromoCodeResponse>
        get() = repository.updatePromoCodeStatusLiveData

    fun updatePromoCodeStatus(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.updatePromoCodeStatus(map)
        }
    }

    val deletePromoCodeResult: LiveData<CommonResponse>
        get() = repository.deletePromoCodeLiveData

    fun deletePromoCode(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.deletePromoCode(map)
        }
    }

    val getGiftCardResult: LiveData<GetAllGiftCardResponse>
        get() = repository.giftCardLiveData

    fun getGiftCard(search: String?=null, page:Int) {
        viewModelScope.launch {
            repository.getGiftCard(search,page)
        }
    }

    val addGiftCardResult: LiveData<CommonResponse>
        get() = repository.addGiftCardLiveData

    fun addGiftCard(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.addGiftCard(map)
        }
    }

    val editGiftCardResult: LiveData<UpdateGiftCardResponse>
        get() = repository.editGiftCardLiveData

    fun editGiftCard(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.editGiftCard(map)
        }
    }

    val deleteGiftCardResult: LiveData<CommonResponse>
        get() = repository.deleteGiftCardLiveData

    fun deleteGiftCard(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.deleteGiftCard(map)
        }
    }

    val updateGiftCardStatusLiveData: LiveData<UpdateGiftCardResponse>
        get() = repository.updateGiftCardStatusLiveData

    fun updateGiftCardStatus(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.updateGiftCardStatus(map)
        }
    }

    val getShippingChargeResult: LiveData<GetShippingChargeResponse>
        get() = repository.shippingChargeLiveData

    fun getShippingCharge(countryId:String,page:Int) {
        viewModelScope.launch {
            repository.getShippingCharge(countryId,page)
        }
    }

    val getShippingTaxResult: LiveData<GetShippingTaxResponse>
        get() = repository.shippingTaxLiveData

    fun getShippingTax() {
        viewModelScope.launch {
            repository.getShippingTax()
        }
    }

    val bannerPerDayResult: LiveData<GetBannerPricePerDayResponse>
        get() = repository.bannerPerDayPriceLiveData

    fun getBannerPerDay(type:Int) {
        viewModelScope.launch {
            repository.getBannerPerDay(type)
        }
    }

    val updateBannerPerDayResult: LiveData<UpdateBannerPriceResponse>
        get() = repository.updateBannerPerDayPriceLiveData

    fun updateBannerPerDayPrice(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.updateBannerPerDayPrice(map)
        }
    }

    val addTaxResult: LiveData<AddTaxResponse>
        get() = repository.addTaxLiveData

    fun addTax(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.addTax(map)
        }
    }

    val addShippingChargeResult: LiveData<UpdateShippingChargeResponse>
        get() = repository.addShippingChargeLiveData

    fun addShippingCharge(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.addShippingCharge(map)
        }
    }

    val updateShippingChargeResult: LiveData<UpdateShippingChargeResponse>
        get() = repository.updateShippingChargeLiveData

    fun updateShippingCharge(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.updateShippingCharge(map)
        }
    }

    val deleteShippingChargeResult: LiveData<CommonResponse>
        get() = repository.deleteShippingChargeLiveData

    fun deleteShippingCharge(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.deleteShippingCharge(map)
        }
    }

    val updateShippingChargeStatusResult: LiveData<UpdateShippingChargeResponse>
        get() = repository.updateShippingChargeStatusLiveData
    fun updateShippingChargeStatus(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.updateShippingChargeStatus(map)
        }
    }
    val bannerRequestResult: LiveData<GetBannerRequestResponse>
        get() = repository.bannerRequestLiveData
    fun getBannerRequest(type:Int,search: String?=null,fromDate:String?=null,toDate:String?=null, countryId :String?=null,page: Int) {
        viewModelScope.launch {
            repository.getBannerRequest(type,search,fromDate, toDate, countryId, page)
        }
    }
    val updateBannerStatusResult: LiveData<UpdateBannerRequestStatusResponse>
        get() = repository.updateBannerStatusLiveData
    fun updateBannerStatus(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.updateBannerStatus(map)
        }
    }
    val approveBannerRequestResult: LiveData<CommonResponse>
        get() = repository.approveBannerRequestLiveData
    fun approveBannerRequest(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.approveBannerRequest(map)
        }
    }
    val rejectBannerRequestResult: LiveData<RejectionResponse>
        get() = repository.rejectBannerRequestLiveData
    fun rejectBannerRequest(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.rejectBannerRequest(map)
        }
    }
    val getAffiliatedCommissionResult: LiveData<GetAffiliateCommissionResponse>
        get() = repository.getAffiliatedCommissionLiveData
    fun getAffiliatedCommission() {
        viewModelScope.launch {
            repository.getAffiliatedCommission()
        }
    }
    val updateAffiliateCommissionResult: LiveData<UpdateAffiliateCommissionResponse>
        get() = repository.updateAffiliateCommissionLiveData
    fun updateAffiliateCommission(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.updateAffiliateCommission(map)
        }
    }
    val getActiveOrderListResult: LiveData<GetActiveOrderResponse>
        get() = repository.activeOrderListLiveData
    fun getActiveOrderList(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getActiveOrderList(map)
        }
    }
    val getOrderDetailResult: LiveData<GetSingleOrderDetailResponse>
        get() = repository.getOrderDetailLiveData
    fun getOrderDetail(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getOrderDetail(map)
        }
    }
    val getCompletedOrderListResult: LiveData<GetActiveOrderResponse>
        get() = repository.completedOrderListLiveData
    fun getCompletedOrderList(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getCompletedOrderList(map)
        }
    }
    val getReturnOrderListResult: LiveData<GetActiveOrderResponse>
        get() = repository.returnOrderListLiveData
    fun getReturnOrderList(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getReturnOrderList(map)
        }
    }
    val getCancelledOrderListResult: LiveData<GetActiveOrderResponse>
        get() = repository.cancelledOrderListLiveData
    fun getCancelledOrderList(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getCancelledOrderList(map)
        }
    }
    val getAssignDriverListResult: LiveData<GetAvailableDeliveryBoyResponse>
        get() = repository.getAssignDriverListLiveData
    fun getDriverToAssign(orderId:String) {
        viewModelScope.launch {
            repository.getDriverToAssign(orderId)
        }
    }
    val assignDriverResult: LiveData<CommonResponse>
        get() = repository.assignDriverLiveData
    fun assignDriver(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.assignDriver(map)
        }
    }
    val invoiceResult: LiveData<CommonResponse>
        get() = repository.invoiceLiveData
    fun getInvoice(id:String) {
        viewModelScope.launch {
            repository.getInvoice(id)
        }
    }
    val getActiveDealsResult: LiveData<GetActiveDealResponse>
        get() = repository.getActiveDealsLiveData
    fun getActiveDeals(search: String?=null, page:Int) {
        viewModelScope.launch {
            repository.getActiveDeals(search,page)
        }
    }
    val getExpiredDealsResult: LiveData<GetActiveDealResponse>
        get() = repository.getExpiredDealsLiveData
    fun getExpiredDeals(search: String?=null, page:Int) {
        viewModelScope.launch {
            repository.getExpiredDeals(search,page)
        }
    }
    val addDealResult: LiveData<CommonResponse>
        get() = repository.addDealLiveData
    fun addDeal(request:AddDealRequest) {
        viewModelScope.launch {
            repository.addDeal(request)
        }
    }
    val updateDealResult: LiveData<UpdateDealStatusResponse>
        get() = repository.updateDealLiveData
    fun updateDeal(request:AddDealRequest) {
        viewModelScope.launch {
            repository.updateDeal(request)
        }
    }
    val updateDealStatusResult: LiveData<UpdateDealStatusResponse>
        get() = repository.updateDealStatusLiveData
    fun updateDealStatus(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.updateDealStatus(map)
        }
    }
    val deleteDealResult: LiveData<CommonResponse>
        get() = repository.deleteDealLiveData

    fun deleteDeal(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.deleteDeal(map)
        }
    }
    val getDealDetailResult: LiveData<GetDealDetailResponse>
        get() = repository.getDealDetailLiveData
    fun getDealDetail(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.getDealDetail(map)
        }
    }
    val getDealProductResult: LiveData<GetDealProductResponse>
        get() = repository.getDealProductLiveData
    fun getDealProduct(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.getDealProduct(map)
        }
    }
    val getFullScreenBannerResult: LiveData<GetFullScreenBannerResponse>
        get() = repository.getFullScreenBannerLiveData
    fun getFullScreenBanner(search: String?=null,country: String?=null,type: String?=null,startDate: String?=null,enDate: String?=null, page:Int) {
        viewModelScope.launch {
            repository.getFullScreenBanner(search,country,type,startDate,enDate,page)
        }
    }
    val addFullScreenBannerResult: LiveData<AddFullScreenBannerResponse>
        get() = repository.addFullScreenBannerLiveData
    fun addFullScreenBanner(request:AddFullScreenBannerRequest) {
        viewModelScope.launch {
            repository.addFullScreenBanner(request)
        }
    }
val updateFullScreenBannerResult: LiveData<AddFullScreenBannerResponse>
        get() = repository.updateFullScreenBannerLiveData
    fun updateFullScreenBanner(request:AddFullScreenBannerRequest) {
        viewModelScope.launch {
            repository.updateFullScreenBanner(request)
        }
    }
val deleteFullScreenBannerResult: LiveData<CommonResponse>
        get() = repository.deleteFullScreenBannerLiveData
    fun deleteFullScreenBanner(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.deleteFullScreenBanner(map)
        }
    }
    val addStoryResult: LiveData<CommonResponse>
        get() = repository.addStoryLiveData
    fun addStory(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.addStory(map)
        }
    }
    val deleteStoryResult: LiveData<CommonResponse>
        get() = repository.deleteStoryLiveData
    fun deleteStory(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.deleteStory(map)
        }
    }
    val getSellerStoryResult: LiveData<GetStoryResponse>
        get() = repository.getSellerStoryLiveData
    fun getSellerStory(page:Int) {
        viewModelScope.launch {
            repository.getSellerStory(page)
        }
    }
    val getAdminStoryResult: LiveData<GetStoryResponse>
        get() = repository.getAdminStoryLiveData
    fun getAdminStory(page:Int) {
        viewModelScope.launch {
            repository.getAdminStory(page)
        }
    }
    val getLiveRequestResult: LiveData<GetLiveRequestResponse>
        get() = repository.getLiveRequestLiveData
    fun getLiveRequest(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.getLiveRequest(map)
        }
    }
    val acceptLiveRequestResult: LiveData<CommonResponse>
        get() = repository.acceptLiveRequestLiveData
    fun acceptLiveRequest(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.acceptLiveRequest(map)
        }
    }
    val rejectLiveRequestResult: LiveData<CommonResponse>
        get() = repository.rejectLiveRequestLiveData
    fun rejectLiveRequest(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.rejectLiveRequest(map)
        }
    }
    val getTimeSlotResult: LiveData<GetTimeSlotResponse>
        get() = repository.getTimeSlotLiveData
    fun getTimeSlot(countryId:String,date:String){
        viewModelScope.launch {
            repository.getTimeSlot(countryId,date)
        }
    }
    val addTimeSlotLiveDataResult: LiveData<UpdateTimeSlotResponse>
        get() = repository.addTimeSlotLiveData
    fun addTimeSlot(request:AddTimeSlotRequest) {
        viewModelScope.launch {
            repository.addTimeSlot(request)
        }
    }
    val updateTimeSlotLiveDataResult: LiveData<UpdateTimeSlotResponse>
        get() = repository.updateTimeSlotLiveData
    fun updateTimeSlot(request:AddTimeSlotRequest) {
        viewModelScope.launch {
            repository.updateTimeSlot(request)
        }
    }
    val updateTimeSlotStatusResult: LiveData<UpdateTimeSlotResponse>
        get() = repository.updateTimeSlotStatusLiveData
    fun updateTimeSlotStatus(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.updateTimeSlotStatus(map)
        }
    }
    val deleteTimeSlotResult: LiveData<CommonResponse>
        get() = repository.deleteTimeSlotLiveData
    fun deleteTimeSlot(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.deleteTimeSlot(map)
        }
    }
    val getNewAffiliateUserResult: LiveData<GetNewAffiliateUserResponse>
        get() = repository.newAffiliateUserLiveData
    fun getNewAffiliateUser(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.getNewAffiliateUser(map)
        }
    }
    val getApprovedAffiliateUserResult: LiveData<GetNewAffiliateUserResponse>
        get() = repository.approvedAffiliateUserLiveData
    fun getApprovedAffiliateUser(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.getApprovedAffiliateUser(map)
        }
    }

    val getAssignAffiliateUserResult: LiveData<GetAssignAffiliateUserResponse>
        get() = repository.getAssignAffiliateUserLiveData
    fun getAssignAffiliateUser(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.getAssignAffiliateUser(map)
        }
    }

    val getRejectedAffiliateUserResult: LiveData<GetNewAffiliateUserResponse>
        get() = repository.rejectedAffiliateUserLiveData
    fun getRejectedAffiliateUser(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.getRejectedAffiliateUser(map)
        }
    }
    val getAffiliateUserDetailResult: LiveData<GetAffiliateUserDetailResponse>
        get() = repository.userAffiliateDetailLiveData
    fun getAffiliateUserDetail(userId:String) {
        viewModelScope.launch {
            repository.getAffiliateUserDetail(userId)
        }
    }
    val updateAffiliateUserStatusResult: LiveData<BlockUnblockAffiliateUserResponse>
        get() = repository.updateAffiliateUserStatusLiveData
    fun blockUnblockAffiliateUser(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.blockUnblockAffiliateUser(map)
        }
    }
    val acceptAffiliateUserResult: LiveData<AcceptAffiliateUserResponse>
        get() = repository.acceptAffiliateUserLiveData
    fun acceptAffiliateUser(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.acceptAffiliateUser(map)
        }
    }
    val rejectAffiliateUserResult: LiveData<AcceptAffiliateUserResponse>
        get() = repository.rejectAffiliateUserLiveData
    fun rejectAffiliateUser(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.rejectAffiliateUser(map)
        }
    }
    val getPromotionalBannerResult: LiveData<GetPromotionalBannerResponse>
        get() = repository.getPromotionalBannerLiveData
    fun getPromotionalBanner(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.getPromotionalBanner(map)
        }
    }
    val addPromotionalBannerResult: LiveData<AddPromotionalBannerResponse>
        get() = repository.addPromotionalBannerLiveData
    fun addPromotionalBanner(request: AddPromotionalBannerRequest) {
        viewModelScope.launch {
            repository.addPromotionalBanner(request)
        }
    }
    val updatePromotionalBannerResult: LiveData<AddPromotionalBannerResponse>
        get() = repository.updatePromotionalBannerLiveData
    fun updatePromotionalBanner(request:AddPromotionalBannerRequest) {
        viewModelScope.launch {
            repository.updatePromotionalBanner(request)
        }
    }
    val deletePromotionalBannerResult: LiveData<CommonResponse>
        get() = repository.deletePromotionalBannerLiveData
    fun deletePromotionalBanner(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.deletePromotionalBanner(map)
        }
    }
  val updatePromotionalBannerStatusResult: LiveData<UpdatePromotionalBannerStatusResponse>
        get() = repository.updatePromotionalBannerStatusLiveData
    fun updatePromotionalBannerStatus(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.updatePromotionalBannerStatus(map)
        }
    }
    val updateDefaultShippingFeeResult: LiveData<UpdateDefaultShippingChargeResponse>
        get() = repository.updateDefaultShippingFeeLiveData
    fun updateDefaultShippingFee(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.updateDefaultShippingFee(map)
        }
    }
    val getDefaultShippingFeeResult: LiveData<GetDefaultShippingChargeResponse>
        get() = repository.getDefaultShippingFeeLiveData
    fun getDefaultShippingFee(countryId: String) {
        viewModelScope.launch {
            repository.getDefaultShippingFee(countryId)
        }
    }
    val getAllProductResult: LiveData<GetAllProductListResponse>
        get() = repository.getAllProductLiveData
    fun getAllProduct(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getAllProduct(map)
        }
    }
    val getProductDetailResult: LiveData<GetProductDetailResponse>
        get() = repository.getProductDetailLiveData
    fun getProductDetail(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.getProductDetail(map)
        }
    }
    val updateProductStatusResult: LiveData<UpdateProductStatusResponse>
        get() = repository.updateProductStatusLiveData
    fun updateProductStatus(map:HashMap<String,Any>) {
        viewModelScope.launch {
            repository.updateProductStatus(map)
        }
    }
    val getWithdrawalRequestResult: LiveData<GetWithdrawalRequestResponse>
        get() = repository.getWithdrawalRequestLiveData
    fun getWithdrawalRequest(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.getWithdrawalRequest(map)
        }
    }
    val acceptWithdrawalRequestResult: LiveData<CommonResponse>
        get() = repository.acceptWithdrawalRequestLiveData
    fun acceptWithdrawalRequest(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.acceptWithdrawalRequest(map)
        }
    }
    val rejectWithdrawalRequestResult: LiveData<CommonResponse>
        get() = repository.rejectWithdrawalRequestLiveData
    fun rejectWithdrawalRequest(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.rejectWithdrawalRequest(map)
        }
    }
    val WithdrawalTransferResult: LiveData<CommonResponse>
        get() = repository.WithdrawalTransferLiveData
    fun WithdrawalTransfer(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.WithdrawalTransfer(map)
        }
    }
    val getTermsAndConditionResult: LiveData<GetTermsAndConditionResponse>
        get() = repository.getTermsAndConditionLiveData
    fun getTermsAndCondition(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.getTermsAndCondition(map)
        }
    }
    val createUpdateTermsAndConditionResult: LiveData<GetTermsAndConditionResponse>
        get() = repository.createUpdateTermsAndConditionLiveData
    fun createUpdateTermsAndCondition(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.createUpdateTermsAndCondition(map)
        }
    }
    val getAboutUsResult: LiveData<GetAboutUsResponse>
        get() = repository.getAboutUsLiveData
    fun getAboutUs(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.getAboutUs(map)
        }
    }
    val createUpdateAboutUsResult: LiveData<GetAboutUsResponse>
        get() = repository.createUpdateAboutUsLiveData
    fun createUpdateAboutUs(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.createUpdateAboutUs(map)
        }
    }
    val getCancellationPolicyResult: LiveData<GetCancellationPolicyResponse>
        get() = repository.getCancellationPolicyLiveData
    fun getCancellationPolicy(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.getCancellationPolicy(map)
        }
    }
    val createUpdateCancellationPolicyResult: LiveData<GetCancellationPolicyResponse>
        get() = repository.createUpdateCancellationPolicyLiveData
    fun createUpdateCancellationPolicy(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.createUpdateCancellationPolicy(map)
        }
    }
    val getReturnPolicyResult: LiveData<GetReturnPolicyResponse>
        get() = repository.getReturnPolicyLiveData
    fun getReturnPolicy(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.getReturnPolicy(map)
        }
    }
    val createUpdateReturnPolicyResult: LiveData<GetReturnPolicyResponse>
        get() = repository.createUpdateReturnPolicyLiveData
    fun createUpdateReturnPolicy(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.createUpdateReturnPolicy(map)
        }
    }
    val getExchangePolicyResult: LiveData<GetExchangePolicyResponse>
        get() = repository.getExchangePolicyLiveData
    fun getExchangePolicy(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.getExchangePolicy(map)
        }
    }
    val createUpdateExchangePolicyResult: LiveData<GetExchangePolicyResponse>
        get() = repository.createUpdateExchangePolicyLiveData
    fun createUpdateExchangePolicy(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.createUpdateExchangePolicy(map)
        }
    }
    val getFaqResult: LiveData<GetFaqResponse>
        get() = repository.getFaqLiveData
    fun getFaq(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.getFaq(map)
        }
    }
    val addFaqResult: LiveData<UpdateFaqResponse>
        get() = repository.addFaqLiveData

    fun addFaq(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.addFaq(map)
        }
    }
    val updateFaqResult: LiveData<UpdateFaqResponse>
        get() = repository.updateFaqLiveData
    fun updateFaq(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.updateFaq(map)
        }
    }

    val deleteFaqResult: LiveData<CommonResponse>
        get() = repository.deleteFaqLiveData
    fun deleteFaq(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.deleteFaq(map)
        }
    }
    val getContactUsResult: LiveData<GetContactResponse>
        get() = repository.getContactUsLiveData
    fun getContactUs(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.getContactUs(map)
        }
    }
    val createUpdateContactUsResult: LiveData<CommonResponse>
        get() = repository.createUpdateContactUsLiveData
    fun createUpdateContactUs(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.createUpdateContactUs(map)
        }
    }

    val getMyNotificationResult: LiveData<CommonResponse>
        get() = repository.getMyNotificationLiveData
    fun getMyNotification(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.getMyNotification(map)
        }
    }

    val readNotificationResult: LiveData<CommonResponse>
        get() = repository.readNotificationLiveData
    fun readNotification(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.readNotification(map)
        }
    }

    val allReadNotificationResult: LiveData<CommonResponse>
        get() = repository.allReadNotificationLiveData
    fun allReadNotification(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.allReadNotification(map)
        }
    }

    val getSendNotificationResult: LiveData<GetSendNotificationListResponse>
        get() = repository.getSendNotificationLiveData
    fun getSendNotification(map: HashMap<String, Any>) {
        viewModelScope.launch {
            repository.getSendNotification(map)
        }
    }

    val getNotificationCountResult: LiveData<GetNotificationCountResponse>
        get() = repository.getNotificationCountLiveData
    fun getNotificationCount() {
        viewModelScope.launch {
            repository.getNotificationCount()
        }
    }

    val sendNotificationResult: LiveData<SendNotificationResponse>
        get() = repository.sendNotificationLiveData
    fun sendNotification(request: SendNotificationRequest) {
        viewModelScope.launch {
            repository.sendNotification(request)
        }
    }

}
