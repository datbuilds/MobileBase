package com.mobile.base.screens.beneficiary.helper

import com.mobile.base.data.entities.beneficiary.Beneficiary

interface ActionEditBeneficiary {
    fun edit(item: Beneficiary)
    fun delete(item: Beneficiary)
}