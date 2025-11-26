package com.desynatadmin.data.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import android.content.Context
import com.desynatadmin.utils.CommonMethod
import com.desynatadmin.utils.ProgressDialogUtils
import com.desynatadmin.utils.isNetworkAvailable

abstract class BaseRepository(private val context: Context) {

    var progress = MutableLiveData<Boolean>()
    var error = SingleLiveEvent<String>()

    protected suspend fun <T> handleApiCall(
        api: suspend () -> Response<T>,
        responseLiveData: MutableLiveData<T>
    ) {
        if (!isNetworkAvailable(context)) {
            error.postValue("No internet connection. Please check your network.")
            return
        }
        progress.postValue(true)
        try {
            val response = api()

            when {
                response.isSuccessful && response.body() != null -> {
                    responseLiveData.postValue(response.body())
                }
                response.isSuccessful && response.body() == null -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    error.postValue("Empty response from server")
                }
                response.errorBody() != null -> {
                    ProgressDialogUtils.getInstance().hideProgress()
                    try {
                        val errorText = response.errorBody()?.charStream()?.readText() ?: ""
                        if (errorText.isNotBlank()) {
                            val errorJsonObject = JSONObject(errorText)
                            val errorMessage = when {
                                errorJsonObject.has("msg") -> errorJsonObject.optString("msg")
                                errorJsonObject.has("message") -> errorJsonObject.optString("message")
                                else -> "Unknown server error"

                            }
                            error.postValue(errorMessage)
                            if (errorJsonObject.optInt("status_code")!=0){
                                CommonMethod.commonErrorHandling(context,errorJsonObject.optInt("status_code"),errorMessage)

                            }
                            else{
                                CommonMethod.commonErrorHandling(context,response.code(),errorMessage)
                            }

                        } else {
                            error.postValue("Unknown server error (no message provided)")
                        }
                    } catch (jsonEx: Exception) {
                        error.postValue("Server error: ${response.code()} ${response.message()}")
                    } finally {
                        progress.postValue(false)
                    }
                }
                else -> {
                    error.postValue("Unexpected error: ${response.code()} ${response.message()}")
                }
            }

        } catch (e: UnknownHostException) {
            ProgressDialogUtils.getInstance().hideProgress()
            error.postValue("No internet connection. Please check your network.")
        } catch (e: SocketTimeoutException) {
            ProgressDialogUtils.getInstance().hideProgress()
            error.postValue("Connection timed out. Please try again.")
        } catch (e: IOException) {
            ProgressDialogUtils.getInstance().hideProgress()
            error.postValue("Network error: ${e.localizedMessage ?: "Unknown"}")
        } catch (e: Exception) {
            ProgressDialogUtils.getInstance().hideProgress()
            error.postValue(e.localizedMessage ?: "Something went wrong")
            Log.e("BaseRepository", "Unexpected error", e)
        } finally {
            ProgressDialogUtils.getInstance().hideProgress()
            progress.postValue(false)
        }
    }
}

