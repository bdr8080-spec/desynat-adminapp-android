package com.desynatadmin.ui.home.giftCardManagement

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.desynatadmin.R
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentGiftCardDetailBinding
import com.desynatadmin.databinding.FragmentGiftCardManagementBinding
import com.desynatadmin.ui.home.HomeActivity
import com.desynatadmin.ui.home.brandManagement.response.GetBrandResponse
import com.desynatadmin.ui.home.giftCardManagement.response.GetAllGiftCardResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.Constants
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GiftCardDetailFragment : BaseFragment() {
    private var binding: FragmentGiftCardDetailBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    var giftData: GetAllGiftCardResponse.Data.Data?=null
    var actionAccess : Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGiftCardDetailBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("updatedGiftCard")
            ?.observe(viewLifecycleOwner) { json ->
                json?.let {
                    giftData = Gson().fromJson(json, GetAllGiftCardResponse.Data.Data::class.java)
                    setData()
                    updateStatus()
                }
            }


        ProgressDialogUtils.getInstance().hideProgress()
        val itemJson = arguments?.getString("giftCard")
        if (!itemJson.isNullOrEmpty()) {
            giftData = Gson().fromJson(itemJson, GetAllGiftCardResponse.Data.Data::class.java)
            setData()
            updateStatus()
        }
        binding?.apply {
            toolbar.tvToolbar.text = getString(R.string.gift_card_detail)
            val userInfo = SharedPreference.get(requireActivity()).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(requireActivity()).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("gift_card_management", true)
                    }

                actionAccess = module?.canAction == 1

                btnDelete.isVisible = actionAccess
                btnEdit.isVisible = actionAccess
                tvSwitch.isClickable = actionAccess
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnDelete.setOnClickListener {
                CommonMethod.deleteAlertDialog(
                    requireActivity(),
                    getString(R.string.are_you_sure_you_want_to_delete_the_gift_card_category)
                ) { action ->
                    when (action) {
                        CommonMethod.DialogAction.DELETE -> {
                            deleteGiftCard()
                        }

                        CommonMethod.DialogAction.DISCARD -> {

                        }
                    }
                }

            }

            btnEdit.setOnClickListener {
                val bundle = Bundle().apply {
                    putString("giftData", Gson().toJson(giftData))
                }
                findNavController().navigate(R.id.action_giftCardDetailDetail_to_addGiftCardFragment,bundle)
            }

            tvSwitch.setOnClickListener {
                if (actionAccess){
                    updateGiftCardStatus()
                }

            }

        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()

                isObserver = false
            }
        }


        viewModel.updateGiftCardStatusLiveData.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                giftData = response.data
                if (response.data.status==1){
                    showToast(getString(R.string.gift_card_category_activated_successfully))
                }
                else{
                    showToast(getString(R.string.gift_card_category_deactivated_successfully))

                }
                updateStatus()
                isObserver = false

            }
        }

        viewModel.deleteGiftCardResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(requireContext(),
                    getString(R.string.gift_card_category_deleted_successfully)){
                    findNavController().popBackStack()
                }
                isObserver = false
            }
        }
    }

    private fun deleteGiftCard(){
        val map = HashMap<String,Any>()
        map["giftCard_id"]=giftData?.id.toString()
        viewModel.deleteGiftCard(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun updateGiftCardStatus(){
        val map = HashMap<String,Any>()
        map["giftCard_id"]=giftData?.id.toString()
        viewModel.updateGiftCardStatus(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun updateStatus(){
        binding?.apply {
            if(giftData?.status==1){
                tvSwitch.text = getString(R.string.active)
                tvSwitch.setTextColor(Color.parseColor("#1A900D"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_on, 0)
            }
            else{
                tvSwitch.text = getString(R.string.inactive)
                tvSwitch.setTextColor(Color.parseColor("#666666"))
                tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.switch_off, 0)

            }

        }
    }

    private fun setData(){

        binding?.apply {

            tvGiftCardName.text =  if (Constants.language=="ar") giftData?.nameArabic else giftData?.nameEnglish
            updateStatus()

            ivGiftCard.load(giftData?.image) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }
        }

    }

}