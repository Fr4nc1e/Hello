package com.francle.hello.feature.pair.domain.repository

import com.francle.hello.core.data.call.Resource
import com.francle.hello.feature.pair.domain.models.PairUser
import kotlinx.coroutines.flow.Flow

interface PairRepository {
    fun getPairUser(): Flow<Resource<PairUser?>>
}
