package vn.shb.cam.utils.extensions

import com.google.android.material.button.MaterialButton
import vn.shb.core.utils.extesions.setOnSingleClickListener

fun MaterialButton.hideIcon() {
    this.icon = null // Đặt icon thành null để ẩn nó
    this.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_END // Đặt vị trí của icon
}

fun MaterialButton.setOnMaterialButtonClick(onSubmit: () -> Unit) {
    this.setOnSingleClickListener {
        this.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                this.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .withEndAction {
                        // Handle upgrade logic here
                        onSubmit()
                    }
                    .start()
            }
            .start()
    }
}