package com.francle.hello.feature.pair.data.repository

import com.francle.hello.R
import com.francle.hello.core.data.call.Resource
import com.francle.hello.core.ui.util.UiText
import com.francle.hello.feature.pair.data.api.PairApi
import com.francle.hello.feature.pair.domain.models.PairUser
import com.francle.hello.feature.pair.domain.repository.PairRepository
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class PairRepositoryImpl(
    private val pairApi: PairApi
) : PairRepository {
    override fun getPairUser(): Flow<Resource<PairUser?>> {
        return flow {
            try {
                val response = pairApi.getPairUser()?.toPairUser()
                emit(Resource.Success(data = response))
            } catch (e: HttpException) {
                emit(
                    Resource.Error(
                        message = UiText.StringResource(R.string.an_unknown_error_occurred)
                    )
                )
            } catch (e: IOException) {
                emit(
                    Resource.Error(
                        message = UiText.StringResource(R.string.an_unknown_error_occurred)
                    )
                )
            }
        }
    }
}
