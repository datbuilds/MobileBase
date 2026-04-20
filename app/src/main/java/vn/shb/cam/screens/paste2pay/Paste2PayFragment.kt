package vn.shb.cam.screens.paste2pay

import android.annotation.SuppressLint
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import org.koin.androidx.viewmodel.ext.android.viewModel
import vn.shb.cam.R
import vn.shb.cam.base.BaseFragmentBinding
import vn.shb.cam.databinding.FragmentPastePayBinding
import vn.shb.cam.navigation.AppDestination
import vn.shb.cam.screens.paste2pay.dialog.AiIntroductionDialog
import vn.shb.cam.screens.paste2pay.dialog.BSAiResult
import vn.shb.cam.screens.paste2pay.state.ChatPayUiState
import vn.shb.cam.screens.paste2pay.widget.SafePasteEditText
import vn.shb.cam.screens.transfer.MoneyTransferFragment
import vn.shb.cam.utils.ApiConst
import vn.shb.cam.utils.BankType
import vn.shb.cam.utils.extensions.collapse
import vn.shb.cam.utils.extensions.collectState
import vn.shb.cam.utils.extensions.hideProgressDialog
import vn.shb.cam.utils.extensions.hideSoftKeyboard
import vn.shb.cam.utils.extensions.setOnMaterialButtonClick
import vn.shb.cam.utils.extensions.showProgressDialog
import vn.shb.cam.utils.extensions.toast
import vn.shb.cam.utils.extensions.visible
import vn.shb.cam.utils.view.actionView.OnToolbarListener
import vn.shb.core.core.domain.source.response.AiPayResult
import vn.shb.core.utils.extesions.setOnSingleClickListener
import vn.shb.dn.choosePhotoHelper.ChoosePhotoHelper
import vn.shb.dn.choosePhotoHelper.callback.ChoosePhotoCallback
import java.io.File
import java.util.Locale

