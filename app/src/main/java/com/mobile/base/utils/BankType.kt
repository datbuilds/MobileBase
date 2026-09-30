package com.mobile.base.utils

import com.mobile.base.R

enum class BankType(val code: String, val iconRes: Int) {
    ACLE("ACLE", R.drawable.ic_bank_acle),
    APB("APB", R.drawable.ic_bank_apb),
    BCEL("BCEL", R.drawable.ic_bank_bcel),
    BFL("BFL", R.drawable.ic_bank_bfl),
    BIC("BIC", R.drawable.ic_bank_bic),
    BOC("BOC", R.drawable.ic_bank_boc),
    ICBC("ICBC", R.drawable.ic_bank_icbc),
    IDB("IDB", R.drawable.ic_bank_idb),
    JDB("JDB", R.drawable.ic_bank_jdb),
    KBANK("KBANK", R.drawable.ic_bank_kbank),
    LDB("LDB", R.drawable.ic_bank_ldb),
    LVB("LVB", R.drawable.ic_bank_lvb),
    MJB("MJB", R.drawable.ic_bank_mjb),
    PSV("PSV", R.drawable.ic_bank_psv),
    SACOM("SACOM", R.drawable.ic_bank_sacom),
    STB("STB", R.drawable.ic_bank_stb),
    VMB("VMB", R.drawable.ic_bank_mb),
    VTB("VTB", R.drawable.ic_bank_vtb),
    // New banks
    LCNB("LCNB", R.drawable.ic_bank_lcnb),
    UMONEY_MB("Umoney.MB", R.drawable.ic_bank_mb),
    MMONEY_JDB("Mmoney.JDB", R.drawable.ic_bank_jdb),
    VCBLAO("VCBLAO", R.drawable.ic_bank_vcb_lao),
    LBB("LBB", R.drawable.ic_bank_lbb),
    PBLL("PBLL", R.drawable.ic_bank_pbll),
    Base("Base", R.drawable.ic_mobile_base);

    companion object {
        fun getIconByCode(code: String?): Int {
            return values().find { it.code == code }?.iconRes ?: R.drawable.ic_avatar_default
        }
    }
}
