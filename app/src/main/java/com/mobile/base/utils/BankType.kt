package com.mobile.base.utils

import com.mobile.base.R

enum class BankType(val code: String, val iconRes: Int) {
    ACLE("ACLE", R.drawable.ic_mobile_base),
    APB("APB", R.drawable.ic_mobile_base),
    BCEL("BCEL", R.drawable.ic_mobile_base),
    BFL("BFL", R.drawable.ic_mobile_base),
    BIC("BIC", R.drawable.ic_mobile_base),
    BOC("BOC", R.drawable.ic_mobile_base),
    ICBC("ICBC", R.drawable.ic_mobile_base),
    IDB("IDB", R.drawable.ic_mobile_base),
    JDB("JDB", R.drawable.ic_mobile_base),
    KBANK("KBANK", R.drawable.ic_mobile_base),
    LDB("LDB", R.drawable.ic_mobile_base),
    LVB("LVB", R.drawable.ic_mobile_base),
    MJB("MJB", R.drawable.ic_mobile_base),
    PSV("PSV", R.drawable.ic_mobile_base),
    SACOM("SACOM", R.drawable.ic_mobile_base),
    STB("STB", R.drawable.ic_mobile_base),
    VMB("VMB", R.drawable.ic_mobile_base),
    VTB("VTB", R.drawable.ic_mobile_base),
    // New banks
    LCNB("LCNB", R.drawable.ic_mobile_base),
    UMONEY_MB("Umoney.MB", R.drawable.ic_mobile_base),
    MMONEY_JDB("Mmoney.JDB", R.drawable.ic_mobile_base),
    VCBLAO("VCBLAO", R.drawable.ic_mobile_base),
    LBB("LBB", R.drawable.ic_mobile_base),
    PBLL("PBLL", R.drawable.ic_mobile_base),
    Base("Base", R.drawable.ic_mobile_base);

    companion object {
        fun getIconByCode(code: String?): Int {
            return values().find { it.code == code }?.iconRes ?: R.drawable.ic_avatar_default
        }
    }
}
