package com.francle.hello.feature.home.ui.presentation.components.postcard.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.francle.hello.feature.like.domain.repository.LikeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InjectionsProvider @Inject constructor(
    val likeRepository: LikeRepository,
    val sharedPreferences: SharedPreferences
) : ViewModel()
