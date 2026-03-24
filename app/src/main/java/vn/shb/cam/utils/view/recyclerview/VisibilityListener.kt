package vn.shb.cam.utils.view.recyclerview

interface VisibilityListener {
    fun onVisibleChanged(positionShowing: Int, positionHiding: Int, visibilityPercentage: Float)
}