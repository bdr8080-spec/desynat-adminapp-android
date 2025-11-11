package com.desynatadmin.data.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desynatadmin.data.repo.MainRepository
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




    val subscriptionPlanResult: LiveData<GetAllPlanResponse>
        get() = repository.subscriptionPlanLiveData

    fun getSubscriptionPlan(countryId:String?=null) {
        viewModelScope.launch {
            repository.getSubscriptionPlan(countryId)
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


}
