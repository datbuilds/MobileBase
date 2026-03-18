package vn.shb.cam.screens.beneficiary

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import vn.shb.core.core.delivery.Reason
import vn.shb.core.core.delivery.onFailure
import vn.shb.core.core.delivery.onLoading
import vn.shb.core.core.delivery.onSuccess
import vn.shb.core.core.domain.source.request.BeneficiaryRequest
import vn.shb.core.core.domain.source.request.ValidateAccountRequest
import vn.shb.core.core.domain.source.response.ValidateAccountResponse
import vn.shb.core.core.domain.usecases.None
import vn.shb.core.core.domain.usecases.beneficiary.GetBeneficiariesUseCase
import vn.shb.data.entities.beneficiary.Beneficiary
import vn.shb.cam.base.BaseViewModel
import vn.shb.core.core.delivery.ResultSHB
import vn.shb.core.core.domain.source.request.RemoveBeneficiaryRequest

import vn.shb.core.core.domain.usecases.beneficiary.CreateBeneficiaryUseCase
import vn.shb.core.core.domain.usecases.beneficiary.DeleteBeneficiaryUseCase
import vn.shb.core.core.domain.usecases.beneficiary.GetBanksUseCase
import vn.shb.core.core.domain.usecases.beneficiary.UpdateBeneficiaryUseCase
import vn.shb.core.core.domain.usecases.beneficiary.ValidateAccountUseCase
import vn.shb.data.entities.beneficiary.Bank

class BeneficiaryViewModel(
    private val useCaseGetBeneficiaries: GetBeneficiariesUseCase,
    private val useCaseGetBanks: GetBanksUseCase,
    private val useCaseDeleteBeneficiary: DeleteBeneficiaryUseCase,
    private val useCaseCreateBeneficiary: CreateBeneficiaryUseCase,
    private val useCaseUpdateBeneficiary: UpdateBeneficiaryUseCase,
    private val useCaseValidateAccount: ValidateAccountUseCase
) : BaseViewModel() {

    private val _localBeneficiaries = MutableStateFlow<List<Beneficiary>>(emptyList())

    private val _stateBanks = MutableStateFlow<List<Bank>>(emptyList())
    val stateBanks: StateFlow<List<Bank>> = _stateBanks

    private val _stateDelete = MutableSharedFlow<Boolean?>()
    val stateDelete: SharedFlow<Boolean?> = _stateDelete

    private val _stateAction = MutableSharedFlow<Boolean?>()
    val stateAction: SharedFlow<Boolean?> = _stateAction

    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateValidateAccount =
        MutableStateFlow<ResultSHB<ValidateAccountResponse.ValidateAccountData>>(ResultSHB.Loading)
    val stateValidateAccount: StateFlow<ResultSHB<ValidateAccountResponse.ValidateAccountData>> = _stateValidateAccount

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
                        _stateDelete.emit(false)
                        // Handle failure if needed, baseViewModel might handle error toast if configured, but here we just update state
                    }
                    result.onLoading {
                        showLoading(true)
                    }
                }
        }
    }


    fun createBeneficiary(request: BeneficiaryRequest, isRefresh: Boolean = true) {
        viewModelScope.launch {
            useCaseCreateBeneficiary.invoke(request).collect { result ->
                result.onSuccess {
                    _stateAction.emit(true)
                    if (isRefresh) {
                        getAllBeneficiary()
                    }
                }
                result.onFailure {
                    _stateError.emit(it)
                }
            }
        }
    }

    fun updateBeneficiary(request: BeneficiaryRequest) {
        viewModelScope.launch {
            useCaseUpdateBeneficiary.invoke(UpdateBeneficiaryUseCase.Params(request))
                .collect { result ->
                    result.onSuccess {
                        _stateAction.emit(true)
                        getAllBeneficiary()
                    }
                    result.onFailure {
                        _stateError.emit(it)
                    }
                }
        }
    }

    fun validateAccount(accountNumber: String, bankCode: String) {
        viewModelScope.launch {
            val request = ValidateAccountRequest(accountNumber, bankCode)
            useCaseValidateAccount(request).collect { result ->
                result.onLoading {
                    _stateValidateAccount.value = ResultSHB.Loading
                }

                result.onSuccess { response ->
                    if (response.data != null) {
                        _stateValidateAccount.value = ResultSHB.Success(response.data!!)
                    }
                }
                result.onFailure {
                    _stateValidateAccount.value = ResultSHB.Failure(it)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        _stateLoading.value = isLoading
    }

}