package vn.shb.lao.screens.beneficiary

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vn.shb.core.core.delivery.onFailure
import vn.shb.core.core.delivery.onSuccess
import vn.shb.core.core.domain.usecases.None
import vn.shb.core.core.domain.usecases.beneficiary.GetBeneficiariesUseCase
import vn.shb.data.entities.beneficiary.Beneficiary
import vn.shb.lao.base.BaseViewModel

class BeneficiaryViewModel(
    private val useCaseGetBeneficiaries: GetBeneficiariesUseCase
) : BaseViewModel() {

    private val _stateAllBeneficiary = MutableStateFlow<List<Beneficiary>>(emptyList())
    val stateAllBeneficiary = _stateAllBeneficiary.asStateFlow()

    fun getAllBeneficiary() {
        viewModelScope.launch {
            useCaseGetBeneficiaries.invoke(None).collect { result ->
                result.onSuccess { list ->
                    _stateAllBeneficiary.value = list
                }
                result.onFailure {
                    _stateAllBeneficiary.value = arrayListOf()
                }
            }
        }
    }
}