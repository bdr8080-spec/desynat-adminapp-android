package com.desynatadmin.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.desynatadmin.R
import com.desynatadmin.ui.auth.LoginResponse
import com.desynatadmin.ui.home.subAdminManagement.response.GetSubAdminDetailResponse
import com.desynatadmin.ui.home.subscriptionPlan.response.GetCountryResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreference(context: Context) {
    val preference: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = preference.edit()

    companion object {
        var instance: SharedPreference? = null
        fun get(ctx: Context): SharedPreference {
            if (instance == null) {
                instance = SharedPreference(ctx)
            }
            return instance!!
        }
    }

    operator fun SharedPreferences.set(key: String, value: Any?) {
        when (value) {
            is Int -> edit { it.putInt(key, value) }
            is String? -> edit { it.putString(key, value) }
            is Boolean -> edit { it.putBoolean(key, value) }
            is Float -> edit { it.putFloat(key, value) }
            is Long -> edit { it.putLong(key, value) }
            else -> Log.e("TAG", "Setting shared pref failed for key: $key and value: $value ")
        }
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    inline operator fun <reified T : Any> SharedPreferences.get(
        key: String,
        defaultValue: T? = null
    ): T? {
        return when (T::class) {
            String::class -> getString(key, defaultValue as? String) as T?
            Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
            Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
            Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
            Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }

    var language: String
        get() = preference["language", "ar"]!!
        set(value) = preference.set("language", value)


    var selectedCountry: String
        get() = preference["selectedCountry", "ar"]!!
        set(value) = preference.set("selectedCountry", value)

    var accessToken: String
        get() = preference["accessToken", ""]!!
        set(value) = preference.set("accessToken", value)

    var deviceToken: String
        get() = preference["deviceToken", ""]!!
        set(value) = preference.set("deviceToken", value)


    var badgeCount: Int
        get() = preference["badgeCount", 0]!!
        set(value) = preference.set("badgeCount", value)

    fun saveCountryList(countryList: List<GetCountryResponse.Data>) {
        val json = Gson().toJson(countryList)
        preference.set("countryList", json)
    }

    fun getCountryList(): List<GetCountryResponse.Data>? {
        val json = preference["countryList", ""] ?: return null
        return try {
            val type = object : TypeToken<List<GetCountryResponse.Data>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }

    fun clearCountryList() {
        preference["countryList"] = null
    }

    fun saveUserCountryList(countryList: List<GetCountryResponse.Data>) {
        val json = Gson().toJson(countryList)
        preference.set("countryUserList", json)
    }

    fun getUserCountryList(): List<GetCountryResponse.Data>? {
        val json = preference["countryUserList", ""] ?: return null
        return try {
            val type = object : TypeToken<List<GetCountryResponse.Data>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }


    fun clearUserCountryList() {
        preference["countryUserList"] = null
    }

    fun saveUser(user: LoginResponse.Data) {
        val json = Gson().toJson(user)
        preference.set("userData", json)
    }

    fun getUser(): LoginResponse.Data? {
        val json = preference["userData", ""] ?: return null
        return try {
            Gson().fromJson(json, LoginResponse.Data::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun saveSubAdmin(user: GetSubAdminDetailResponse.Data) {
        val json = Gson().toJson(user)
        preference.set("subAdminData", json)
    }

    fun getSubAdmin(): GetSubAdminDetailResponse.Data? {
        val json = preference["subAdminData", ""] ?: return null
        return try {
            Gson().fromJson(json, GetSubAdminDetailResponse.Data::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun clearUser() {
        preference.set("userData", "")
    }

}