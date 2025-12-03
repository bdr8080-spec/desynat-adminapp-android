package com.desynatadmin.data.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desynatadmin.data.repo.MainRepository
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
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


    val logoutResult: LiveData<CommonResponse>
        get() = repository.logoutLiveData

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    val getAllBrandResult: LiveData<GetBrandResponse>
        get() = repository.brandLiveData

    fun getAllBrand(search: String?=null, page:Int) {
        viewModelScope.launch {
            repository.getBrand(search,page)
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
        token : String,
        country_id : String,
        from_date : String,
        to_date : String,
        limit : String,
        search : String,
    ) {
        viewModelScope.launch {
            repository.
            hitApprovedSellerList(token,country_id,from_date, to_date,limit,search)
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
    ) {
        viewModelScope.launch {
            repository.
            hitRejectedSellerList(token,country_id,from_date, to_date,limit,search)
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

    fun getAllUser(search: String?=null, page:Int,countryId:String?=null,fromDate:String?=null,toDate:String?=null) {
        viewModelScope.launch {
            repository.getAllUser(search,page,countryId,fromDate,toDate)
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

    fun getDeliveryBoy(search: String?=null, page:Int,fromDate:String?=null,toDate:String?=null) {
        viewModelScope.launch {
            repository.getDeliveryBoy(search,page,fromDate,toDate)
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

    fun getPromoCode(search: String?=null,fromDate:String?=null,toDate:String?=null) {
        viewModelScope.launch {
            repository.getPromoCode(search,fromDate,toDate)
        }
    }

    val addPromoCodeResult: LiveData<CommonResponse>
        get() = repository.addPromoCodeLiveData

    fun addPromoCode(request: AddPromoCodeRequest) {
        viewModelScope.launch {
            repository.addPromoCode(request)
        }
    }

    val updatePromoCodeResult: LiveData<CommonResponse>
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

    val editGiftCardResult: LiveData<CommonResponse>
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

    val getShippingChargeResult: LiveData<GetBrandResponse>
        get() = repository.shippingChargeLiveData

    fun getShippingCharge(page:Int) {
        viewModelScope.launch {
            repository.getShippingCharge(page)
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


}
