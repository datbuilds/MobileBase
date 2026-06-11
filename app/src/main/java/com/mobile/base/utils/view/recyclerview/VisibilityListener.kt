package com.mobile.base.utils.view.recyclerview

interface VisibilityListener {
    fun onVisibleChanged(positionShowing: Int, positionHiding: Int, visibilityPercentage: Float)
}