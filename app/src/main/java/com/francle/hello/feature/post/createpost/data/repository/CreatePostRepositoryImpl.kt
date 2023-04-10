package com.francle.hello.feature.post.createpost.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import com.francle.hello.R
import com.francle.hello.core.data.util.call.Resource
import com.francle.hello.core.data.util.file.getFileName
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.feature.post.createpost.data.api.CreatePostApi
import com.francle.hello.feature.post.createpost.data.request.CreatePostRequest
import com.francle.hello.feature.post.createpost.domain.repository.CreatePostRepository
import com.google.gson.Gson
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException

class CreatePostRepositoryImpl(
    private val api: CreatePostApi,
    private val context: Context,
    private val gson: Gson
) : CreatePostRepository {
    @SuppressLint("Recycle")
    override suspend fun createPost(
        postText: String?,
        contentUriList: List<Uri>?
    ): Resource<Unit> {
        return try {
            // Upload post text
            val request = postText?.let { CreatePostRequest(it) }
            val postData = request?.let {
                MultipartBody.Part.createFormData(
                    name = "post_data",
                    value = gson.toJson(it)
                )
            }

            // Upload images and videos
            val fileList = contentUriList?.let { uriList ->
                withContext(Dispatchers.IO) {
                    uriList.map { uri ->
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
            val postContent = fileList?.mapNotNull { file ->
                file?.let {
                    MultipartBody.Part.createFormData(
                        name = "post_content",
                        filename = it.name,
                        body = it.asRequestBody()
                    )
                }
            }

            // Create post
            api.createPost(
                postData = postData,
                postContent = postContent
            )

            // Return success
            Resource.Success(message = UiText.StringResource(R.string.create_post_successfully))
        } catch (e: HttpException) {
            if (e.code() == 400) {
                Resource.Error(
                    data = null,
                    message = UiText.StringResource(R.string.text_content_empty)
                )
            } else if (e.code() == 500) {
                Resource.Error(
                    data = null,
                    message = UiText.StringResource(R.string.post_created_unsuccessfully)
                )
            } else {
                Log.d("error", "http_error")
                Resource.Error(
                    data = null,
                    message = UiText.StringResource(R.string.something_went_wrong)
                )
            }
        } catch (e: Exception) {
            Log.d("error", "other_error")
            Resource.Error(
                data = null,
                message = UiText.StringResource(R.string.something_went_wrong)
            )
        }
    }
}
