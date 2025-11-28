package vn.shb.lao.screens.transaction

import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.lao.R
import vn.shb.lao.base.BaseFragmentBinding
import vn.shb.lao.base.view.MyTextView
import vn.shb.lao.databinding.FragmentTransactionHistoryBinding
import vn.shb.lao.screens.account.helper.TransactionAdapter


import vn.shb.lao.utils.extensions.common.Const
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TransactionHistoryFragment :
    BaseFragmentBinding<FragmentTransactionHistoryBinding>(FragmentTransactionHistoryBinding::inflate) {
    private val adapter by lazy { TransactionAdapter() }
//    private var isStarted = false

    private var fromDateMillis: Long = 0L
    private var toDateMillis: Long = 0L

    override fun initView(view: View) {
        setUpRecyclerView()
    }

    private fun setupViewDate() {
        binding.iclFromDate.tvValueDate.apply {
            if (fromDateMillis == 0L) {
                text = getString(R.string.from)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral6))
            } else {
                text = getViewDate(fromDateMillis)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral8))
            }
        }

        binding.iclToDate.tvValueDate.apply {
            if (toDateMillis == 0L) {
                text = getString(R.string.toDate)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral6))
            } else {
                text = getViewDate(toDateMillis)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral8))
            }
        }
    }

    private fun getViewDate(value: Long): String {
        val dateFormat = SimpleDateFormat(Const.FORMAT_TRANSACTION_DATE, Locale.getDefault())
        return dateFormat.format(Date(value))
    }

    override fun onResume() {
        super.onResume()
        if ((fromDateMillis == 0L || toDateMillis == 0L)){
            val initDate = homeViewModel.getInitDate()
            fromDateMillis = initDate.first
            toDateMillis = initDate.second
        }
        setupViewDate()
        homeViewModel.getAllTransactions(
            requireContext(),
            Pair(
                getViewDate(fromDateMillis),
                getViewDate(toDateMillis)
            )
        )
//        isStarted = true
    }

    private fun setUpRecyclerView() {
        adapter.setOnClickDetailListener {
            homeViewModel.currentTransaction = it
            safeNavigate(R.id.transactionHistory, R.id.transactionDetailFragment)
        }
        with(binding.rcvTransaction) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@TransactionHistoryFragment.adapter
        }
    }

    override fun initListener() {
        with(binding) {
            tvTransactionHistory.setOnSingleClickListener {
                safeNavigate(R.id.transactionHistory, R.id.backToAccountDetails)
            }

            iclFromDate.root.setOnSingleClickListener {
                showDatePicker(requireContext(), fromDateMillis) { selected ->
                    if (selected > toDateMillis) {
                        showErrorChooseDate(tvStatusUpdateDate)
                        return@showDatePicker
                    }
                    fromDateMillis = selected
                    setupViewDate()
                    homeViewModel.getAllTransactions(
                        requireContext(),
                        pairDate = Pair(getViewDate(fromDateMillis), getViewDate(toDateMillis))
                    )
                }
            }

            iclToDate.root.setOnSingleClickListener {
                showDatePicker(requireContext(), toDateMillis) { selected ->
                    if (selected < fromDateMillis) {
                        showErrorChooseDate(tvStatusUpdateDate)
                        return@showDatePicker
                    }
                    toDateMillis = selected
                    setupViewDate()
                    homeViewModel.getAllTransactions(
                        requireContext(),
                        pairDate = Pair(getViewDate(fromDateMillis), getViewDate(toDateMillis))
                    )
                }
            }
        }
    }

    override fun initObserve() {
        with(homeViewModel) {
            launchRepeatOnLifecycle {
                launch {
                    stateAllTransactions.collectLatest {
                        adapter.submitList(it)
                        binding.apply {
                            rcvTransaction.isVisible = it.isNotEmpty()
                            tvNoTransaction.isVisible = it.isEmpty()
                        }
                    }
                }

            }
        }
    }

    fun showDatePicker(context: Context, selectedMillis: Long?, onDateSelected: (Long) -> Unit) {
        val calendar = Calendar.getInstance()
        val today = Calendar.getInstance()

        selectedMillis?.let { calendar.timeInMillis = it }

        val minDate = Calendar.getInstance().apply {
            add(Calendar.MONTH, -3)
        }

        val datePicker = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 0, 0, 0)
                }
                onDateSelected(cal.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.datePicker.minDate = minDate.timeInMillis
        datePicker.datePicker.maxDate = today.timeInMillis
        datePicker.show()
    }

    fun showErrorChooseDate(
        textView: MyTextView
    ) {
        textView.visibility = View.VISIBLE

        textView.animate()
            .alpha(1f)
            .setDuration(200)
            .withEndAction {
                textView.postDelayed({
                    textView.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .withEndAction {
                            textView.visibility = View.GONE
                            textView.alpha = 1f
                        }
                        .start()
                }, 2000)
            }
            .start()
    }

}
