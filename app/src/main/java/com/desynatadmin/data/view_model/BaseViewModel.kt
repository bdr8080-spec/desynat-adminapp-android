package com.desynatadmin.data.view_model

import androidx.lifecycle.ViewModel
import com.desynatadmin.data.repo.MainRepository
import com.desynatadmin.model.ValidationResult
import com.desynatadmin.validation.FieldValidator
import javax.inject.Inject

class BaseViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    val progress = repository.progress
    val error = repository.error


    fun validateFields(
        email: String?,
        password: String?,
        phone: String?
    ): ValidationResult {
        val errors = mutableMapOf<String, String>()

        if (!FieldValidator.isEmailValid(email)) {
            errors["email"] = "Invalid email format"
        }

        if (!FieldValidator.isPasswordValid(password)) {
            errors["password"] = "Password must be at least 6 characters"
        }

        if (!FieldValidator.isPhoneValid(phone)) {
            errors["phone"] = "Phone number must be 10 digits"
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

}
