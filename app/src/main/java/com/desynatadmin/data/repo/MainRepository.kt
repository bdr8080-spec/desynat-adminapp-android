package com.desynatadmin.data.repo


import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.desynatadmin.network.ApiServices
import com.desynatadmin.ui.auth.LoginResponse
import com.desynatadmin.ui.home.brandManagement.response.AcceptSellerResponse
import com.desynatadmin.ui.home.brandManagement.response.AddServiceCityResponse
import com.desynatadmin.ui.home.brandManagement.response.AddZoneRequest
import com.desynatadmin.ui.home.brandManagement.response.AddZoneResponse
import com.desynatadmin.ui.home.brandManagement.response.ApprovedSellerResponse
import com.desynatadmin.ui.home.brandManagement.response.ChangeCityStatusResponse
import com.desynatadmin.ui.home.brandManagement.response.CityResponse
import com.desynatadmin.ui.home.brandManagement.response.CountryREsponse
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
import com.desynatadmin.ui.home.reasonManagement.response.BlockUnblockReasonResponse
import com.desynatadmin.ui.home.reasonManagement.response.GetReasonResponse
import com.desynatadmin.ui.home.reasonManagement.response.UpdateReasonResponse
import com.desynatadmin.ui.home.response.CommonResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.AddPlanRequest
import com.desynatadmin.ui.home.subscriptionPlan.response.GetAllPlanResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.UpdatePlanStatusResponse
import com.desynatadmin.ui.home.userManagement.response.AddWalletBalanceResponse
import com.desynatadmin.ui.home.userManagement.response.BlockUnblockUserResponse
import com.desynatadmin.ui.home.userManagement.response.GetUserDetailResponse
import com.desynatadmin.ui.home.userManagement.response.GetUserResponse
import com.desynatadmin.utils.toMultipartPart
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.RequestBody
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
    suspend fun getBrand(search:String?=null, page:Int
    ) {
        handleApiCall(
            api = {
                api.getBrand(search,page)
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


    val getCountryData = SingleLiveEvent<CountryREsponse>()
    suspend fun hitCountyList() {
        handleApiCall(
            api = {
                api.getCountry()
            },
            responseLiveData = getCountryData
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
    suspend fun getCountryy(
    ) {
        handleApiCall(
            api = {
                api.getCountryy()
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

