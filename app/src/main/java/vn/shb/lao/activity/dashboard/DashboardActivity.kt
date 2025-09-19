package vn.shb.lao.activity.dashboard

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import vn.shb.lao.R
import vn.shb.lao.base.BaseActivity
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.databinding.ActivityDashboardBinding
import vn.shb.lao.screens.four.FourFragment
import vn.shb.lao.screens.two.TwoFragment
import vn.shb.lao.screens.three.ThreeFragment
import vn.shb.lao.screens.home.HomeFragment
import vn.shb.lao.utils.extensions.disableOverScrollMode
import vn.shb.lao.utils.extensions.fadeAndScaleToItem

class DashboardActivity :
    BaseActivity<ActivityDashboardBinding>(ActivityDashboardBinding::inflate) {

    companion object {
        @JvmStatic
        fun intent(context: Context): Intent {
            return Intent(context, DashboardActivity::class.java)
        }
    }

    override fun shouldStartRefreshTokenManager(): Boolean {
        return true // Start RefreshTokenManager vì user đã login
    }

    private var viewPagerAdapter: DashBoardViewPageAdapter? = null
    private var currentPosition = 0

    private var pageCallback: ViewPager2.OnPageChangeCallback =
        object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position

                val mode =
                    when (position) {
                        0 -> WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
                        1 -> WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
                        2 -> WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                        else -> WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
                    }

                window?.setSoftInputMode(mode)

                when (currentPosition) {
                    1 -> (currentFragment() as TwoFragment).resetUI()
                    2 -> (currentFragment() as ThreeFragment).resetUI()
                    3 -> (currentFragment() as FourFragment).resetUI()
                }
            }
        }

    private lateinit var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener

    override fun initView() {
        val fragments = listOf(
            HomeFragment.newInstance(),
            TwoFragment.newInstance(),
            ThreeFragment.newInstance(),
            FourFragment.newInstance(),
        )

        viewPagerAdapter = DashBoardViewPageAdapter(fragments, this@DashboardActivity)

        with(binding.viewPager) {
            isSaveEnabled = false
            isUserInputEnabled = false
            offscreenPageLimit = fragments.size - 1
            disableOverScrollMode()

            adapter = viewPagerAdapter

            globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
                viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
            }
            viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
            registerOnPageChangeCallback(pageCallback)
        }
    }

    override fun initListener() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> openHomeScreen()
                R.id.nav_two -> openTwoScreen()
                R.id.nav_three -> openThreeScreen()
                R.id.nav_four -> openFourScreen()
                else -> true
            }
        }

        //        binding.fabClose.setOnSingleClickListener {
        //            binding.chatBotView.collapse()
        //            binding.fabClose.gone()
        //        }

        //        handleTouchChatBotView()
        //        binding.chatBotView.setOnClickListener {
        //            openChatBotScreen()
        //        }
    }

    private fun animateBottomNavItem(itemId: Int) {
        val itemView = binding.bottomNavigationView.findViewById<View>(itemId)

        itemView?.let { view ->
            // Reset any previous animations
            view.animate().cancel()

            // Create a smooth bounce animation
            view.animate()
                .scaleX(1.15f)
                .scaleY(1.15f)
                .alpha(0.8f)
                .setDuration(200)
                .withEndAction {
                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(1f)
                        .setDuration(200)
                        .setInterpolator(OvershootInterpolator(1.2f))
                        .start()
                }
                .start()
        }
    }

