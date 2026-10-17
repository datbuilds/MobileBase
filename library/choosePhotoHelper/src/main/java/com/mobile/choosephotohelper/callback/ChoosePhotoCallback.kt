package com.mobile.choosephotohelper.callback

/**
 * @author aminography
 */
fun interface ChoosePhotoCallback<T> {
    fun onChoose(photo: T?)

    fun onError() {}
}