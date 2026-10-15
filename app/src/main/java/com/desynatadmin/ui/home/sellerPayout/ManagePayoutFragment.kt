package com.desynatadmin.ui.home.sellerPayout

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.desynatadmin.R
import com.desynatadmin.adapter.SellerPayoutHistoryAdapter
import com.desynatadmin.base.BaseFragment
import com.desynatadmin.data.view_model.HomeViewModel
import com.desynatadmin.databinding.AddPayoutDialogBinding
import com.desynatadmin.databinding.FragmentManagePayoutBinding
import com.desynatadmin.ui.home.sellerPayout.response.GetSellerPayoutHistoryResponse
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.net.toUri

@AndroidEntryPoint
class ManagePayoutFragment : BaseFragment() {
    private var binding: FragmentManagePayoutBinding? = null
    private val viewModel: HomeViewModel by viewModels()
    private var isObserver = true

    private var sellerId: Int = 0
    private var sellerName: String = ""
    private var currentPage = 1
    private var lastPage = 1
    private var totalItem = 0
    private var pageLimit = 10
    private var isFromSwipe = false
    private var pagination = false

    private var sellerPayoutHistoryAdapter: SellerPayoutHistoryAdapter? = null
    private var payoutHistoryList = ArrayList<GetSellerPayoutHistoryResponse.Data.PayoutHistory.PayoutHistoryItem>()
    private var currentPendingAmount: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManagePayoutBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sellerId = arguments?.getInt("seller_id", 0) ?: 0
        sellerName = arguments?.getString("seller_name", "") ?: ""
        Log.e("TAG", "sellerName:$sellerName ")
        if (sellerId == 0) {
            sellerId = arguments?.getString("seller_id")?.toIntOrNull() ?: 0
        }
        initView()
        initControl()
        observe()
        getPayoutHistoryList(true)
    }

    override fun initView() {
        ProgressDialogUtils.getInstance().hideProgress()


        binding?.apply {
            if (sharedPreference.language=="ar"){
                tvAmount.gravity==Gravity.RIGHT
            }else{
                tvAmount.gravity==Gravity.LEFT
            }
            toolbar.tvToolbar.text = getString(R.string.seller_payout)

            sellerPayoutHistoryAdapter = SellerPayoutHistoryAdapter(
                requireActivity(),
                payoutHistoryList,
                object : SellerPayoutHistoryAdapter.OnClick {
                    override fun OnItemClick(position: Int) {
                    }
                }
            )
            rvPayoutHistory.adapter = sellerPayoutHistoryAdapter

            swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), R.color.brown)
            )

            swipeRefreshLayout.setOnRefreshListener {
                isFromSwipe = true
                pagination = false
                currentPage = 1
                getPayoutHistoryList(false)
            }
        }
    }

    override fun initControl() {
        binding?.apply {
            toolbar.tvToolbar.setOnClickListener {
                findNavController().popBackStack()
            }

            btnAddPayout.setOnClickListener {
                addPayoutDialog()
            }

            ivNextArrow.setOnClickListener {
                if (!isObserver && currentPage < lastPage && totalItem != 0) {
                    currentPage += 1
                    getPayoutHistoryList(false)
                    binding?.progressBar?.isVisible = true
                }
            }

            ivBackArrow.setOnClickListener {
                if (currentPage > 1) {
                    currentPage--
                    getPayoutHistoryList(false)
                    binding?.progressBar?.isVisible = true
                }
            }
        }
    }

    override fun observe() {
        viewModel.error.observe(viewLifecycleOwner) {
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe) {
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }

        viewModel.addPayoutResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                CommonMethod.showSuccessDialog(
                    requireActivity(),
                    response.message
                ) {
                    currentPage = 1
                    pagination = false
                    getPayoutHistoryList(true)
                }
                isObserver = false
            }
        }

        viewModel.getPayoutHistoryListResult.observe(viewLifecycleOwner) { response ->
            if (isObserver) {
                ProgressDialogUtils.getInstance().hideProgress()
                binding?.progressBar?.isVisible = false
                if (isFromSwipe) {
                    binding?.swipeRefreshLayout?.isRefreshing = false
                }

                val historyData = response.data?.payoutHistory

                val pendingAmt = response.data?.pendingAmount?.toDoubleOrNull() ?: 0.0
                currentPendingAmount = pendingAmt
                binding?.tvAmount?.text = "${CommonMethod.formatPrice(pendingAmt)} KD"

                currentPage = historyData?.currentPage ?: 1
                totalItem = historyData?.total ?: 0
                lastPage = historyData?.lastPage ?: 1

                val currentStr = if (currentPage < 10) "0$currentPage" else "$currentPage"
                val lastStr = if (lastPage < 10) "0$lastPage" else "$lastPage"

                binding?.tvCount?.text = "$currentStr ${getString(R.string.of)} $lastStr"

                if (currentPage < lastPage) {
                    binding?.ivNextArrow?.setImageResource(R.drawable.ic_next_brand)
                } else {
                    binding?.ivNextArrow?.setImageResource(R.drawable.ic_next_brand_disable)
                }

                if (currentPage > 1) {
                    binding?.ivBackArrow?.setImageResource(R.drawable.ic_back_brand)
                } else {
                    binding?.ivBackArrow?.setImageResource(R.drawable.ic_back_brand_disable)
                }

                val oldSize = payoutHistoryList.size
                if (!pagination) {
                    payoutHistoryList.clear()
                }

                val newItems = historyData?.data ?: emptyList()
                payoutHistoryList.addAll(newItems)
                checkData()

                if (pagination) {
                    historyData?.data?.size?.let {
                        sellerPayoutHistoryAdapter?.notifyItemRangeInserted(
                            oldSize,
                            it
                        )
                    }
                } else {
                    sellerPayoutHistoryAdapter?.notifyDataSetChanged()
                }

                pagination = false
                isFromSwipe = false
                isObserver = false
            }
        }
    }

    private fun getPayoutHistoryList(loading: Boolean = true) {
        val map = HashMap<String, Any>()
        map["seller_id"] = sellerId
        map["page"] = currentPage
        map["limit"] = pageLimit
        viewModel.getPayoutHistoryList(map)
        if (loading) {
            ProgressDialogUtils.getInstance().showProgress(requireActivity())
        }
        isObserver = true
    }

    private fun checkData() {
        binding?.tvNotData?.isVisible = payoutHistoryList.isEmpty()
    }

    private fun addPayout(amount: String, transactionId: String) {
        val map = HashMap<String, Any>()
        map["seller_id"] = sellerId
        map["amount"] = amount.toDoubleOrNull() ?: amount
        map["transaction_id"] = transactionId
        viewModel.addPayout(map)
        ProgressDialogUtils.getInstance().showProgress(requireActivity())
        isObserver = true
    }

    private fun addPayoutDialog() {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        val dialogBinding = AddPayoutDialogBinding.inflate(LayoutInflater.from(context))
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
            etAmount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(editable: Editable?) {
                    val text = editable.toString()

                    if (text.length > 1 && text.startsWith("0") && !text.startsWith("0.")) {
                        val newValue = text.trimStart('0')

                        etAmount.removeTextChangedListener(this)
                        etAmount.setText(newValue)
                        etAmount.setSelection(newValue.length)
                        etAmount.addTextChangedListener(this)
                    }
                }
            })

            tvToolbar.setOnClickListener {
                dialog.dismiss()
            }

            btnSubmit.setOnClickListener {
                if (isInvalid(etAmount, "Amount")) {
                    return@setOnClickListener
                } else {
                    val amountText = etAmount.text?.trim().toString()
                    val amountValue = amountText.toDoubleOrNull() ?: 0.0

                    if (amountValue > currentPendingAmount) {
                        showToast(getString(R.string.amount_cannot_be_greater_than_pending_amount))
                        return@setOnClickListener
                    }

                    dialog.dismiss()
                    initiateMyFatoorahPayment(amountValue)
                }
            }
        }

        dialog.show()
    }
    companion object {
        private const val TAG = "MyFatoorah"
        private const val SUCCESS_URL = "https://desynat.com/success"
        private const val ERROR_URL = "https://desynat.com/error"

        // Set to true for Production / Live, false for Sandbox / Test
        private const val IS_LIVE = true

        private const val TEST_AUTH = "Bearer SK_KWT_vVZlnnAqu8jRByOWaRPNId4ShzEDNt256dvnjebuyzo52dXjAfRx2ixW5umjWSUx"
        private const val LIVE_AUTH = "Bearer SK_KWT_Xn6nYzfdgF3Lz7TKhrGLhruKiBT5OgesD080ZIJohon0q0CEqCRrwdAsV0qICIsx"

        private val AUTH = if (IS_LIVE) LIVE_AUTH else TEST_AUTH
        private val SEND_PAYMENT_URL = if (IS_LIVE) "https://api.myfatoorah.com/v2/SendPayment" else "https://apitest.myfatoorah.com/v2/SendPayment"
    }

    private fun initiateMyFatoorahPayment(amount: Double) {
        ProgressDialogUtils.getInstance().showProgress(requireActivity())

        val name = sellerName.ifBlank { "" }
        val request = com.desynatadmin.ui.home.sellerPayout.response.SendPaymentRequest(
            customerName = name,
            notificationOption = "LNK",
            invoiceValue = java.math.BigDecimal(amount)
                .setScale(3, java.math.RoundingMode.HALF_UP).toDouble(),
            displayCurrencyIso = "KWD",
            callBackUrl = SUCCESS_URL,
            errorUrl = ERROR_URL,
//            customerReference = "payout_${System.currentTimeMillis()}"
        )
       Log.d(TAG, "SendPayment request: ${com.google.gson.Gson().toJson(request)}")

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = com.desynatadmin.payoutRetrofit.PayoutRetrofit.api.sendPaymentLink(
                    AUTH,
                    SEND_PAYMENT_URL,
                    request
                )
                ProgressDialogUtils.getInstance().hideProgress()
               Log.d(
                    TAG,
                    "SendPayment HTTP ${response.code()} body: ${com.google.gson.Gson().toJson(response.body())}"
                )

                if (response.isSuccessful) {
                    if (response.body()?.isSuccess == true) {
                        val invoiceUrl = response.body()?.data?.invoiceURL
                        if (!invoiceUrl.isNullOrEmpty()) {
                            openWebViewDialog(invoiceUrl, amount.toString())
                        } else {
                            showToast("Failed to get payment link: invoiceURL is null")
                        }
                    } else {
                        showToast("API Error: ${response.body()?.message}")
                      Log.e(TAG, "SendPayment failed: ${com.google.gson.Gson().toJson(response.body())}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: response.message()
                  Log.e(TAG, "SendPayment HTTP error ${response.code()}: $errorBody")
                    showToast("HTTP Error: $errorBody")
                }
            } catch (e: Exception) {
                ProgressDialogUtils.getInstance().hideProgress()
              Log.e(TAG, "SendPayment exception", e)
                showToast("Error: ${e.message}")
            }
        }
    }

    @android.annotation.SuppressLint("SetJavaScriptEnabled")
    private fun openWebViewDialog(url: String, amount: String) {
        val dialog = Dialog(requireActivity(), R.style.CustomDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_myfatoorah_webview)

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val ivClose = dialog.findViewById<android.widget.ImageView>(R.id.ivClose)
        val webView = dialog.findViewById<android.webkit.WebView>(R.id.webView)

        var isCompleted = false

        ivClose.setOnClickListener {
          Log.d(TAG, "User closed dialog")
            isCompleted = true
            dialog.dismiss()
        }

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
        }
        android.webkit.CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(webView, true)
        }

        // Log the page's JS console too (nothing is hidden or filtered)
        webView.webChromeClient = object : android.webkit.WebChromeClient() {
            override fun onConsoleMessage(m: android.webkit.ConsoleMessage?): Boolean {
              Log.d("MyFatoorahJS", "${m?.messageLevel()}: ${m?.message()} (${m?.sourceId()}:${m?.lineNumber()})")
                return true
            }
        }

        // Returns true only for OUR callback URLs. MyFatoorah's own pages load normally.
        fun handleUrl(currentUrl: String): Boolean {
            val isSuccess = currentUrl.startsWith(SUCCESS_URL)
            val isError = currentUrl.startsWith(ERROR_URL)
            if (!isSuccess && !isError) return false
            if (isCompleted) return true

            isCompleted = true
            val paymentId = currentUrl.toUri().getQueryParameter("paymentId")
          Log.d(TAG, "Callback hit: ${if (isSuccess) "SUCCESS" else "ERROR"} url=$currentUrl paymentId=$paymentId")

            dialog.dismiss()
            if (isSuccess && !paymentId.isNullOrEmpty()) {
                addPayout(amount, paymentId)
            } else {
                showToast("Payment failed")
            }
            return true
        }

        webView.webViewClient = object : android.webkit.WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: android.webkit.WebView?,
                request: android.webkit.WebResourceRequest?
            ): Boolean {
                val currentUrl = request?.url?.toString() ?: ""
                Log.d(TAG, "nav: $currentUrl")
                return handleUrl(currentUrl)
            }

            override fun onPageStarted(view: android.webkit.WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d(TAG, "pageStarted: $url")
                handleUrl(url ?: "") // backup if a redirect skips shouldOverrideUrlLoading
            }

            override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(TAG, "pageFinished: $url")
            }

            override fun onReceivedError(
                view: android.webkit.WebView?,
                request: android.webkit.WebResourceRequest?,
                error: android.webkit.WebResourceError?
            ) {
                Log.e(TAG, "WebView error ${error?.errorCode} ${error?.description} for ${request?.url}")
            }

            override fun onReceivedHttpError(
                view: android.webkit.WebView?,
                request: android.webkit.WebResourceRequest?,
                errorResponse: android.webkit.WebResourceResponse?
            ) {
                if (request?.isForMainFrame == true) {
                    Log.e(TAG, "HTTP ${errorResponse?.statusCode} for ${request.url}")
                }
            }
        }

        Log.d(TAG, "Loading: $url")
        webView.loadUrl(url)
        dialog.show()
    }


    private fun isInvalid(et: AppCompatEditText, fieldName: String): Boolean {
        val value = et.text.toString().trim()
        if (value.isEmpty()) {
            showToast("${getString(R.string.please_enter)} $fieldName")
            return true
        }
        val num = value.toIntOrNull() ?: 0
        if (num <= 0) {
            showToast("$fieldName ${getString(R.string.should_be_greater_than_zero)}")
            return true
        }
        return false
    }
}
