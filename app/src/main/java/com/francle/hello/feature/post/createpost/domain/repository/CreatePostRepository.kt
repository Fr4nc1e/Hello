package com.francle.hello.feature.post.createpost.domain.repository

import android.net.Uri
import com.francle.hello.core.data.call.Resource

interface CreatePostRepository {
    suspend fun createPost(
        postText: String?,
        contentUriList: List<Uri>?
    ): Resource<Unit>
}
