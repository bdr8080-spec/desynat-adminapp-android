package com.desynatadmin.data.repo


import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.desynatadmin.network.ApiServices
import com.desynatadmin.ui.auth.LoginResponse
import com.desynatadmin.ui.home.brandManagement.response.FileUploadResponse
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.brandManagement.response.UpdateBrandStatusResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetCategoryResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.UpdateCategoryResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.UpdateSubCategoryResponse
import com.desynatadmin.ui.home.response.CommonResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.AddPlanRequest
import com.desynatadmin.ui.home.subscriptionPlan.response.GetAllPlanResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.UpdatePlanStatusResponse
import com.desynatadmin.utils.toMultipartPart
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MultipartBody
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
    suspend fun editSubCategory(map:HashMap<String,RequestBody>
    ) {
        handleApiCall(
            api = {
                api.editSubCategory(map)
            },
            responseLiveData = updateSubCategoryLiveData
        )
    }

    val deleteBrandLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun deleteBrand(map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.deleteBrand(map)
            },
            responseLiveData = deleteBrandLiveData
        )
    }

    val deleteSubCategoryLiveData = SingleLiveEvent<CommonResponse>()
    suspend fun deleteSubCategory(map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.deleteSubCategory(map)
            },
            responseLiveData = deleteSubCategoryLiveData
        )

    }


    val updateBrandStatusLiveData = SingleLiveEvent<UpdateBrandStatusResponse>()
    suspend fun updateBrandStatus(map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.updateBrandStatus(map)
            },
            responseLiveData = updateBrandStatusLiveData
        )
    }

    val updateSubCategoryStatusLiveData = SingleLiveEvent<UpdateSubCategoryResponse>()
    suspend fun updateSubCategoryStatus(map: HashMap<String, Any>
    ) {
        handleApiCall(
            api = {
                api.updateSubCategoryStatus(map)
            },
            responseLiveData = updateSubCategoryStatusLiveData
        )

    }

    val updateCategoryStatusLiveData = SingleLiveEvent<UpdateCategoryResponse>()
    suspend fun updateCategoryStatus(map: HashMap<String, Any>
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
            responseLiveData = editBrandLiveData
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

