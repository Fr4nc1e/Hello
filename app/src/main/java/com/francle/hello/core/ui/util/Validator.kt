package com.francle.hello.core.ui.util

import android.util.Patterns

object Validator {
    fun validateEmail(email: String): InputError? {
        return when {
            email.trim().isBlank() -> {
                InputError.BlankError
            }

            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> {
                InputError.MatchError
            }

            else -> null
        }
    }

    fun validatePassword(password: String): InputError? {
        return when {
            password.trim().isBlank() -> {
                InputError.BlankError
            }

            password.trim().length < 8 -> {
                InputError.TooShortError
            }

            else -> null
        }
    }
}
