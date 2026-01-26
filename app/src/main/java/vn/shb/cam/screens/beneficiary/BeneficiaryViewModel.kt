package vn.shb.cam.screens.beneficiary

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import vn.shb.core.core.delivery.Reason
import vn.shb.core.core.delivery.onFailure
import vn.shb.core.core.delivery.onSuccess
import vn.shb.core.core.domain.usecases.None
import vn.shb.core.core.domain.usecases.beneficiary.GetBeneficiariesUseCase
import vn.shb.data.entities.beneficiary.Beneficiary
import vn.shb.cam.base.BaseViewModel

import vn.shb.core.core.domain.usecases.beneficiary.CreateBeneficiaryUseCase
import vn.shb.core.core.domain.usecases.beneficiary.UpdateBeneficiaryUseCase
import vn.shb.core.core.domain.source.request.BeneficiaryRequest
import vn.shb.core.core.domain.usecases.beneficiary.DeleteBeneficiaryUseCase
import vn.shb.core.core.domain.usecases.beneficiary.GetBanksUseCase
import vn.shb.data.entities.beneficiary.Bank

class BeneficiaryViewModel(
    private val useCaseGetBeneficiaries: GetBeneficiariesUseCase,
    private val useCaseGetBanks: GetBanksUseCase,
    private val useCaseDeleteBeneficiary: DeleteBeneficiaryUseCase,
    private val useCaseCreateBeneficiary: CreateBeneficiaryUseCase,
    private val useCaseUpdateBeneficiary: UpdateBeneficiaryUseCase
) : BaseViewModel() {

    private val _localBeneficiaries = MutableStateFlow<List<Beneficiary>>(emptyList())

    private val _stateBanks = MutableStateFlow<List<Bank>>(emptyList())
    val stateBanks = _stateBanks.asStateFlow()

    private val _stateDelete = MutableStateFlow<Boolean?>(null)
    val stateDelete = _stateDelete.asStateFlow()

    private val _stateAction = MutableStateFlow<Boolean?>(null)
    val stateAction = _stateAction.asStateFlow()

    private val _stateError = Channel<Reason>(Channel.BUFFERED)
    val stateError = _stateError.receiveAsFlow()

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
//            showLoading(true)
            useCaseDeleteBeneficiary.invoke(DeleteBeneficiaryUseCase.Params(beneficiary.id.toString())).collect { result ->
//                showLoading(false)
                result.onSuccess {
                    _stateDelete.value = true
                    // Refresh list or remove item locally
                    getAllBeneficiary()
                }
                result.onFailure {
                    _stateDelete.value = false
                    // Handle failure if needed, baseViewModel might handle error toast if configured, but here we just update state
                }
            }
        }
    }
    
    fun resetActionState() {
        _stateAction.value = null
    }

    fun createBeneficiary(request: BeneficiaryRequest, isRefresh: Boolean = true) {
        viewModelScope.launch {
//            showLoading(true)
            useCaseCreateBeneficiary.invoke(request).collect { result ->
//                showLoading(false)
                result.onSuccess {
                    _stateAction.value = true
                    if (isRefresh){
                        getAllBeneficiary()
                    }
                }
                result.onFailure {
                    _stateError.send(it)
                }
            }
        }
    }

    fun updateBeneficiary(id: String, request: BeneficiaryRequest) {
        viewModelScope.launch {
//            showLoading(true)
            useCaseUpdateBeneficiary.invoke(UpdateBeneficiaryUseCase.Params(id, request)).collect { result ->
//                showLoading(false)
                result.onSuccess {
                    _stateAction.value = true
                    getAllBeneficiary()
                }
                result.onFailure {
                    _stateAction.value = false
//                    handleError(it)
                }
            }
        }
    }
}