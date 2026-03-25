package com.desynatadmin.utils

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Html
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import coil.load
import com.bumptech.glide.Glide
import com.desynatadmin.R
import com.desynatadmin.adapter.AllCountryPickerAdapter
import com.desynatadmin.adapter.CountryPickerAdapter
import com.desynatadmin.adapter.ImagePreviewAdapter
import com.desynatadmin.databinding.CustomCountryPickerBinding
import com.desynatadmin.databinding.DeleteDialogLayoutBinding
import com.desynatadmin.databinding.ImagePreviewDialogBinding
import com.desynatadmin.databinding.ImagePreviewListDialogBinding
import com.desynatadmin.databinding.SuccessDialogLayoutBinding
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.google.android.material.tabs.TabLayoutMediator
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object CommonMethod {

    fun openDatePicker(context: Context,editText: EditText,format:String="dd-MM-yyyy",pastDate:Boolean=false,futureDate:Boolean=true,onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,R.style.DialogTheme,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                val sdf = SimpleDateFormat(format, Locale.getDefault())
                val formattedDate = sdf.format(selectedCalendar.time)

                editText.setText(formattedDate)
                onDateSelected(formattedDate)
            },
            year,
            month,
            day
        )

        if (!pastDate){
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        }
        if (!futureDate) {
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        }

        datePickerDialog.show()
    }


     fun detectGestureMode(root: View, viewBottom: View) {
         root.setOnApplyWindowInsetsListener { _, insets ->
            val windowInsetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets)
            val navigationBarInsets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.navigationBars())

            val isUsingNavigationButtons = navigationBarInsets.bottom > 0

            if (isUsingNavigationButtons) {
                viewBottom.isVisible = true
            } else {
                viewBottom.isVisible = false
            }
            insets
        }
    }

    fun uriToFile(context:Context,uri: Uri): File {
        val mimeType = context.contentResolver.getType(uri)

        val extension = when (mimeType) {
            "image/jpeg" -> "jpg"
            "image/jpg" -> "jpg"
            "image/png" -> "png"
            "image/gif" -> "gif"
            "image/webp" -> "webp"
            "video/mp4" -> "mp4"
            "application/pdf" -> "pdf"
            else -> "bin"
        }
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "picked_file_${System.currentTimeMillis()}.$extension")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    fun uriToFileForStory(context: Context, uri: Uri): File? {
        val mimeType = context.contentResolver.getType(uri)

        val extension = when {
            mimeType == null -> "bin" // fallback
            mimeType.startsWith("image") -> "jpg"
            mimeType.startsWith("video") -> "mp4"
            mimeType == "application/pdf" -> "pdf"
            else -> "bin"
        }

        val file = File(context.cacheDir, "picked_file_${System.currentTimeMillis()}.$extension")

        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

       /* if (extension=="mp4") {
            val maxSize = 5 * 1024 * 1024 // 5 MB in bytes
            if (file.length() > maxSize) {
                file.delete()
                context.showToast("Upload size exceed to 5 MB")
                return null
            }
        }*/

        return file
    }


    @SuppressLint("Range")
    fun fileFromUri(context: Context, uri: Uri): File {
        val fileName = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getString(cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME))
            } else null
        } ?: "temp_file"

        val file = File(context.cacheDir, fileName)

        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        return file
    }

    fun applyArabicOnlyFilter(editText: EditText) {

        val filter = InputFilter { source, start, end, dest, dstart, dend ->

            for (i in start until end) {
                val char = source[i]

                val isArabic = char in '\u0600'..'\u06FF'
                val isNumber = char.isDigit()
                val isSpace = char == ' '
                val isNewLine = char == '\n' || char == '\r'
                val isEmoji = Character.isSurrogate(char)

                val isSymbol = char in listOf(
                    '@','#','%','&','*','!','?','.',',','-','_','(',')','/','+','='
                )

                if (!isArabic && !isNumber && !isSpace && !isNewLine && !isEmoji && !isSymbol) {
                    return@InputFilter ""
                }
            }

            if (dstart == 0 && source.startsWith(" ")) {
                return@InputFilter ""
            }

            null
        }

        editText.filters = arrayOf(filter)
    }

    fun applyEnglishOnlyFilter(editText: EditText) {

        val allowedSymbols = setOf(
            '@','#','%','&','*','!','?','.',',','-','_','(',')','/','+','='
        )

        val filter = InputFilter { source, start, end, dest, dstart, dend ->

            for (i in start until end) {
                val char = source[i]

                val isEnglish = char in 'A'..'Z' || char in 'a'..'z'
                val isNumber = char.isDigit()
                val isSpace = char == ' '
                val isNewLine = char == '\n' || char == '\r'
                val isEmoji = Character.isSurrogate(char)
                val isSymbol = char in allowedSymbols

                if (!isEnglish && !isNumber && !isSpace && !isEmoji && !isSymbol &&!isNewLine) {
                    return@InputFilter ""
                }
            }

            // block leading space
            if (dstart == 0 && source.startsWith(" ")) {
                return@InputFilter ""
            }

            null
        }

        editText.filters = arrayOf(filter)
    }

    fun removeAllTextSpaces (editText: AppCompatEditText){
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val text = charSequence.toString()
                val newText = text.replace(" ", "")
                if (text != newText) {
                    editText.setText(newText)
                    editText.setSelection(newText.length)
                }
            }
            override fun afterTextChanged(editable: Editable?) {

            }
        })
    }

    fun removeSpace(editText: EditText){
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {

                if (start == 0 && count > 0 && charSequence?.startsWith(" ") == true) {
                    editText.setText(charSequence.toString().trim())
                    editText.setSelection(editText.text.length)
                }
            }
            override fun afterTextChanged(editable: Editable?) {

            }
        })
    }

    fun spannableText(
        context: Context,
        fullString: String,
        spannableString: String,
        onClick: (() -> Unit)? = null,
        color: Int = R.color.brown,
        fontRes: Int = R.font.poppins_semi_bold
    ): SpannableString {
        val textSpannable = SpannableString(fullString)

        val typeface = ResourcesCompat.getFont(context, fontRes)

        fun applySpan(target: String, click: (() -> Unit)?) {
            val startIndex = textSpannable.indexOf(target)
            if (startIndex != -1) {
                val endIndex = startIndex + target.length

                typeface?.let {
                    textSpannable.setSpan(
                        CustomTypefaceSpan(null, it),
                        startIndex, endIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                textSpannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, color)),
                    startIndex, endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                click?.let {
                    textSpannable.setSpan(
                        object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                it()
                            }

                            override fun updateDrawState(ds: TextPaint) {
                                super.updateDrawState(ds)
                                ds.isUnderlineText = false // no underline
                                ds.color = ContextCompat.getColor(context, color)
                            }
                        },
                        startIndex, endIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }

        applySpan(spannableString, onClick)

        return textSpannable

    }

    fun commonErrorHandling(
        context: Context,
        errorCode:Int,
        errorMessage:String

    ) {
        try {
            when (errorCode) {

                401 -> {
                    clearAllData(context)
                }
               else -> {
                    context.showToast(errorMessage)
                }


            }
        }

        catch (e:Exception){

        }
    }

    fun clearAllData(context: Context){
        /*SharedPreference.get(context).accessToken = ""
        SharedPreference.get(context).language = ""
        SharedPreference.get(context).clearUser()
        Constants.latitude = 0.0
        Constants.longitude = 0.0
        val intent = Intent(context, AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)*/
    }

    fun formatPrice(price: Double): String {
        return if (price % 1.0 == 0.0) {
            price.toInt().toString()
        } else {
            price.toString()
        }
    }

    fun formatPriceToReduceLength(amountStr: String?): String {
        if (amountStr.isNullOrBlank()||amountStr=="null") return ""
        return try {
            val amount = amountStr.toDouble()
            when {
                amount >= 1_00_00_000 -> "${amount / 1_00_00_000}Cr"
                amount >= 1_00_000 -> "${amount / 1_00_000}L"
                amount >= 1_000 -> "${amount / 1_000}K"
                else -> {
                    if (amount % 1.0 == 0.0) {
                        amount.toInt().toString()
                    } else {
                        amount.toString()
                    }
                }
            }
        } catch (e: NumberFormatException) {
            amountStr // fallback → return original string if parsing fails
        }
    }


    fun customizeChangeDateFormat(inputDate: String,inputFormat:String="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",outputFormat:String="dd MMM,yyyy"): String {
        val originalFormat: SimpleDateFormat = SimpleDateFormat(inputFormat, Locale.ENGLISH).apply {
            timeZone = TimeZone.getTimeZone("UTC")}
        val targetFormat = SimpleDateFormat(outputFormat, Locale.ENGLISH)

        val date: Date
        try {
            date = originalFormat.parse(inputDate)
            return targetFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }
    }

    fun htmlText(text: String): Spanned {
        return HtmlCompat.fromHtml(
            text,
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    fun formatDate(date:String,input:String="dd/MM/yyyy",output:String="yyyy-MM-dd"):String{
        val inputFormat = SimpleDateFormat(input, Locale.getDefault())
        val outputFormat = SimpleDateFormat(output, Locale.getDefault())
        val date = inputFormat.parse(date)
        return outputFormat.format(date!!)
    }

    fun isValidPassword(password: String): Boolean {
        // At least 8 characters, 1 uppercase, 1 lowercase, 1 digit, 1 special character
        val passwordPattern =
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#\$%^&+=!.*_\\-]).{8,}\$"
        return password.matches(passwordPattern.toRegex())
    }

    fun showSuccessDialog(context: Context,description:String,button:Boolean=false,onDismiss: (() -> Unit)? = null) {
        val dialog = Dialog(context, R.style.CustomDialog)
        val dialogBinding = SuccessDialogLayoutBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        dialogBinding.tvDescription.text=description
        dialogBinding.tvOkay.isVisible = button

        dialogBinding.tvOkay.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            onDismiss?.invoke()
        }

        dialog.show()
        if(button==false){
            Handler(Looper.getMainLooper()).postDelayed({
                if (dialog.isShowing) {
                    dialog.dismiss()
                }
            }, 1000)
        }
    }

    fun formatDoubleValue(value: Double?): String {
        if (value == null) return ""
        return if (value % 1.0 == 0.0) {
            value.toInt().toString()  // Convert to integer string if whole number
        } else {
            value.toString()          // Keep decimal part if not whole
        }
    }

    fun deleteAlertDialog(
        context: Context,
        description: String,
        onResult: (action: DialogAction) -> Unit
    ) {
        val dialog = Dialog(context, R.style.CustomDialog)
        val dialogBinding = DeleteDialogLayoutBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogBinding.root)

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        dialogBinding.tvDescription.text = description

        dialogBinding.btnDelete.setOnClickListener {
            onResult(DialogAction.DELETE)
            dialog.dismiss()
        }
        dialogBinding.btnDiscard.setOnClickListener {
            onResult(DialogAction.DISCARD)
            dialog.dismiss()
        }

        dialog.show()
    }

    enum class DialogAction {
        DELETE, DISCARD
    }

    fun showCountryPicker(
        context: Context,
        anchor: View,
        countryList: ArrayList<GetCountryResponse.Data>,
        isDialCode:Boolean=false,
        onCountrySelected: (GetCountryResponse.Data) -> Unit
    ) {
        val binding = CustomCountryPickerBinding.inflate(LayoutInflater.from(context))

        val popupWindow = PopupWindow(
            binding.root,
            anchor.width,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.elevation = 20f
        popupWindow.showAsDropDown(anchor)

        binding.rvCountry.adapter = CountryPickerAdapter(context, countryList,isDialCode,
            object : CountryPickerAdapter.ClickOnItem {
                override fun onClickItem(position: Int) {
                    onCountrySelected(countryList[position])
                    popupWindow.dismiss()
                }
            }
        )
    }


    fun showAllCountryPicker(
        context: Context,
        anchor: View,
        countryList: ArrayList<GetCountryResponse.Data>,
        isDialCode:Boolean=false,
        onCountrySelected: (GetCountryResponse.Data) -> Unit
    ) {
        val binding = CustomCountryPickerBinding.inflate(LayoutInflater.from(context))

        val popupWindow = PopupWindow(
            binding.root,
            anchor.width,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.elevation = 20f
        popupWindow.showAsDropDown(anchor)

        binding.rvCountry.adapter = AllCountryPickerAdapter(context, countryList,isDialCode,
            object : AllCountryPickerAdapter.ClickOnItem {
                override fun onClickItem(position: Int) {
                    onCountrySelected(countryList[position])
                    popupWindow.dismiss()
                }
            }
        )
    }

    fun formatAmount(value: String): String {
        return try {
            val number = value.toDouble()
            val parts = value.split(".")

            if (parts.size == 1) return parts[0]

            val decimal = parts[1]

            return if (decimal.toDouble() == 0.0) {
                number.toInt().toString()
            } else {
                value
            }
        } catch (e: Exception) {
            value
        }
    }


    fun convertTimestampToDateString(timestamp: Long,format: String="yyyy-MM-dd"): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat(format, Locale.getDefault())
        return format.format(date)
    }

    fun loadDocument(imageView: ImageView, url: String?,placeHolder:Int = R.drawable.ic_placeholder) {
        if (url.isNullOrEmpty()) {
            imageView.setImageResource(R.drawable.ic_placeholder)
            return
        }

        if (url.endsWith(".pdf", ignoreCase = true)) {
            imageView.setImageResource(R.drawable.pdf)
        } else {
            imageView.load(url) {
                crossfade(true)
                placeholder(placeHolder)
                error(placeHolder)
            }
        }
    }

    fun openPdfInBrowser(context: Context, pdfUrl: String) {
        if (pdfUrl.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    fun downloadPdf(context: Context, pdfUrl: String, fileName: String = "Invoice") {

        val request = DownloadManager.Request(Uri.parse(pdfUrl))
            .setTitle(fileName)
            .setDescription("Downloading PDF...")
            .setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "$fileName.pdf"
            )

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        downloadManager.enqueue(request)

        context.showToast("Download started")
    }

    fun getVideoThumbnail(videoUrl: String): Bitmap? {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(videoUrl, HashMap())
            val bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
            retriever.release()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun imagePreviewDialog(context: Context,image:String,onDismiss: (() -> Unit)? = null) {
        val dialog = Dialog(context, R.style.CustomDialog)
        val dialogBinding = ImagePreviewDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog?.setContentView(it) }

        val window: Window = dialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.CENTER
        window.attributes = params

        dialogBinding.apply {

            dialog.setOnDismissListener {
                onDismiss?.invoke()
            }

            tvToolbar.setOnClickListener{
                dialog.dismiss()
            }

            Glide.with(context).load(image).placeholder(R.drawable.ic_placeholder).into(phptoView)
        }

        dialog.show()
    }

     fun imagePreviewListDialog(context:Context,list:ArrayList<String>) {
        val dialog = Dialog(context, R.style.CustomDialog)
        val dialogBinding = ImagePreviewListDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding.root.let { dialog?.setContentView(it) }

        val window: Window = dialog?.window!!
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)

        val params: WindowManager.LayoutParams = window.attributes
        params.gravity = Gravity.CENTER
        window.attributes = params

        dialogBinding.apply {

            ivCross.setOnClickListener{
                dialog?.dismiss()
            }

            val imagePreviewAdapter = ImagePreviewAdapter(context,list)
            viewPager?.adapter = imagePreviewAdapter

            TabLayoutMediator(dotsIndicator, viewPager) { _, _ -> }.attach()

        }

        dialog?.show()
    }

    fun maskAccountNumber(accountNumber: String, visibleDigits: Int = 4): String {
        if (accountNumber.length <= visibleDigits) return accountNumber

        val maskedPart = "X".repeat(accountNumber.length - visibleDigits)
        val visiblePart = accountNumber.takeLast(visibleDigits)

        return maskedPart + visiblePart
    }
    fun setHtmlText(html: String): Spanned {
        val cleanHtml = html
            .replace("\\/", "/")
            .replace("\n", "<br>")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(cleanHtml, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(cleanHtml)
        }
    }

}