//    @SuppressLint("ClickableViewAccessibility")
//    private fun handleTouchChatBotView() {
//        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
//        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
//        var dX = 0F
//        var dY = 0F
//
//        val minY = 0f
//        val maxY = (screenHeight - 350).toFloat()
//
//        binding.chatBotView.setOnTouchListener { v, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    dX = event.rawX - v.x
//                    dY = event.rawY - v.y
//                    true
//                }
//                MotionEvent.ACTION_MOVE -> {
//                    val newX = event.rawX - dX
//                    var newY = event.rawY - dY
//
//                    newY = newY.coerceIn(minY, maxY)
//
//                    v.animate().x(newX).y(newY).setDuration(0).start()
//                    true
//                }
//                MotionEvent.ACTION_UP -> {
//                    val finalX = event.rawX - dX
//                    val targetX =
//                            if (finalX < screenWidth / 2) 0f else (screenWidth - v.width).toFloat()
//
//                    v.animate().x(targetX).setDuration(200).start()
//                    true
//                }
//                else -> false
//            }
//        }
//    }

    //    private fun openChatBotScreen() {
    //        val accessToken =
    //            "Bearer
    // eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0cmFuc2FjdGlvbl9pZCI6ImUxMDk5MzYzLTZmZDItNDJkNS1hOGU2LTViMTRiMTZjYTU2ZiIsInN1YiI6IjNhZjc1OWFjLWQ1MjEtMjc4My1lMDYzLTYzMTk5ZjBhNDI3ZSIsImF1ZCI6WyJyZXN0c2VydmljZSJdLCJ1c2VyX25hbWUiOiJkYXQudHQyQHNoYi5jb20udm4iLCJzY29wZSI6WyJyZWFkIl0sImlzcyI6Imh0dHBzOi8vbG9jYWxob3N0IiwibmFtZSI6ImRhdC50dDJAc2hiLmNvbS52biIsInV1aWRfYWNjb3VudCI6IjNhZjc1OWFjLWQ1MjEtMjc4My1lMDYzLTYzMTk5ZjBhNDI3ZSIsImF1dGhvcml0aWVzIjpbIlVTRVIiXSwianRpIjoiZDM5YzgxOGItYTg2MC00NWY3LWE5MjMtMDdmM2Q3M2NmMjc0IiwiY2xpZW50X2lkIjoiYWRtaW5hcHAifQ.lnLbcOwOApvKLNzbGlJ0CtREzFQVTWa4Pd4vW91QF-_7-PT0EYrYRZT5T5SSYb2ewJ5B9bygLsvMONA4iNx44gzrEBIQWx68qdWcSG-ZGP4mA5KfgeY3-d46mZZth3_y_sgdDQL_EoqXUai9HsGSAWNi784_GxY9fX8UH7Bkw0ZQ6P7Vo_Fbf5MvR917SopN8Mc7FAQhu8e0iqJiJL0-aBlN-8ZjpF8CPVNDAxmK2SXaHjxHrdINKTgDGPKYPJlymWMRGoTfGOCbpkr6zzfVhseElZyx14dS1xNJ3AsX8eNo8NM0nRhPye2tkBhQ2neZaG-3uxDTu4RMXa3zOLREAQ"
    //        val tokenId = "3af92ef8-0877-4a2d-e063-62199f0a436a"
    //        val tokenKey =
    //
    // "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAM5F2cs6MfgFsxqVdT+KHnzAg86lMn6XsmaCy+AzwXZFTZ9nkzSfXB70qtAI9IC2Z/u/luGXyYqouMIYSxpWa3cCAwEAAQ=="
    //        val botId = "ce7d6c70-41f3-11f0-a86d-fb887cea1462"
    //        val senderId = "userA"
    //
    //        val intent = Intent(this@DashboardActivity, ChatbotActivity::class.java).apply {
    //            putExtra(KeyIntentConstants.ACCESS_TOKEN, accessToken)
    //            putExtra(KeyIntentConstants.TOKEN_ID, tokenId)
    //            putExtra(KeyIntentConstants.TOKEN_KEY, tokenKey)
    //            putExtra(KeyIntentConstants.BOT_ID, botId)
    //            putExtra(KeyIntentConstants.SENDER_ID, senderId)
    //            putExtra(KeyIntentConstants.METADATA, "")
    //            putExtra(KeyIntentConstants.APP_NAME, "Trợ lý ảo SHB Sale")
    //            putExtra(KeyIntentConstants.APP_LOGO, R.drawable.ic_logo_app)
    //        }
    //        startActivity(intent)
    //    }

    private fun openFourScreen(): Boolean {
        binding.viewPager.fadeAndScaleToItem(3)
        bottomFourChecked()
        animateBottomNavItem(R.id.nav_four)
        return true
    }

    private fun openThreeScreen(): Boolean {
        binding.viewPager.fadeAndScaleToItem(2)
        bottomThreeChecked()
        animateBottomNavItem(R.id.nav_three)
        return true
    }

    private fun openTwoScreen(): Boolean {
        binding.viewPager.fadeAndScaleToItem(1)
        bottomTwoChecked()
        animateBottomNavItem(R.id.nav_two)
        return true
    }

    private fun openHomeScreen(): Boolean {
        binding.viewPager.fadeAndScaleToItem(0)
        bottomHomeChecked()
        animateBottomNavItem(R.id.nav_home)
        return true
    }

    private fun bottomHomeChecked() {
        binding.bottomNavigationView.menu.findItem(R.id.nav_home).isChecked = true
    }

    private fun bottomTwoChecked() {
        binding.bottomNavigationView.menu.findItem(R.id.nav_two).isChecked = true
    }

    private fun bottomThreeChecked() {
        binding.bottomNavigationView.menu.findItem(R.id.nav_three).isChecked = true
    }

    private fun bottomFourChecked() {
        binding.bottomNavigationView.menu.findItem(R.id.nav_four).isChecked = true
    }

    override fun initObserve() {}

    private fun currentFragment() = viewPagerAdapter?.getFragment(currentPosition)

    inner class DashBoardViewPageAdapter(
        private val fragments: List<BaseFragmentBinding<out ViewBinding>>,
        private val fa: FragmentActivity
    ) : FragmentStateAdapter(fa) {

        override fun getItemCount() = fragments.size

        override fun createFragment(position: Int) = fragments[position]

        fun getFragment(position: Int) = fragments[position]
    }
}
