package com.francle.hello.feature.auth.util

import android.util.Patterns
import com.francle.hello.core.ui.util.InputError

object Validator {
    fun validateEmail(email: String): InputError? {
        return when {
            email.trim().isBlank() -> {
                InputError.BlankError
            }

            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> {
                InputError.EmailMatchError
            }

            else -> null
        }
    }

    fun validateUsername(username: String): InputError? {
        return when {
            username.trim().isBlank() -> {
                InputError.BlankError
            }

            else -> null
        }
    }

    fun validateHashTag(hashTag: String): InputError? {
        return when {
            hashTag.trim().isBlank() -> {
                InputError.BlankError
            }

            !hashTag.trim().startsWith('@') -> {
                InputError.HashTagMatchError
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
