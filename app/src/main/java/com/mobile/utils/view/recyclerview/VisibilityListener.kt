package com.mobile.utils.view.recyclerview

interface VisibilityListener {
    fun onVisibleChanged(positionShowing: Int, positionHiding: Int, visibilityPercentage: Float)
}