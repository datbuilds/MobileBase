package vn.shb.lao.screens.home

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import org.koin.android.ext.android.inject
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.getInitials
import vn.shb.data.entities.login.UserConverters
import vn.shb.lao.R
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.FragmentHomeBinding
import vn.shb.lao.screens.home.helper.LoopingAdapter
import vn.shb.lao.utils.extensions.getTextWelcomeUser

class HomeFragment : BaseFragmentBinding<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val homeViewModel: HomeViewModel by inject()

    private lateinit var adapter: LoopingAdapter
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var autoRunnable: Runnable
    private lateinit var pageCallback: ViewPager2.OnPageChangeCallback

    companion object {
        const val AUTO_SCROLL_BANNER_DELAY = 2000L
    }

    override fun initView(view: View) {
        bindView()
        mapUserInfo()
        bindBannerView()
    }

    private fun bindBannerView() {
        val images = homeViewModel.getListBanner()
        adapter = LoopingAdapter(images)
        binding.viewPager.adapter = adapter

        // --- cho phép xem 1 phần ảnh sau ---
        val pageMargin = resources.getDimensionPixelOffset(R.dimen.pageMargin)
        val pageOffset = resources.getDimensionPixelOffset(R.dimen.pageOffset)

        binding.viewPager.offscreenPageLimit = 3

        // allow children to draw outside
        val recyclerView = binding.viewPager.getChildAt(0) as RecyclerView
        recyclerView.clipToPadding = false
        recyclerView.clipChildren = false
        binding.viewPager.clipToPadding = false
        binding.viewPager.clipChildren = false

        // padding để lộ phần ảnh kế
        binding.viewPager.setPadding(pageOffset, 0, pageOffset, 0)

        // transformer để tạo khoảng cách / hiệu ứng
        binding.viewPager.setPageTransformer { page, position ->
            val offset = position * -(2 * pageOffset + pageMargin)
            page.translationX = offset
        }

        // Bắt đầu ở khối giữa (đảm bảo có trống trước/sau)
        val start = adapter.getMiddlePosition()
        binding.viewPager.setCurrentItem(start, false)

        // --- Đăng ký callback: khi vào khối đầu/cuối thì nhảy vào khối giữa (no animation) ---
        pageCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val real = adapter.getRealCount()
                // nếu đi tới khối sau cùng -> nhảy về tương ứng trong khối giữa
                if (position >= real * 2) {
                    val newPos = position - real
                    binding.viewPager.setCurrentItem(newPos, false)
                } else if (position < real) {
                    // nếu đi tới khối trước cùng -> nhảy về tương ứng trong khối giữa
                    val newPos = position + real
                    binding.viewPager.setCurrentItem(newPos, false)
                }
            }
        }
        binding.viewPager.registerOnPageChangeCallback(pageCallback)

        // --- Auto-scroll ---
        autoRunnable = object : Runnable {
            override fun run() {
                // next item, callback trên sẽ tự điều chỉnh nếu cần
                val next = binding.viewPager.currentItem + 1
                binding.viewPager.setCurrentItem(next, true)
                handler.postDelayed(this, AUTO_SCROLL_BANNER_DELAY)
            }
        }
        handler.postDelayed(autoRunnable, AUTO_SCROLL_BANNER_DELAY)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(autoRunnable)
        binding.viewPager.unregisterOnPageChangeCallback(pageCallback)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(autoRunnable, AUTO_SCROLL_BANNER_DELAY)
        binding.viewPager.registerOnPageChangeCallback(pageCallback)
    }


    private fun bindView() {
        binding.apply {
            tvHelloUser.text = context!!.getTextWelcomeUser()
            incItemTransfer.apply {
                ivIconFeature.setImageResource(R.drawable.ic_transfer)
                tvTitleFeature.text = getString(R.string.transfer)
            }
            incItemAccounts.apply {
                ivIconFeature.setImageResource(R.drawable.ic_accounts)
                tvTitleFeature.text = getString(R.string.accounts)
            }
        }


    }

    private fun mapUserInfo() {
        UserConverters.stringToUserInfo(storage.getUserInfo())?.let { user ->
            binding.tvNameUser.text = user.username
            binding.flAvatarUser.setUserName("", user.username.getInitials())
        }
    }

    override fun initListener() {
        with(binding) {
            flAvatarUser.setListener(
                object : vn.shb.lao.screens.home.widget.OnClickDetail {
                    override fun onAvatarClick() {
                        safeNavigate(R.id.homeFragment, R.id.action_homeFragment_to_profileFragment)
                    }
                })

            incItemTransfer.root.setOnSingleClickListener {

            }
            incItemAccounts.root.setOnSingleClickListener {

            }
        }
    }

    override fun initObserve() {}
}
