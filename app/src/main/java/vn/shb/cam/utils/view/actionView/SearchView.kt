package vn.shb.cam.utils.view.actionView

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.shb.cam.R

class SearchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private val edtSearch: TextInputEditText
    private val ivSearch: AppCompatImageView

    var onSearchSubmit: ((String) -> Unit)? = null
    var onTextChanged: ((String) -> Unit)? = null

    private var debounceJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        LayoutInflater.from(context).inflate(R.layout.view_search, this, true)

        edtSearch = findViewById(R.id.edt_input_search)
        ivSearch = findViewById(R.id.ivSearch)

        setupListeners()
    }

    private fun setupListeners() {
        edtSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                onSearchSubmit?.invoke(v.text.toString().trim())
                true
            } else {
                false
            }
        }

        ivSearch.setOnClickListener {
            onSearchSubmit?.invoke(edtSearch.text.toString().trim())
        }

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                debounceJob?.cancel()
                debounceJob = coroutineScope.launch {
                    delay(500) // Đợi 0.5 giây sau khi dừng gõ
                    onTextChanged?.invoke(s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun setHint(hint: String) {
        edtSearch.hint = hint
    }

    fun setText(text: String) {
        edtSearch.setText(text)
    }

    fun getText(): String {
        return edtSearch.text?.toString() ?: ""
    }

    fun setSearchButtonVisible(visible: Boolean) {
        ivSearch.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        coroutineScope.cancel()
    }
}

