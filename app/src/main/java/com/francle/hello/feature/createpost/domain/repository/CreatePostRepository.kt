package com.francle.hello.feature.createpost.domain.repository

import android.net.Uri
import com.francle.hello.core.data.util.call.Resource

interface CreatePostRepository {
    suspend fun createPost(
        postText: String?,
        contentUriList: List<Uri>?
    ): Resource<Unit>
}
