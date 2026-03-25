package vn.shb.cam.screens.paste2pay.dialog

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import vn.shb.cam.base.BaseBottomDialogBinding
import vn.shb.cam.databinding.BsAiResultBinding
import vn.shb.cam.utils.BankType
import vn.shb.cam.utils.extensions.setOnMaterialButtonClick
import vn.shb.cam.utils.extensions.visibleWhenTrue
import vn.shb.core.core.domain.source.response.AiPayResult
import vn.shb.data.entities.getBalanceFormatted

class BSAiResult(private val builder: Builder) :
    BaseBottomDialogBinding<BsAiResultBinding>(BsAiResultBinding::inflate) {

    companion object {
        const val TAG = "BSAiResult"
        private const val EMPTY_VALUE = ""
        private const val BANK_ICON_SIZE_DP = 25
    }

    class Builder {
        var result: AiPayResult = AiPayResult()
        var onContinue: (() -> Unit)? = null
        var onClose: (() -> Unit)? = null

        fun setResult(result: AiPayResult) = apply {
            this.result = result
        }

        fun setOnContinue(onContinue: (() -> Unit)?) = apply {
            this.onContinue = onContinue
        }

        fun setOnClose(onClose: (() -> Unit)?) = apply {
            this.onClose = onClose
        }

        fun build() = BSAiResult(this)
    }

    override fun initView(view: View) {
        isCancelable = false
        with(binding) {
            val bank = builder.result.shortName
                ?.takeIf { it.isNotBlank() }
                ?: EMPTY_VALUE
            val account = builder.result.accountNum
                ?.takeIf { it.isNotBlank() }
                ?: EMPTY_VALUE
            val amount = builder.result.amount
                ?.stripTrailingZeros()
                ?.toPlainString()
                ?.getBalanceFormatted()
                ?: EMPTY_VALUE
            val currency = builder.result.currency
                ?.takeIf { it.isNotBlank() }
                ?: EMPTY_VALUE
            val remark = builder.result.remark
                ?.takeIf { it.isNotBlank() }
                ?: EMPTY_VALUE
            setBankIcon(tvBankValue, resolveBankIcon(bank))
            tvBankValue.text = bank
            tvAccountValue.text = account
            tvAmountValue.text = amount
            tvCurrencyValue.text = currency
            tvRemarkValue.text = remark

            tvCurrencyValue.visibleWhenTrue(currency.isNotEmpty())
        }
    }

    override fun initListener() {
        binding.btClose.setOnMaterialButtonClick {
            dismissAllowingStateLoss()
            builder.onClose?.invoke()
        }
        binding.btContinue.setOnMaterialButtonClick {
            dismissAllowingStateLoss()
            builder.onContinue?.invoke()
        }
    }

    override fun initObserve() {
    }

    private fun resolveBankIcon(bankCode: String): Int {
        return BankType.entries.firstOrNull { it.code.equals(bankCode, ignoreCase = true) }
            ?.iconRes
            ?: BankType.SHB.iconRes
    }

    private fun setBankIcon(textView: TextView, iconRes: Int) {
        val drawable = ContextCompat.getDrawable(textView.context, iconRes) ?: return
        val iconSizePx = (BANK_ICON_SIZE_DP * textView.resources.displayMetrics.density).toInt()
        drawable.setBounds(0, 0, iconSizePx, iconSizePx)
        textView.setCompoundDrawablesRelative(drawable, null, null, null)
    }
}
