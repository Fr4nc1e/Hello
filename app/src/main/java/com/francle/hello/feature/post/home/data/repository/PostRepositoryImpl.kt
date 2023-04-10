package com.francle.hello.feature.post.home.data.repository

import com.francle.hello.R
import com.francle.hello.core.data.util.call.Resource
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.feature.home.data.api.PostApi
import com.francle.hello.feature.home.domain.models.Post
import com.francle.hello.feature.home.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class PostRepositoryImpl(
    private val api: PostApi
) : PostRepository {
    override fun getPost(postId: String): Flow<Resource<Post?>> {
        return flow {
            try {
                val response = api.getPost(postId)?.post?.toPost()
                emit(Resource.Success(response))
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    emit(
                        Resource.Error(
                            data = null,
                            message = UiText.StringResource(R.string.post_not_found)
                        )
                    )
                } else {
                    emit(
                        Resource.Error(
                            data = null,
                            message = UiText.StringResource(R.string.something_went_wrong)
                        )
                    )
                }
            } catch (e: Exception) {
                emit(
                    Resource.Error(
                        data = null,
                        message = UiText.StringResource(R.string.something_went_wrong)
                    )
                )
            }
        }
    }

    override fun getPosts(
        userId: String,
        page: Int,
        pageSize: Int
    ): Flow<Resource<List<Post?>?>> {
        return flow {
            try {
                val response = api.getPosts(
                    userId = userId,
                    page = page,
                    pageSize = pageSize
                )
                    ?.posts
                    ?.map { it?.toPost() }
                emit(Resource.Success(response))
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    emit(
                        Resource.Error(
                            data = null,
                            message = UiText.StringResource(R.string.post_not_found)
                        )
                    )
                } else {
                    emit(
                        Resource.Error(
                            data = null,
                            message = UiText.StringResource(R.string.something_went_wrong)
                        )
                    )
                }
            } catch (e: Exception) {
                emit(
                    Resource.Error(
                        data = null,
                        message = UiText.StringResource(R.string.something_went_wrong)
                    )
                )
            }
        }
    }

    override suspend fun deletePostByPostId(postId: String): Resource<Unit> {
        return try {
            api.deletePostByPostId(postId)
            Resource.Success()
        } catch (e: HttpException) {
            if (e.code() == 404) {
                Resource.Error(
                    data = null,
                    message = UiText.StringResource(R.string.post_not_found)
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

    override suspend fun deletePostsByUserId(userId: String): Resource<Unit> {
        return try {
            api.deletePostByPostId(userId)
            Resource.Success()
        } catch (e: HttpException) {
            if (e.code() == 404) {
                Resource.Error(
                    data = null,
                    message = UiText.StringResource(R.string.post_not_found)
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
}
