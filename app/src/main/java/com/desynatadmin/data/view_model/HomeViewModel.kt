package com.desynatadmin.data.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desynatadmin.data.repo.MainRepository
import com.desynatadmin.ui.auth.LoginResponse
import com.desynatadmin.ui.home.brandManagement.response.AddServiceCityResponse
import com.desynatadmin.ui.home.brandManagement.response.AddZoneRequest
import com.desynatadmin.ui.home.brandManagement.response.AddZoneResponse
import com.desynatadmin.ui.home.brandManagement.response.CityResponse
import com.desynatadmin.ui.home.brandManagement.response.CountryREsponse
import com.desynatadmin.ui.home.brandManagement.response.FileUploadResponse
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.brandManagement.response.ServiceCityListResponse
import com.desynatadmin.ui.home.brandManagement.response.UpdateBrandStatusResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetCategoryResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.GetSubCategoryResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.UpdateCategoryResponse
import com.desynatadmin.ui.home.categorySubCategoryManagement.response.UpdateSubCategoryResponse
import com.desynatadmin.ui.home.response.CommonResponse
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

    val getCountryResult: LiveData<CountryREsponse>
        get() = repository.getCountryData

    fun hitCountryList() {
        viewModelScope.launch {
            repository.hitCountyList()
        }
    }

    val getServiceCityList: LiveData<ServiceCityListResponse>
        get() = repository.getServiceListData

    fun hitServiceCityList(
        token: String,
        country_id: String,
        limit: String,
        search: String
    ) {
        viewModelScope.launch {
            repository.hitServiceCityList(
                token,
                country_id,
                limit,
                search
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

    val getChangeCityStatus : LiveData<CommonResponse>
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


    val getNewSellerList : LiveData<AddZoneResponse>
        get() = repository.getNewSellerList

    fun hitNewSellerList(
        token : String,
        from_date : String,
        to_date : String,
        limit : String,
        search : String,
    ) {
        viewModelScope.launch {
            repository.
            hitNewSellerList(token,from_date, to_date,limit,search)
        }
    }

    val getApprovedSellerList : LiveData<AddZoneResponse>
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


    val getSellerDetailData : LiveData<AddZoneResponse>
        get() = repository.getSellerDetail

    fun hitRejectedSellerList(
        token : String,
        seller_id : String,

    ) {
        viewModelScope.launch {
            repository.
            hitSellerDetail(token,seller_id)
        }
    }


}
