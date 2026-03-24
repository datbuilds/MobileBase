package vn.shb.cam.screens.beneficiary.helper

import vn.shb.data.entities.beneficiary.Beneficiary

interface ActionEditBeneficiary {
    fun edit(item: Beneficiary)
    fun delete(item: Beneficiary)
}