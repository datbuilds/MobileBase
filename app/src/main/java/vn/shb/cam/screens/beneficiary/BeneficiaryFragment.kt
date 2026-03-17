package vn.shb.cam.screens.beneficiary

import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.data.entities.beneficiary.Beneficiary
import vn.shb.cam.R
import vn.shb.cam.base.BaseFragmentBinding
import vn.shb.cam.databinding.FragmentBeneficiaryBinding
import vn.shb.cam.screens.beneficiary.helper.ActionEditBeneficiary
import vn.shb.cam.screens.beneficiary.helper.BeneficiaryAdapter
import vn.shb.cam.utils.ApiConst
import vn.shb.cam.utils.extensions.launchRepeatOnLifecycle
import vn.shb.cam.utils.extensions.visible
import vn.shb.cam.utils.view.dialog.BottomSheetDialogHelper
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import vn.shb.cam.databinding.LayoutProcessBarBinding
import vn.shb.cam.utils.extensions.CustomToastShowOnTop
import vn.shb.cam.utils.extensions.gone
import vn.shb.cam.utils.extensions.setLatinAlphanumericFilter

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
                            ApiConst.KEY_BENEFICIARY_DATA to item,
                            ApiConst.KEY_LIST_BENEFICIARY_DATA to adapter.getDefaultList(),
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

    private var searchJob: Job? = null

    override fun initListener() {
        with(binding) {
            btnBack.setOnSingleClickListener {
                backPress()
            }

            tvAddNew.setOnSingleClickListener {
                navToEditBeneficiary()
            }

            edtSearchBeneficiary.setLatinAlphanumericFilter(50)

            edtSearchBeneficiary.addTextChangedListener { text ->
                searchJob?.cancel() // Cancel previous pending search
                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(300L) // Wait for user to stop typing
                    adapter.filter(text.toString())
                }
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
            bundleOf(
                ApiConst.KEY_TO_EDIT_BENEFICIARY to EditBeneficiaryFragment.ADD_NEW,
                ApiConst.KEY_LIST_BENEFICIARY_DATA to adapter.getDefaultList()
            )
        )
    }

    fun showToastSuccess(text: String, isSuccess: Boolean = true) {

        context?.let { context->
            val background=if (isSuccess) R.drawable.bg_toast_change_avatar_ss else R.drawable.bg_toast_change_avatar_error
            val icon=if (isSuccess) R.drawable.ic_success else R.drawable.ic_error

            val toast = CustomToastShowOnTop(
                context,
                binding.root,
                icon = icon,
                message = text,
                background = background,
                duration = 3000,
                textColor = R.color.neutral1,
                iconClose = R.color.neutral1
            )
            toast.show()
        }

    }

}