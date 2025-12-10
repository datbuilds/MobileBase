package vn.shb.dn.choosePhotoHelper.callback

/**
 * @author aminography
 */
fun interface ChoosePhotoCallback<T> {
    fun onChoose(photo: T?)

    fun onError() {}
}