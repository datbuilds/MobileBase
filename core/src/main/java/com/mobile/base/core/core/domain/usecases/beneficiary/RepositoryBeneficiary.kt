package com.mobile.base.core.core.domain.usecases.beneficiary

import com.mobile.base.core.core.delivery.ActionDone
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.domain.source.request.BeneficiaryRequest
import com.mobile.base.core.core.domain.source.request.RemoveBeneficiaryRequest
import com.mobile.base.core.core.domain.source.response.BeneficiaryResponse
import com.mobile.base.data.entities.beneficiary.Bank
import com.mobile.base.data.entities.beneficiary.Beneficiary

import com.mobile.base.core.core.domain.source.request.ValidateAccountRequest
import com.mobile.base.core.core.domain.source.response.ValidateAccountResponse

interface RepositoryBeneficiary {
    suspend fun getBeneficiaries(): ResultState<List<Beneficiary>>
    suspend fun getBanks(): ResultState<List<Bank>>
    suspend fun createBeneficiary(request: BeneficiaryRequest): ResultState<com.mobile.base.core.core.domain.source.response.BeneficiaryResponse>
    suspend fun updateBeneficiary(
        request: BeneficiaryRequest
    ): ResultState<BeneficiaryResponse>

    suspend fun validateAccount(request: ValidateAccountRequest): ResultState<ValidateAccountResponse>

    suspend fun deleteBeneficiary(request: RemoveBeneficiaryRequest): ResultState<ActionDone>
}
