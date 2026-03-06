package vn.shb.cam.screens.beneficiary

import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.shb.cam.R
import vn.shb.cam.base.BaseFragmentBinding
import vn.shb.cam.databinding.FragmentBeneficiaryBinding
import vn.shb.cam.databinding.LayoutProcessBarBinding
import vn.shb.cam.screens.beneficiary.helper.ActionEditBeneficiary
import vn.shb.cam.screens.beneficiary.helper.BeneficiaryAdapter
import vn.shb.cam.utils.ApiConst
import vn.shb.cam.utils.extensions.gone
import vn.shb.cam.utils.extensions.launchRepeatOnLifecycle
import vn.shb.cam.utils.extensions.setLatinAlphanumericFilter
import vn.shb.cam.utils.extensions.visible
import vn.shb.cam.utils.view.dialog.BottomSheetDialogHelper
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.beneficiary.Beneficiary

class BeneficiaryFragment : BaseFragmentBinding<FragmentBeneficiaryBinding>(
    FragmentBeneficiaryBinding::inflate
) {

    private val adapter by lazy { BeneficiaryAdapter() }
    private val viewModel: BeneficiaryViewModel by viewModel()
    private var dialog: LayoutProcessBarBinding? = null

    override fun initView(view: View) {
        setUpRecyclerview()
        viewModel.getAllBeneficiary()
        viewModel.getBanks()
        dialog = LayoutProcessBarBinding.inflate(layoutInflater)
    }

    private fun setUpRecyclerview() {
        binding.rcvBeneficiary.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = this@BeneficiaryFragment.adapter
        }

        adapter.setListenAction(
            object : ActionEditBeneficiary {
                override fun edit(item: Beneficiary) {
                    safeNavigate(
                        R.id.beneficiaryFragment, R.id.editBeneficiaryFragment, bundleOf(
                            ApiConst.KEY_TO_EDIT_BENEFICIARY to EditBeneficiaryFragment.EDIT,
                            ApiConst.KEY_BENEFICIARY_DATA to item
                        )
                    )
                }

                override fun delete(item: Beneficiary) {
                    BottomSheetDialogHelper(requireContext()).message(
                        title = getString(R.string.confirmation),
                        message = getString(
                            R.string.doYouWantToDeleteFromBeneficiary,
                            item.accountName ?: ""
                        ),
                        textNegative = getString(R.string.noLabel),
                        textPositive = getString(R.string.yesLabel),
                        positiveAction = {
                            viewModel.deleteBeneficiary(item)
                        }
                    )
                }
            }
        )
    }


    override fun initListener() {
        with(binding) {
            tvBeneficiaryList.setOnSingleClickListener {
                backPress()
            }

            tvAddNew.setOnSingleClickListener {
                navToEditBeneficiary()
            }

            edtSearchBeneficiary.setLatinAlphanumericFilter(50)

            edtSearchBeneficiary.addTextChangedListener { text ->
                adapter.filter(text.toString())
            }

            ivClose.setOnSingleClickListener {
                llToastStatus.animate().cancel()
                llToastStatus.visibility = View.GONE
            }

            setFragmentResultListener(ApiConst.KEY_RESULT_BENEFICIARY) { requestKey, bundle ->
                val message = bundle.getString(ApiConst.KEY_MESSAGE)
                val isError = bundle.getBoolean(ApiConst.KEY_CONFIRM_ERROR)
                if (!message.isNullOrEmpty()) {
                    showToastSuccess(message, !isError)
                }
            }
        }
    }

    override fun initObserve() {
        with(viewModel) {
            launchRepeatOnLifecycle {
                launch {
                    stateAllBeneficiary.collectLatest {
                        adapter.submitListData(it)
                        lifecycleScope.launch {
                            delay(200)
                            binding.rcvBeneficiary.smoothScrollToPosition(0)
                        }
                    }
                }
                launch {
                    stateBanks.collectLatest {
                        // Handle banks list
                    }
                }
                launch {
                    stateDelete.collect {
                        if (it == true) {
                            showToastSuccess(getString(R.string.beneficiaryDeletedSuccessfully))
                        }
                    }
                }

                launch {
                    stateLoading.collect {
                        if (it) {
                            dialog?.root?.visible()
                        } else {
                            dialog?.root?.gone()
                        }
                    }
                }
            }
        }
    }

    private fun navToEditBeneficiary() {
        safeNavigate(
            R.id.beneficiaryFragment,
            R.id.editBeneficiaryFragment,
            bundleOf(ApiConst.KEY_TO_EDIT_BENEFICIARY to EditBeneficiaryFragment.ADD_NEW)
        )
    }

    fun showToastSuccess(text: String, isSuccess: Boolean = true) {
        with(binding) {
            launchRepeatOnLifecycle {
                tvToastMessage.text = text
                llToastStatus.setBackgroundResource(if (isSuccess) R.drawable.bg_toast_change_avatar_ss else R.drawable.bg_toast_change_avatar_error)
                tvToastMessage.setCompoundDrawablesWithIntrinsicBounds(
                    if (isSuccess) R.drawable.ic_success else R.drawable.ic_error,
                    0,
                    0,
                    0
                )
                llToastStatus.visible()
                llToastStatus.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .withEndAction {
                        llToastStatus.postDelayed({
                            llToastStatus.animate()
                                .alpha(0f)
                                .setDuration(300)
                                .withEndAction {
                                    llToastStatus.visibility = View.GONE
                                    llToastStatus.alpha = 1f
                                }
                                .start()
                        }, 3000)
                    }
                    .start()
            }
        }
    }

}