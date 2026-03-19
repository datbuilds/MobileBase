package vn.shb.cam.screens.paste2pay.dialog

import android.content.DialogInterface
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import org.koin.android.ext.android.inject
import vn.shb.cam.R
import vn.shb.cam.base.BaseBottomDialogBinding
import vn.shb.cam.databinding.BsAiIntroductionBinding
import vn.shb.cam.screens.paste2pay.dialog.adapter.PagerAdapter
import vn.shb.cam.utils.extensions.CustomCountdownTimer
import vn.shb.cam.utils.extensions.setOnMaterialButtonClick
import vn.shb.core.core.security.encrypt.AndroidSecureStorage
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

    private val pageAdapter: PagerAdapter by lazy {
        PagerAdapter(onboards)
    }

    private var onboards = listOf<OnAIChartEntity>()

    private var countdownTimer: CustomCountdownTimer? = null
    private var isDone = false
    private var currentPosition = 0
    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            currentPosition = position
            binding.dot.selectPage(position)

            countdownTimer?.reset()
            if (isDone) {
                countdownTimer?.stop()
            } else countdownTimer?.start()

            if (currentPosition == onboards.size - 1) {
                isDone = true
            }
        }
    }

    override fun initView(view: View) {
        onboards = prepareOnboards()

        viewPager = binding.viewPager

        viewPager?.apply {
            adapter = pageAdapter
            registerOnPageChangeCallback(pageChangeCallback)
            binding.dot.setupViewPager(this)
            isSaveEnabled = false
            isUserInputEnabled = true
        }

        // Bắt đầu cuộn tự động
        startCountDownTimer()
    }

    private fun stopAutoScroll() {
        // Tạm dừng auto scroll ở đây
        countdownTimer?.stop()
    }

    private fun startCountDownTimer() {
        if (countdownTimer != null) {
            countdownTimer?.stop()
        }
        countdownTimer = CustomCountdownTimer(
            totalTimeMillis = 3 * 1000L, // Tổng thời gian đếm ngược (60 giây)
            intervalMillis = 1000,  // Khoảng thời gian giữa các tick (1 giây)
            onFinishAction = {
                if (currentPosition < onboards.size - 1) {
                    currentPosition++
                    viewPager?.setCurrentItem(currentPosition, true)
                }
            })
        countdownTimer?.start()
    }

    override fun initListener() {
        binding.btnSkip.setOnMaterialButtonClick {
            if (currentPosition < onboards.size - 1) {
                currentPosition++
                viewPager?.setCurrentItem(currentPosition, true)
            } else {
                nextAction()
            }
        }
    }

    override fun initObserve() {
    }

    private fun nextAction() {
        storage.setAiChatIntroCompleted(true)
        dismissAllowingStateLoss()
    }

    private fun prepareOnboards(): List<OnAIChartEntity> {
        return listOf(
            OnAIChartEntity(imageRes = R.drawable.img_ai_chat_intro_1),
            OnAIChartEntity(imageRes = R.drawable.img_ai_chat_intro_2),
            OnAIChartEntity(imageRes = R.drawable.img_ai_chat_intro_3),
            OnAIChartEntity(imageRes = R.drawable.img_ai_chat_intro_4),
        )
    }

    override fun onDismiss(dialog: DialogInterface) {
        stopAutoScroll()
        countdownTimer?.stop()
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onDismiss(dialog)
    }
}

data class OnAIChartEntity(val imageRes: Int) : Serializable