class Paste2PayFragment :
    BaseFragmentBinding<FragmentPastePayBinding>(FragmentPastePayBinding::inflate) {

    private val chatPayViewModel: ChatPayViewModel by viewModel()
    private lateinit var photoHelper: ChoosePhotoHelper
    private var selectedImagePath: String? = null

    companion object {
        private const val DEFAULT_REQUEST_USER = "admin"
        private const val MAX_UPLOAD_IMAGE_SIZE_MB = 10
        private const val MAX_UPLOAD_IMAGE_SIZE_BYTES = MAX_UPLOAD_IMAGE_SIZE_MB * 1024L * 1024L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoHelper = ChoosePhotoHelper.with(this)
            .asFilePath()
            .alwaysShowRemoveOption(true)
            .build(object : ChoosePhotoCallback<String> {
                override fun onChoose(photo: String?) {
                    if (photo.isNullOrBlank()) return

                    val selectedFile = File(photo)
                    if (!selectedFile.exists() || !selectedFile.isFile) return

                    if (selectedFile.length() >= MAX_UPLOAD_IMAGE_SIZE_BYTES) {
                        showErrorMessageOnly(message = getString(R.string.paste_pay_image_too_large)) {
                            clearSelectedImage()
                        }
                        return
                    }

                    selectedImagePath = photo
                    binding.imagePreview.setPreview(photo)
                    binding.imagePreview.visible()
                    updateConfirmButtonState()
                }

                override fun onError() {
                }
            })
    }

    override fun initView(view: View) {
        binding.toolBar.setTitle(getString(R.string.chatpay))
        binding.imagePreview.setOnDeleteClickListener {
            clearSelectedImage()
        }
        binding.edtContent.imeOptions = EditorInfo.IME_ACTION_DONE
        updateConfirmButtonState()
        if (!storage.isAiChatIntroCompleted()) {
            showAiIntroDialog()
        }
    }

    private fun showAiIntroDialog() {
        val dialog = AiIntroductionDialog.Builder().build()
        dialog.isCancelable = false
        dialog.show(childFragmentManager, AiIntroductionDialog.TAG)
    }

    private fun clearSelectedImage() {
        selectedImagePath = null
        binding.imagePreview.clear()
        binding.imagePreview.collapse()
        updateConfirmButtonState()
    }

    private fun resetInputState() {
        selectedImagePath = null
        binding.edtContent.text?.clear()
        binding.edtContent.clearFocus()
        binding.imagePreview.clear()
        binding.imagePreview.collapse()
        updateConfirmButtonState()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {
        with(binding) {
            btUploadImage.setOnMaterialButtonClick {
                photoHelper.chooseFromGallery()
            }

            btConfirm.setOnMaterialButtonClick {
                chatPayViewModel.submitAiPay(
                    textContent = edtContent.text?.toString().orEmpty(),
                    imagePath = selectedImagePath,
                    requestUser = getRequestUser()
                )
            }

            toolBar.setListener(object : OnToolbarListener {
                override fun onPreviousClick() {
                    backPress()
                }
            })

            ivPaste.setOnSingleClickListener {
                showClipboardChooser()
            }

            edtContent.doAfterTextChanged {
                updateConfirmButtonState()
            }

            rootView.setOnTouchListener { _, _ ->
                hideSoftKeyboard()
                false
            }
        }
    }

    override fun initObserve() {
        collectState(chatPayViewModel.state) { state ->
            when (state) {
                ChatPayUiState.Idle -> hideProgressDialog()
                ChatPayUiState.Loading -> showProgressDialog()
                is ChatPayUiState.Success -> {
                    hideProgressDialog()
                    chatPayViewModel.resetState()
                    showAiResultDialog(state.result)
                }

                is ChatPayUiState.Error -> {
                    hideProgressDialog()
                    chatPayViewModel.resetState()
                    handleErrorHome(state.reason){
                        resetInputState()
                    }
                }
            }
        }
    }

    private fun showClipboardChooser() {
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        if (!clipboard.hasPrimaryClip()) {
            toast("Clipboard đang trống")
            return
        }

        val clipData = clipboard.primaryClip

        if (clipData == null || clipData.itemCount == 0) {
            toast("Clipboard đang trống")
            return
        }

        val description = clipboard.primaryClipDescription
        val clipboardItems = buildList {
            for (index in 0 until clipData.itemCount) {
                val text = SafePasteEditText.normalizeClipboardText(
                    clipData.getItemAt(index).coerceToText(requireContext())
                )

                if (text.isNotEmpty() && SafePasteEditText.isValidClipboardText(text)) {
                    add(text)
                }
            }
        }.distinctBy { it }

        if (clipboardItems.isEmpty() &&
            (description?.hasMimeType("image/*") == true ||
                    description?.hasMimeType(ClipDescription.MIMETYPE_TEXT_URILIST) == true)
        ) {
            toast("Vui lòng chọn upload ảnh")
            return
        }

        if (clipboardItems.isEmpty()) {
            toast("Nội dung không hợp lệ")
            return
        }

        binding.edtContent.setText(clipboardItems.first())
        binding.edtContent.requestFocus()
        binding.edtContent.setSelection(binding.edtContent.text?.length ?: 0)
    }

    private fun updateConfirmButtonState() {
        val hasText = binding.edtContent.text?.toString()?.trim().isNullOrEmpty().not()
        val hasImage = !selectedImagePath.isNullOrBlank()
        val isConfirmEnabled = hasText || hasImage
        val isUploadEnabled = !hasText && !hasImage
        val isTextInputEnabled = !hasImage

        binding.inputContentLayout.isEnabled = isTextInputEnabled

        with(binding.btConfirm) {
            isEnabled = isConfirmEnabled
            alpha = if (isConfirmEnabled) 1f else 0.5f

        }

        with(binding.btUploadImage) {
            isEnabled = isUploadEnabled
            alpha = if (isUploadEnabled) 1f else 0.5f
        }

        with(binding.edtContent) {
            isEnabled = isTextInputEnabled
            isFocusable = isTextInputEnabled
            isFocusableInTouchMode = isTextInputEnabled
            alpha = if (isTextInputEnabled) 1f else 0.6f

            if (!isTextInputEnabled) {
                clearFocus()
            }
        }

        with(binding.ivPaste) {
            isEnabled = isTextInputEnabled
            alpha = if (isTextInputEnabled) 1f else 0.5f
        }
    }

    private fun showAiResultDialog(result: AiPayResult) {
        BSAiResult.Builder()
            .setResult(result)
            .setOnClose {
                resetInputState()
            }
            .setOnContinue {
                if (!isSupportedShbTransfer(result)) {
                    showErrorMessageOnly(message = getString(R.string.paste_pay_only_shb_supported)) {
                        resetInputState()
                    }
                    return@setOnContinue
                }
                selectedImagePath = ""
                updateConfirmButtonState()
                navigateToTransfer(result)
            }
            .build()
            .show(childFragmentManager, BSAiResult.TAG)
    }

    private fun isSupportedShbTransfer(result: AiPayResult): Boolean {
        val bankCode = result.shortName
            ?.takeIf { it.isNotBlank() }

        return bankCode?.trim()?.uppercase(Locale.getDefault()) == BankType.SHB.code
    }

    private fun navigateToTransfer(result: AiPayResult) {
        safeNavigate(
            AppDestination.MoneyTransfer(
                bundleOf(
                    ApiConst.KEY_TYPE_TRANSFER_INTRABANK to isIntrabankResponse(result.responseType),
                    ApiConst.KEY_TYPE_TRANSFER_DATA to result
                )
            )
        )
    }

    private fun isIntrabankResponse(responseType: String?): Boolean {
        val normalizedType = responseType
            ?.trim()
            ?.uppercase(Locale.getDefault())
            .orEmpty()

        return when {
            normalizedType == ApiConst.SELF -> false
            normalizedType == MoneyTransferFragment.OWN_ACCOUNT -> false
            normalizedType.contains("SELF") -> false
            normalizedType.contains("OWN") -> false
            normalizedType == ApiConst.INTRA -> true
            normalizedType == MoneyTransferFragment.INTRABANK -> true
            normalizedType.contains("INTRA") -> true
            normalizedType.contains("SHB") -> true
            else -> true
        }
    }

    private fun getRequestUser(): String {
        return getCurrentUser()?.username?.takeIf { it.isNotBlank() }
            ?: getCurrentUser()?.customerId?.takeIf { it.isNotBlank() }
            ?: DEFAULT_REQUEST_USER
    }
}
