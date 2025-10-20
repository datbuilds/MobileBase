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
import vn.shb.lao.databinding.FragmentTransactionHistoryBinding
import vn.shb.lao.screens.account.helper.TransactionAdapter
import vn.shb.lao.screens.home.HomeViewModel

import vn.shb.lao.utils.extensions.common.Const
import vn.shb.lao.utils.extensions.launchRepeatOnLifecycle
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TransactionHistoryFragment :
    BaseFragmentBinding<FragmentTransactionHistoryBinding>(FragmentTransactionHistoryBinding::inflate) {
    private val adapter by lazy { TransactionAdapter() }

    private val homeViewModel: HomeViewModel by sharedViewModel()

    private var fromDate: String = ""
    private var toDate: String = ""

    override fun initView(view: View) {
        setUpRecyclerView()
    }

    private fun setupViewDate() {
        binding.iclFromDate.tvValueDate.apply {
            if (fromDate.isEmpty()) {
                text = getString(R.string.from)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral6))
            } else {
                text = fromDate
                setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral8))
            }
        }

        binding.iclToDate.apply {
            tvValueDate.apply {
                if (toDate.isEmpty()) {
                    text = getString(R.string.toDate)
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral6))
                } else {
                    text = toDate
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.neutral8))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val initDate = homeViewModel.getInitDate()
        fromDate = initDate.first
        toDate = initDate.second
        setupViewDate()
        homeViewModel.getAllTransactions(requireContext())
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
                showDatePicker(requireContext(), fromDate) {
                    fromDate = it
                    setupViewDate()
                    homeViewModel.getAllTransactions(requireContext(), pairDate = Pair(fromDate, toDate))
                }
            }

            iclToDate.root.setOnSingleClickListener {
                showDatePicker(requireContext(), toDate) {
                    toDate = it
                    setupViewDate()
                    homeViewModel.getAllTransactions(requireContext(), pairDate = Pair(fromDate, toDate))
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
                launch {
                    stateError.collect { error ->
                       handleErrorHome(error)
                    }
                }
            }
        }
    }

    fun showDatePicker(context: Context, selectedDate: String?, onDateSelected: (String) -> Unit) {
        val dateFormat = SimpleDateFormat(Const.FORMAT_TRANSACTION_DATE, Locale.getDefault())

        val calendar = Calendar.getInstance()
        val today = Calendar.getInstance()

        // Nếu có ngày đã chọn thì set lại làm mặc định
        selectedDate?.let {
            try {
                val parsed = dateFormat.parse(it)
                calendar.time = parsed!!
            } catch (_: Exception) {}
        }

        val minDate = Calendar.getInstance().apply {
            add(Calendar.MONTH, -3)
        }

        val datePicker = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // 🔒 Giới hạn chỉ chọn trong 3 tháng gần nhất
        datePicker.datePicker.minDate = minDate.timeInMillis
        datePicker.datePicker.maxDate = today.timeInMillis
        datePicker.show()
    }


//    val today = Calendar.getInstance()
//    val threeMonthsAgo = Calendar.getInstance().apply { add(Calendar.MONTH, -3) }
//
//    val datePickerDialog = DatePickerDialog(
//        context,
//        { _, year, month, dayOfMonth ->
//            val selectedDate = Calendar.getInstance().apply {
//                set(year, month, dayOfMonth)
//            }
//
//            if (selectedDate.before(threeMonthsAgo) || selectedDate.after(today)) {
//                Toast.makeText(context, "Vui lòng chọn trong 3 tháng gần nhất", Toast.LENGTH_SHORT).show()
//            } else {
//                val formatted = "%02d/%02d/%d".format(dayOfMonth, month + 1, year)
//                println("Ngày hợp lệ: $formatted")
//            }
//        },
//        today.get(Calendar.YEAR),
//        today.get(Calendar.MONTH),
//        today.get(Calendar.DAY_OF_MONTH)
//    )
//
//    datePickerDialog.show()


}
