package com.francle.hello.feature.post.comment.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import com.francle.hello.R
import com.francle.hello.core.data.call.Resource
import com.francle.hello.core.data.file.getFileName
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.feature.post.comment.data.api.CommentApi
import com.francle.hello.feature.post.comment.data.request.CreateCommentRequest
import com.francle.hello.feature.post.comment.domain.models.Comment
import com.francle.hello.feature.post.comment.domain.repository.CommentRepository
import com.google.gson.Gson
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException

class CommentRepositoryImpl(
    private val api: CommentApi,
    private val context: Context,
    private val gson: Gson
) : CommentRepository {
    @SuppressLint("Recycle")
    override suspend fun createComment(
        arrowForwardUserId: String,
        arrowForwardEntityId: String,
        arrowForwardEntityType: Int,
        commentText: String?,
        commentMediaUris: List<Uri>?
    ): Resource<Unit> {
        return try {
            val request = CreateCommentRequest(
                arrowForwardUserId = arrowForwardUserId,
                arrowForwardEntityId = arrowForwardEntityId,
                arrowForwardEntityType = arrowForwardEntityType,
                commentText = commentText
            )
            val commentData = request.let {
                MultipartBody.Part.createFormData(
                    name = "commentData",
                    value = gson.toJson(it)
                )
            }
            val fileList = commentMediaUris?.let { uris ->
                withContext(Dispatchers.IO) {
                    uris.map { uri ->
                        context.contentResolver.openFileDescriptor(uri, "r")?.let {
                            val inputStream = FileInputStream(it.fileDescriptor)
                            val file = File(
                                context.cacheDir,
                                context.contentResolver.getFileName(uri)
                            )
                            val outputStream = FileOutputStream(file)
                            inputStream.copyTo(outputStream)
                            file
                        }
                    }
                }
            }
            val commentMediaContent = fileList?.mapNotNull { file ->
                file?.let {
                    MultipartBody.Part.createFormData(
                        name = "commentMediaContent",
                        filename = it.name,
                        body = it.asRequestBody()
                    )
                }
            }

            api.createComment(
                commentData = commentData,
                commentMediaContent = commentMediaContent
            )

            Resource.Success()
        } catch (e: HttpException) {
            if (e.code() == 400) {
                Resource.Error(
                    data = null,
                    message = UiText.StringResource(R.string.text_content_empty)
                )
            } else if (e.code() == 500) {
                Resource.Error(
                    data = null,
                    message = UiText.StringResource(R.string.created_unsuccessfully)
                )
            } else {
                Resource.Error(
                    data = null,
                    message = UiText.StringResource(R.string.something_went_wrong)
                )
            }
        } catch (e: Exception) {
            Resource.Error(
                data = null,
                message = UiText.StringResource(R.string.something_went_wrong)
            )
        }
    }

    override fun getCommentsOfEntity(
        entityId: String,
        page: Int,
        pageSize: Int
    ): Flow<Resource<List<Comment?>?>> {
        return flow {
            try {
                val comments = api.getCommentsOfEntity(
                    entityId = entityId,
                    page = page,
                    pageSize = pageSize
                ).comments?.mapNotNull {
                    it?.toComment()
                }

                emit(Resource.Success(comments))
            } catch (e: HttpException) {
                emit(
                    Resource.Error(
                        data = null,
                        message = UiText.StringResource(R.string.network_error_happens)
                    )
                )
            } catch (e: IOException) {
                emit(
                    Resource.Error(
                        data = null,
                        message = UiText.StringResource(R.string.local_error_happens)
                    )
                )
            }
        }
    }

    override fun getCommentsOfUser(
        userId: String,
        page: Int,
        pageSize: Int
    ): Flow<Resource<List<Comment?>?>> {
        return flow {
            try {
                val comments = api.getCommentsOfUser(
                    userId = userId,
                    page = page,
                    pageSize = pageSize
                ).comments?.mapNotNull {
                    it?.toComment()
                }

                emit(Resource.Success(comments))
            } catch (e: HttpException) {
                emit(
                    Resource.Error(
                        data = null,
                        message = UiText.StringResource(R.string.network_error_happens)
                    )
                )
            } catch (e: IOException) {
                emit(
                    Resource.Error(
                        data = null,
                        message = UiText.StringResource(R.string.local_error_happens)
                    )
                )
            }
        }
    }

    override suspend fun deleteComment(commentId: String): Resource<Unit> {
        return try {
            api.deleteComment(commentId)
            Resource.Success()
        } catch (e: HttpException) {
            Resource.Error(
                data = null,
                message = UiText.StringResource(R.string.network_error_happens)
            )
        } catch (e: IOException) {
            Resource.Error(
                data = null,
                message = UiText.StringResource(R.string.local_error_happens)
            )
        }
    }
}
