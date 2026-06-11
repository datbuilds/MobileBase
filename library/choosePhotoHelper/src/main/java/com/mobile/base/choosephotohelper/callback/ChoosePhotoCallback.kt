package com.mobile.base.choosephotohelper.callback

/**
 * @author aminography
 */
fun interface ChoosePhotoCallback<T> {
    fun onChoose(photo: T?)

    fun onError() {}
}