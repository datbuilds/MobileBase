package com.mobile.base.screens.beneficiary

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.mobile.base.base.BaseViewModel
import com.mobile.base.core.core.delivery.Reason
import com.mobile.base.core.core.delivery.ResultState
import com.mobile.base.core.core.delivery.onFailure
import com.mobile.base.core.core.delivery.onLoading
import com.mobile.base.core.core.delivery.onSuccess
import com.mobile.base.core.core.domain.source.request.BeneficiaryRequest
import com.mobile.base.core.core.domain.source.request.RemoveBeneficiaryRequest
import com.mobile.base.core.core.domain.source.request.ValidateAccountRequest
import com.mobile.base.core.core.domain.source.response.ValidateAccountResponse
import com.mobile.base.core.core.domain.usecases.None
import com.mobile.base.core.core.domain.usecases.beneficiary.CreateBeneficiaryUseCase
import com.mobile.base.core.core.domain.usecases.beneficiary.DeleteBeneficiaryUseCase
import com.mobile.base.core.core.domain.usecases.beneficiary.GetBanksUseCase
import com.mobile.base.core.core.domain.usecases.beneficiary.GetBeneficiariesUseCase
import com.mobile.base.core.core.domain.usecases.beneficiary.UpdateBeneficiaryUseCase
import com.mobile.base.core.core.domain.usecases.beneficiary.ValidateAccountUseCase
import com.mobile.base.data.entities.beneficiary.Bank
import com.mobile.base.data.entities.beneficiary.Beneficiary

class BeneficiaryViewModel(
    private val useCaseGetBeneficiaries: GetBeneficiariesUseCase,
    private val useCaseGetBanks: GetBanksUseCase,
    private val useCaseDeleteBeneficiary: DeleteBeneficiaryUseCase,
    private val useCaseCreateBeneficiary: CreateBeneficiaryUseCase,
    private val useCaseUpdateBeneficiary: UpdateBeneficiaryUseCase,
    private val useCaseValidateAccount: ValidateAccountUseCase
) : BaseViewModel() {

    private val _localBeneficiaries = MutableStateFlow<List<Beneficiary>>(emptyList())
    val localBeneficiaries: StateFlow<List<Beneficiary>> = _localBeneficiaries
    private val _stateBanks = MutableStateFlow<List<Bank>>(emptyList())
    val stateBanks: StateFlow<List<Bank>> = _stateBanks

    private val _stateDelete = MutableSharedFlow<Boolean?>()
    val stateDelete: SharedFlow<Boolean?> = _stateDelete

    private val _stateAction = MutableSharedFlow<Boolean?>()
    val stateAction: SharedFlow<Boolean?> = _stateAction

    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateValidateAccount =
        MutableStateFlow<ResultState<ValidateAccountResponse.ValidateAccountData>>(ResultState.Loading)
    val stateValidateAccount: StateFlow<ResultState<ValidateAccountResponse.ValidateAccountData>> =
        _stateValidateAccount

    private val _stateError = MutableSharedFlow<Reason>()
    val stateError = _stateError

    val stateAllBeneficiary = combine(_localBeneficiaries, _stateBanks) { beneficiaries, banks ->
        if (banks.isEmpty() || beneficiaries.isEmpty()) {
            beneficiaries
        } else {
            beneficiaries.map { beneficiary ->
                val bank = banks.find { it.bankCode == beneficiary.bankCode }
                if (bank != null) {
                    beneficiary.copy(bankName = bank.bankName)
                } else {
                    beneficiary
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(7000), emptyList())

    fun getAllBeneficiary() {
        viewModelScope.launch {
            useCaseGetBeneficiaries.invoke(None).collect { result ->
                result.onSuccess { list ->
                    _localBeneficiaries.value = list
                }
                result.onFailure {
                    _localBeneficiaries.value = arrayListOf()
                }
            }
        }
    }

    fun getBanks() {
        viewModelScope.launch {
            useCaseGetBanks.invoke(None).collect { result ->
                result.onSuccess { list ->
                    _stateBanks.value = list
                }
                result.onFailure {
                    // Handle failure
                }
            }
        }
    }

    fun deleteBeneficiary(beneficiary: Beneficiary) {
        viewModelScope.launch {
            val bodyRequest =
                RemoveBeneficiaryRequest(beneficiary.accountNumber, beneficiary.bankCode)

            useCaseDeleteBeneficiary.invoke(DeleteBeneficiaryUseCase.Params(bodyRequest))
                .collect { result ->
                    result.onSuccess {
                        showLoading(false)
                        _stateDelete.emit(true)
                        // Refresh list or remove item locally
                        getAllBeneficiary()
                    }
                    result.onFailure {
                        showLoading(false)
                        _stateError.emit(it)
                        _stateDelete.emit(false)
                        // Handle failure if needed, baseViewModel might handle error toast if configured, but here we just update state
                    }
                    result.onLoading {
                        showLoading(true)
                    }
                }
        }
    }


    fun createBeneficiary(request: BeneficiaryRequest, isRefresh: Boolean = true,onFinish:()->Unit={},onLoading:()-> Unit={}) {
        viewModelScope.launch {
            useCaseCreateBeneficiary.invoke(request).collect { result ->
                result.onSuccess {
                    onFinish.invoke()
                    _stateAction.emit(true)
                    if (isRefresh) {
                        getAllBeneficiary()
                    }
                }
                result.onFailure {
                    onFinish.invoke()
                    _stateError.emit(it)
                }
                result.onLoading {
                    showLoading(true)
                }
            }
        }
    }

    fun updateBeneficiary(request: BeneficiaryRequest,onFinish:()->Unit={},onLoading:()-> Unit={}) {
        viewModelScope.launch {
            useCaseUpdateBeneficiary.invoke(UpdateBeneficiaryUseCase.Params(request))
                .collect { result ->
                    result.onSuccess {
                        onFinish.invoke()
                        _stateAction.emit(true)
                        getAllBeneficiary()
                    }
                    result.onFailure {
                        onFinish.invoke()
                        _stateError.emit(it)
                    }
                    result.onLoading { onLoading.invoke() }
                }
        }
    }

    fun validateAccount(accountNumber: String, bankCode: String) {
        viewModelScope.launch {
            val request = ValidateAccountRequest(accountNumber, bankCode)
            useCaseValidateAccount(request).collect { result ->
                result.onLoading {
                    _stateValidateAccount.value = ResultState.Loading
                }

                result.onSuccess { response ->
                    if (response.data != null) {
                        _stateValidateAccount.value = ResultState.Success(response.data!!)
                    }
                }
                result.onFailure {
                    _stateValidateAccount.value = ResultState.Failure(it)
                    _stateError.emit(it)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        _stateLoading.value = isLoading
    }

}