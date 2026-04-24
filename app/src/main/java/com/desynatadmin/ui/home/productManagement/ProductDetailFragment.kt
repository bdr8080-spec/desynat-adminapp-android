package com.desynatadmin.ui.home.productManagement

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.bumptech.glide.Glide
import com.desynatadmin.R
import com.desynatadmin.adapter.ProductImageAdapter
import com.desynatadmin.adapter.ProductRectangleColorAdapter
import com.desynatadmin.adapter.SizeAdapter
import com.desynatadmin.adapter.SpecificationAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.FragmentProductDetailBinding
import com.desynatadmin.databinding.SizeGuideChartDialogBinding
import com.desynatadmin.ui.home.productManagement.response.GetAllProductListResponse
import com.desynatadmin.ui.home.productManagement.response.GetProductDetailResponse
import com.desynatadmin.ui.home.productManagement.response.ProductArrangeResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.SharedPreference
import com.desynatadmin.utils.showToast
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ProductDetailFragment : BaseFragment() {
    private var binding: FragmentProductDetailBinding?=null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true
    private var specificationList = ArrayList<GetProductDetailResponse.Data.ProductSpecification>()

    private var productImageList = ArrayList<String>()
    private var colorAdapter: ProductRectangleColorAdapter?=null
    private var sizeAdapter: SizeAdapter?=null
    private var specificationAdapter: SpecificationAdapter?=null
    private var productImageAdapter: ProductImageAdapter?=null
    private var productData: GetAllProductListResponse.Data.Data?=null
    private var productDetailData: GetProductDetailResponse.Data?=null
    private var productInfo = ArrayList<ProductArrangeResponse.Product>()
    private var selectedColorPos = 0
    private var selectedSizePos = 0
    private var selectedSizeList = ArrayList<ProductArrangeResponse.Product.ProductVariation>()
    private var selectedColorList = ArrayList<ProductArrangeResponse.Product>()
    private var actionAccess: Boolean = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductDetailBinding.inflate(layoutInflater,container,false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initControl()
        observe()
    }

    override fun initView() {
        binding?.apply {
            val userInfo = SharedPreference.get(requireActivity()).getUser()
            if (userInfo?.type=="2"){
                val subAdminInfo = SharedPreference.get(requireActivity()).getSubAdmin()
                val module = subAdminInfo?.permissionDetails
                    ?.firstOrNull {
                        it.moduleDetails.name.equals("product_management", true)
                    }

                actionAccess = module?.canAction == 1

                tvSwitch.isClickable = actionAccess
            }

            toolbar.tvToolbar.text = "Product Details"
            val itemJson = arguments?.getString("productData")
            if (!itemJson.isNullOrEmpty()) {
                productData = Gson().fromJson(itemJson, GetAllProductListResponse.Data.Data::class.java)
                updateStatus()
            }

            productImageAdapter = ProductImageAdapter(requireActivity(),productImageList,object :
                ProductImageAdapter.OnClick{
                override fun onClick(position: Int) {
                    CommonMethod.imagePreviewListDialog(requireActivity(),productImageList)
                }

            })
            viewPager.adapter = productImageAdapter
            TabLayoutMediator(dotsIndicator, viewPager) { _, _ -> }.attach()

            sizeAdapter = SizeAdapter(requireActivity(),selectedSizeList,object : SizeAdapter.OnClick{
                override fun onClickItem(position: Int) {
                    selectedSizePos = position
                    setData()
                }

            })
            rvSize.adapter = sizeAdapter

            colorAdapter = ProductRectangleColorAdapter(requireActivity(),selectedColorList,object :
                ProductRectangleColorAdapter.OnClick{
                override fun onClickItem(position: Int) {
                    selectedColorPos = position
                    selectedSizePos = 0
                    sizeAdapter?.updateData(selectedSizePos)
                    setData()
                }

            })
            rvColor.adapter = colorAdapter

            specificationAdapter = SpecificationAdapter(requireActivity(),specificationList)
            rvSpecification.adapter = specificationAdapter

        }

        getProductDetail()

    }

    override fun initControl() {
        binding?.apply {
            toolbar.ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            tvSwitch.setOnClickListener {
                if (actionAccess) {
                    updateProductStatus()
                }
            }

            tvSizeGuide.setOnClickListener {
                sizeGuideDialog()
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
        viewModel.getProductDetailResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                binding?.mainProgressBar?.isVisible = false
                ProgressDialogUtils.getInstance().hideProgress()
                productDetailData = response.data
                productDetailData?.productVariants?.let { mapVariantsToProducts(it) }?.let { productInfo.addAll(it) }
                setData()
                isObserver = false
            }
        }

        viewModel.updateProductStatusResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                binding?.mainProgressBar?.isVisible = false
                ProgressDialogUtils.getInstance().hideProgress()
                showToast(response.message)
                productDetailData?.productActiveStatus = response.data.productActiveStatus
                productData?.productActiveStatus = response.data.productActiveStatus
                updateStatus()
                isObserver = false
            }
        }
    }

    private fun getProductDetail(){
        val map = HashMap<String,Any>()
        map["id"] = productData?.id.toString()
        viewModel.getProductDetail(map)
        binding?.mainProgressBar?.isVisible = true
        isObserver = true
    }

    private fun updateProductStatus(){
        val map = HashMap<String,Any>()
        map["id"] = productData?.id.toString()
        viewModel.updateProductStatus(map)
        binding?.mainProgressBar?.isVisible = true
        isObserver = true
    }

    private fun setData() {
        binding?.apply {
            specificationList.clear()
            productDetailData?.productSpecifications?.let { specificationList.addAll(it) }
            specificationAdapter?.notifyDataSetChanged()
            selectedSizeList.clear()
            selectedSizeList.addAll(productInfo[selectedColorPos].productVariation)
            productInfo[selectedColorPos].productVariation.let {

                sizeAdapter?.notifyDataSetChanged()

                selectedColorList.clear()

                productInfo.let { selectedColorList.addAll(it) }
                colorAdapter?.notifyDataSetChanged()

                productImageList.clear()
                productInfo[selectedColorPos]?.variantImage?.let {
                    productImageList.addAll(
                        it
                    )
                }
                productImageAdapter?.notifyDataSetChanged()
                viewPager.setCurrentItem(0, false)

                tvBrand.text = if (productDetailData?.brand?.nameEnglish.toString() == "Other") productDetailData?.otherEn.toString() else productDetailData?.brand?.nameEnglish.toString()
                tvName.text = productDetailData?.productNameEn
                tvBrandName.text = "${productDetailData?.productNameEn} (by ${productDetailData?.sellerDetails?.firstName.toString()} ${productDetailData?.sellerDetails?.lastName.toString()})"

                tvStore.text =
                    productDetailData?.store?.storeName + " (${productDetailData?.category?.name})"

                val rating = productDetailData?.averageRating?.product?.toFloatOrNull() ?: 0f
                ratingBar.rating = rating
                tvRating.text = "(${productDetailData?.averageRating?.product})"

                tvPrice.text =
                    "${(productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.actualPrice ?: 0)} KD"
                tvDiscountedPrice.text =
                    "${(productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.finalPrice ?: 0)} KD"

                val variant = productInfo[selectedColorPos].productVariation?.get(selectedSizePos)
                val discountValue = variant?.discountValue ?: 0
                val discountType = if (variant?.discountType == "1") "% off" else " KD off"
                tvOffer.text = "$discountValue$discountType"

                tvAddedOn.text = "${CommonMethod.customizeChangeDateFormat(productDetailData?.createdAt.toString(), outputFormat = "dd/MM/yyyy")}"
                tvDescription.text = productDetailData?.descriptionEn

                if (productDetailData?.productReturn == "1") {
                    tvReturnTime.text = "${productDetailData?.returnDays} days"
                }
                if (productDetailData?.productReturn == "1") {
                    tvExchangeTime.text = "${productDetailData?.exchangableDays} days"
                }
                tvRatingAndReviewStatus.text =
                    if (productDetailData?.reviewRating == "1") "Yes" else "No"
                tvAffiliatedProduct.text =
                    if (productDetailData?.affiliatedProduct == "1") "Yes" else "No"
                tvCustomizedStatus.text =
                    if (productDetailData?.customizedProduct == "1") "Yes" else "No"
                tvReadyToShipStatus.text =
                    if (productDetailData?.readyToShip == "1") "Yes" else "No"
                tvCashOnDeliveryStatus.text =
                    if (productDetailData?.cashOnDelivery == "1") "Yes" else "No"

                tvMinCartQuantity.text = productDetailData?.minCardQuantity
                tvMaxCartQuantity.text = productDetailData?.maxCardQuantity

                if (productDetailData?.category?.name == "Women") {
                    clWomenSize.isVisible = true
                    tvBustSize.text =
                        productInfo[selectedColorPos]?.productVariation?.get(selectedSizePos)?.bust
                            ?: "NA"
                    tvHipSize.text =
                        productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.hip
                            ?: "NA"
                    tvShoulderSize.text =
                        productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.shoulder
                            ?: "NA"
                    tvWaistSize.text =
                        productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.waist
                            ?: "NA"
                    tvSleeveLengthSize.text =
                        productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.sleeveLength
                            ?: "NA"
                    tvLengthSize.text =
                        productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.length
                            ?: "NA"

                } else {
                    clWomenSize.isVisible = false
                }

                tvTotalStocksValue.text =
                    productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.totalStock
                tvSkuValue.text =
                    productInfo[selectedColorPos].productVariation?.get(selectedSizePos)?.sku

                tvSellerName.text =
                    productDetailData?.sellerDetails?.firstName + " " + productDetailData?.sellerDetails?.lastName
                tvStoreName.text = productDetailData?.store?.storeName
                tvLocation.text = productDetailData?.store?.countryInfo?.name
                Glide.with(requireActivity()).load(productDetailData?.store?.storeImage)
                    .placeholder(R.drawable.ic_placeholder).into(ivProfileImage)
            }
        }
    }

    private fun updateStatus(){
        binding?.apply {
                if(productData?.productActiveStatus==1){
                    tvSwitch.text = "Active"
                    tvSwitch.setTextColor(Color.parseColor("#1A900D"))
                    tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.switch_on,0 )
                }
                else{
                    tvSwitch.text = "Inactive"
                    tvSwitch.setTextColor(Color.parseColor("#875C3A"))
                    tvSwitch.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.switch_off,0)
                }
        }

    }

    fun mapVariantsToProducts(
        variants: ArrayList<GetProductDetailResponse.Data.ProductVariant>
    ): ArrayList<ProductArrangeResponse.Product> {

        return ArrayList(
            variants
                .groupBy { it.colorId }   // group by color_id
                .map { (colorId, listByColor) ->

                    val images = ArrayList(listByColor.first().variantImage)

                    val variationList = ArrayList(
                        listByColor.map { variant ->
                            ProductArrangeResponse.Product.ProductVariation(
                                id = variant.id.toString(),
                                productId = variant.productId.toString(),
                                sizeId = variant.sizeId.toString(),
                                actualPrice = variant.actualPrice,
                                discountType = variant.discountType,
                                discountValue = variant.discountValue,
                                finalPrice = variant.finalPrice,
                                totalStock = variant.totalStock,
                                sku = variant.sku,
                                skuThresholdValue = variant.skuThresholdValue ?: "0",
                                bust = variant.bust,
                                waist = variant.waist,
                                hip = variant.hip,
                                shoulder = variant.shoulder,
                                sleeveLength = variant.sleeveLength,
                                length = variant.length,
                                status = variant.status,
                                createdAt = variant.createdAt,
                                updatedAt = variant.updatedAt,
                                size = variant.size
                            )
                        }
                    )

                    ProductArrangeResponse.Product(
                        colorId = colorId,
                        variantImage = images,
                        productVariation = variationList,
                        productId = listByColor.first().productId.toString(),
                        color = listByColor.first().color
                    )
                }
        )
    }

    private fun sizeGuideDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = SizeGuideChartDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog.setContentView(it) }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.BOTTOM
        window.attributes = params

        dialogBinding.apply {

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            ivSizeChart.load(productData?.uploadSizeChart) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder)
                error(R.drawable.ic_placeholder)
            }

        }

        dialog.show()
    }

}