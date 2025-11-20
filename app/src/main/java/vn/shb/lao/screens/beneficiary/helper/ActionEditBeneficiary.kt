package vn.shb.lao.screens.beneficiary.helper

import vn.shb.data.entities.home.beneficiary.BeneficiaryUser

interface ActionEditBeneficiary {
    fun edit(item: BeneficiaryUser)
    fun delete(item: BeneficiaryUser)
}