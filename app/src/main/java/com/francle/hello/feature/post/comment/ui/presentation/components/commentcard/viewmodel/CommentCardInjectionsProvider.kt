package com.francle.hello.feature.post.comment.ui.presentation.components.commentcard.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.francle.hello.feature.post.like.domain.repository.LikeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommentCardInjectionsProvider @Inject constructor(
    val likeRepository: LikeRepository,
    val sharedPreferences: SharedPreferences
) : ViewModel()
