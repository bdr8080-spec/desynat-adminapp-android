package com.desynatadmin.utils

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
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
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
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
import org.json.JSONArray
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.log

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

//    fun applyArabicOnlyFilter(editText: EditText) {
//
//        val filter = InputFilter { source, start, end, dest, dstart, dend ->
//
//            for (i in start until end) {
//                val char = source[i]
//
//                val isArabic = char in '\u0600'..'\u06FF'
//                val isNumber = char.isDigit()
//                val isSpace = char == ' '
//                val isNewLine = char == '\n' || char == '\r'
//                val isEmoji = Character.isSurrogate(char)
//
//                val isSymbol = char in listOf(
//                    '@','#','%','&','*','!','?','.',',','-','_','(',')','/','+','='
//                )
//
//                if (!isArabic && !isNumber && !isSpace && !isNewLine && !isEmoji && !isSymbol) {
//                    return@InputFilter ""
//                }
//            }
//
//            if (dstart == 0 && source.startsWith(" ")) {
//                return@InputFilter ""
//            }
//
//            null
//        }
//
//        editText.filters = arrayOf(filter)
//    }

    fun applyArabicOnlyFilter(editText: EditText) {

        val filter = InputFilter { source, start, end, dest, dstart, dend ->

            for (i in start until end) {
                val char = source[i]

                val isArabic = char in '\u0600'..'\u06FF'
                val isEnglish = char.isLetter() && char.code < 128
                val isNumber = char.isDigit()
                val isSpace = char == ' '
                val isNewLine = char == '\n' || char == '\r'
                val isEmoji = Character.isSurrogate(char)

                val isSymbol = char in listOf(
                    '@', '#', '%', '&', '*', '!', '?', '.', ',', '-', '_',
                    '(', ')', '/', '+', '=', ':', ';', '\'', '"'
                )

                if (!isArabic &&
                    !isEnglish &&
                    !isNumber &&
                    !isSpace &&
                    !isNewLine &&
                    !isEmoji &&
                    !isSymbol
                ) {
                    return@InputFilter ""
                }
            }

            // Prevent leading space
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

    fun removeStartingSpace(editText: EditText) {

        editText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {

                if (s.toString().startsWith(" ")) {

                    editText.setText(s.toString().trimStart())

                    editText.setSelection(editText.text.length)
                }
            }
        })
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
        val targetFormat = SimpleDateFormat(outputFormat, Locale.ENGLISH).apply {
            timeZone = TimeZone.getDefault()
        }
        val date: Date
        try {
            if (Constants.language == "ar") {
                val date = originalFormat.parse(inputDate)

                var formattedDate = targetFormat.format(date!!)

                val englishMonthsMMMM = arrayOf(
                    "January", "February", "March", "April",
                    "May", "June", "July", "August",
                    "September", "October", "November", "December"
                )

                val arabicMonthsMMMM = arrayOf(
                    "يناير", "فبراير", "مارس", "أبريل",
                    "مايو", "يونيو", "يوليو", "أغسطس",
                    "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
                )

                for (i in englishMonthsMMMM.indices) {
                    formattedDate =
                        formattedDate.replace(
                            englishMonthsMMMM[i],
                            arabicMonthsMMMM[i]
                        )
                }

                val englishMonths = arrayOf(
                    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
                )

                val arabicMonths = arrayOf(
                    "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
                    "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
                )

                for (i in englishMonths.indices) {
                    formattedDate =
                        formattedDate.replace(
                            englishMonths[i],
                            arabicMonths[i]
                        )
                }

                val englishDays = arrayOf(
                    "Sun", "Mon", "Tue", "Wed",
                    "Thu", "Fri", "Sat"
                )

                val arabicDays = arrayOf(
                    "الأحد", "الاثنين", "الثلاثاء", "الأربعاء",
                    "الخميس", "الجمعة", "السبت"
                )

                for (i in englishDays.indices) {
                    formattedDate = formattedDate.replace(
                        englishDays[i],
                        arabicDays[i]
                    )
                }

                formattedDate = formattedDate
                    .replace("AM", "ص", ignoreCase = true)
                    .replace("PM", "م", ignoreCase = true)
                return formattedDate
            } else {
                date = originalFormat.parse(inputDate)
                return targetFormat.format(date)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }
    }

    fun customizeChangeDateFormatKuwait(inputDate: String,inputFormat:String="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",outputFormat:String="dd MMM,yyyy"): String {
        val originalFormat: SimpleDateFormat = SimpleDateFormat(inputFormat, Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Kuwait")}
        val targetFormat = SimpleDateFormat(outputFormat, Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }
        val date: Date
        try {
            if (Constants.language == "ar") {
                val date = originalFormat.parse(inputDate)

                var formattedDate = targetFormat.format(date!!)

                val englishMonthsMMMM = arrayOf(
                    "January", "February", "March", "April",
                    "May", "June", "July", "August",
                    "September", "October", "November", "December"
                )

                val arabicMonthsMMMM = arrayOf(
                    "يناير", "فبراير", "مارس", "أبريل",
                    "مايو", "يونيو", "يوليو", "أغسطس",
                    "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
                )

                for (i in englishMonthsMMMM.indices) {
                    formattedDate =
                        formattedDate.replace(
                            englishMonthsMMMM[i],
                            arabicMonthsMMMM[i]
                        )
                }

                val englishMonths = arrayOf(
                    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
                )

                val arabicMonths = arrayOf(
                    "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
                    "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
                )

                for (i in englishMonths.indices) {
                    formattedDate =
                        formattedDate.replace(
                            englishMonths[i],
                            arabicMonths[i]
                        )
                }

                val englishDays = arrayOf(
                    "Sun", "Mon", "Tue", "Wed",
                    "Thu", "Fri", "Sat"
                )

                val arabicDays = arrayOf(
                    "الأحد", "الاثنين", "الثلاثاء", "الأربعاء",
                    "الخميس", "الجمعة", "السبت"
                )

                for (i in englishDays.indices) {
                    formattedDate = formattedDate.replace(
                        englishDays[i],
                        arabicDays[i]
                    )
                }

                formattedDate = formattedDate
                    .replace("AM", "ص", ignoreCase = true)
                    .replace("PM", "م", ignoreCase = true)
                return formattedDate
            } else {
                date = originalFormat.parse(inputDate)
                return targetFormat.format(date)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }
    }

    fun customizeChangeDateFormatKuwaitFromUtc(
        inputDate: String,
        inputFormat: String = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
        outputFormat: String = "dd MMM, yyyy hh:mm a"
    ): String {

        val originalFormat = SimpleDateFormat(inputFormat, Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val targetFormat = SimpleDateFormat(outputFormat, Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Asia/Kuwait")
        }

        return try {
            if (Constants.language == "ar") {
                val date = originalFormat.parse(inputDate)

                var formattedDate = targetFormat.format(date!!)

                val englishMonthsMMMM = arrayOf(
                    "January", "February", "March", "April",
                    "May", "June", "July", "August",
                    "September", "October", "November", "December"
                )

                val arabicMonthsMMMM = arrayOf(
                    "يناير", "فبراير", "مارس", "أبريل",
                    "مايو", "يونيو", "يوليو", "أغسطس",
                    "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
                )

                for (i in englishMonthsMMMM.indices) {
                    formattedDate =
                        formattedDate.replace(
                            englishMonthsMMMM[i],
                            arabicMonthsMMMM[i]
                        )
                }

                val englishMonths = arrayOf(
                    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
                )

                val arabicMonths = arrayOf(
                    "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
                    "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
                )

                for (i in englishMonths.indices) {
                    formattedDate =
                        formattedDate.replace(
                            englishMonths[i],
                            arabicMonths[i]
                        )
                }

                val englishDays = arrayOf(
                    "Sun", "Mon", "Tue", "Wed",
                    "Thu", "Fri", "Sat"
                )

                val arabicDays = arrayOf(
                    "الأحد", "الاثنين", "الثلاثاء", "الأربعاء",
                    "الخميس", "الجمعة", "السبت"
                )

                for (i in englishDays.indices) {
                    formattedDate = formattedDate.replace(
                        englishDays[i],
                        arabicDays[i]
                    )
                }

                formattedDate = formattedDate
                    .replace("AM", "ص", ignoreCase = true)
                    .replace("PM", "م", ignoreCase = true)
                return formattedDate
            } else {
                val date = originalFormat.parse(inputDate)
                targetFormat.format(date!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
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
        val outputFormat = SimpleDateFormat(output, Locale.ENGLISH).apply {
            timeZone = TimeZone.getDefault()
        }


        if (Constants.language == "ar") {
            val date = inputFormat.parse(date)
            var formattedDate =  outputFormat.format(date!!)

            val englishMonthsMMMM = arrayOf(
                "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December"
            )

            val arabicMonthsMMMM = arrayOf(
                "يناير", "فبراير", "مارس", "أبريل",
                "مايو", "يونيو", "يوليو", "أغسطس",
                "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
            )

            for (i in englishMonthsMMMM.indices) {
                formattedDate =
                    formattedDate.replace(
                        englishMonthsMMMM[i],
                        arabicMonthsMMMM[i]
                    )
            }

            val englishMonths = arrayOf(
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
            )

            val arabicMonths = arrayOf(
                "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
                "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
            )

            for (i in englishMonths.indices) {
                formattedDate =
                    formattedDate.replace(
                        englishMonths[i],
                        arabicMonths[i]
                    )
            }

            val englishDays = arrayOf(
                "Sun", "Mon", "Tue", "Wed",
                "Thu", "Fri", "Sat"
            )

            val arabicDays = arrayOf(
                "الأحد", "الاثنين", "الثلاثاء", "الأربعاء",
                "الخميس", "الجمعة", "السبت"
            )

            for (i in englishDays.indices) {
                formattedDate = formattedDate.replace(
                    englishDays[i],
                    arabicDays[i]
                )
            }

            formattedDate = formattedDate
                .replace("AM", "ص", ignoreCase = true)
                .replace("PM", "م", ignoreCase = true)
            return formattedDate
        } else {
            val date = inputFormat.parse(date)
            return outputFormat.format(date!!)
        }

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
                    Log.d("TAG", "onClickItem: ${countryList[position]}")
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

    fun getVideoThumbnailFile(context: Context, uri: Uri): File? {
        val retriever = MediaMetadataRetriever()

        return try {
            retriever.setDataSource(context, uri)

            val bitmap = retriever.getFrameAtTime(0) // first frame

            bitmap?.let {
                val file = File(
                    context.cacheDir,
                    "thumb_${System.currentTimeMillis()}.jpg"
                )
                val fos = file.outputStream()
                it.compress(Bitmap.CompressFormat.JPEG, 80, fos)
                fos.flush()
                fos.close()

                file
            }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
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

    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        context.resources.updateConfiguration(
            config,
            context.resources.displayMetrics
        )
    }

    fun objectToCsv(obj: Any?): String {
        if (obj == null) return ""

        val fields = obj::class.java.declaredFields
        val csv = StringBuilder()

        // Header
        csv.append(fields.joinToString(",") { it.name }).append("\n")

        // Row
        val row = fields.joinToString(",") {
            it.isAccessible = true
            "\"${it.get(obj)?.toString() ?: ""}\""
        }

        csv.append(row).append("\n")

        return csv.toString()
    }

    fun saveCsvFile(context: Context, fileName: String, csvData: String) {
        val resolver = context.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.csv")
            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = resolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            contentValues
        )

        if (uri != null) {
            resolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(csvData.toByteArray())
                outputStream.flush()
            }

            Toast.makeText(context, "Downloaded", Toast.LENGTH_SHORT).show()

            openCsvFile(context, uri)
        } else {
            Toast.makeText(context, "Failed to save file", Toast.LENGTH_SHORT).show()
        }
    }

    fun <T> listToCsv(list: List<T>): String {
        if (list.isEmpty()) return ""

        val fields = list[0]!!::class.java.declaredFields

        val csv = StringBuilder()

        // Header
        csv.append(fields.joinToString(",") { it.name }).append("\n")

        // Rows
        list.forEach { item ->
            val row = fields.joinToString(",") { field ->
                field.isAccessible = true
                "\"${field.get(item)?.toString() ?: ""}\""
            }
            csv.append(row).append("\n")
        }

        return csv.toString()
    }

    fun openCsvFile(context: Context, uri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "text/csv")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No app found to open CSV", Toast.LENGTH_SHORT).show()
        }
    }

}
