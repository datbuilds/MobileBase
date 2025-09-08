package vn.shb.lao.utils.view.recyclerview

interface VisibilityListener {
    fun onVisibleChanged(positionShowing: Int, positionHiding: Int, visibilityPercentage: Float)
}