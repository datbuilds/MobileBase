package com.mobile.base.screens.paste2pay.dialog

import android.content.DialogInterface
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import org.koin.android.ext.android.inject
import com.mobile.base.R
import com.mobile.base.base.BaseBottomDialogBinding
import com.mobile.base.databinding.BsAiIntroductionBinding
import com.mobile.base.screens.paste2pay.dialog.adapter.PagerAdapter
import com.mobile.base.utils.extensions.CustomCountdownTimer
import com.mobile.base.utils.extensions.setOnMaterialButtonClick
import com.mobile.base.core.core.security.encrypt.AndroidSecureStorage
import java.io.Serializable

class AiIntroductionDialog(private val builder: Builder) :
    BaseBottomDialogBinding<BsAiIntroductionBinding>(BsAiIntroductionBinding::inflate) {

    companion object {
        const val TAG = "AiIntroductionDialog"
    }

    class Builder {
        fun build() = AiIntroductionDialog(this)
    }

    private val storage: AndroidSecureStorage by inject()
    private var viewPager: ViewPager2? = null

    private val onboards = listOf(
        OnAIChartEntity(R.drawable.img_ai_chat_intro_1),
        OnAIChartEntity(R.drawable.img_ai_chat_intro_2),
        OnAIChartEntity(R.drawable.img_ai_chat_intro_3),
        OnAIChartEntity(R.drawable.img_ai_chat_intro_4)
    )

    private val pageAdapter: PagerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        PagerAdapter(onboards)
    }

    private var countdownTimer: CustomCountdownTimer? = null
    private var currentPosition = 0
    private val lastPageIndex: Int
        get() = onboards.lastIndex

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            currentPosition = position
            binding.dot.selectPage(position)
            updateAutoScrollState()
        }
    }

    override fun initView(view: View) {
        viewPager = binding.viewPager

        viewPager?.apply {
            adapter = pageAdapter
            registerOnPageChangeCallback(pageChangeCallback)
            binding.dot.setupViewPager(this)
            isSaveEnabled = false
            isUserInputEnabled = true
        }

        updateAutoScrollState()
    }

    private fun stopAutoScroll() {
        countdownTimer?.stop()
    }

    private fun updateAutoScrollState() {
        if (currentPosition >= lastPageIndex) {
            stopAutoScroll()
            return
        }
        startCountDownTimer()
    }

    private fun startCountDownTimer() {
        if (countdownTimer != null) {
            countdownTimer?.stop()
        }
        countdownTimer = CustomCountdownTimer(
            totalTimeMillis = 5 * 1000L,
            intervalMillis = 1000,
            onFinishAction = {
                if (currentPosition < lastPageIndex) {
                    currentPosition++
                    viewPager?.setCurrentItem(currentPosition, true)
                }
            })
        countdownTimer?.start()
    }

    override fun initListener() {
        binding.btnSkip.setOnMaterialButtonClick {
            nextAction()
        }
    }

    override fun initObserve() {
    }

    private fun nextAction() {
        storage.setAiChatIntroCompleted(true)
        dismissAllowingStateLoss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        stopAutoScroll()
        countdownTimer?.stop()
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onDismiss(dialog)
    }
}

data class OnAIChartEntity(val imageRes: Int) : Serializable
