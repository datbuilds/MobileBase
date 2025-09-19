package vn.shb.lao.utils.view.recyclerview

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.min

/**
 * source github: https://github.com/DingMouRen
 */
class PickerLayoutManager : LinearLayoutManager {
    private var mScale = 0.5f
    private var mIsAlpha = true
    private var mLinearSnapHelper: LinearSnapHelper?
    private var mOnSelectedViewListener: OnSelectedViewListener? = null
    private var mItemViewWidth = 0
    private var mItemViewHeight = 0
    private var mItemCount = -1
    private var mRecyclerView: RecyclerView? = null
    private var mOrientation: Int

    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    ) {
        mLinearSnapHelper = LinearSnapHelper()
        this.mOrientation = orientation
    }

    constructor(
        context: Context?,
        recyclerView: RecyclerView?,
        orientation: Int,
        reverseLayout: Boolean,
        itemCount: Int,
        scale: Float,
        isAlpha: Boolean
    ) : super(context, orientation, reverseLayout) {
        mLinearSnapHelper = LinearSnapHelper()
        mItemCount = itemCount
        this.mOrientation = orientation
        this.mRecyclerView = recyclerView
        mIsAlpha = isAlpha
        mScale = scale

    }

    override fun isAutoMeasureEnabled(): Boolean {
        return if (mItemCount != 0)
            false
        else
            super.isAutoMeasureEnabled()
    }

    /**
     * LinearSnapHelper
     * @param view
     */
    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        mLinearSnapHelper!!.attachToRecyclerView(view)
    }

    /**
     * @param recycler
     * @param state
     * @param widthSpec
     * @param heightSpec
     */
    override fun onMeasure(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        widthSpec: Int,
        heightSpec: Int
    ) {
//        if (itemCount > 0 && mItemCount > 0 && mRecyclerView!= null/* && mRecyclerView?.childCount ?: 0 > 0*/) {
//            val view = recycler.getViewForPosition(0)
//            measureChildWithMargins(view, widthSpec, heightSpec)
//            mItemViewWidth = view.measuredWidth
//            mItemViewHeight = view.measuredHeight
//
//            logE("""
//                mItemViewWidth: $mItemViewWidth
//                mItemViewHeight: $mItemViewHeight
//                recyclerViewHeight: ${mRecyclerView!!.height}
//            """.trimIndent())
//            if (mOrientation == HORIZONTAL) {
//                val paddingHorizontal = (mItemCount - 1) / 2 * mItemViewWidth
//                mRecyclerView!!.clipToPadding = false
//                mRecyclerView!!.setPadding(paddingHorizontal, 0, paddingHorizontal, 0)
//                setMeasuredDimension(mItemViewWidth * mItemCount, mItemViewHeight)
//            } else if (mOrientation == VERTICAL) {
//                val paddingVertical = (mItemCount - 1) / 2 * mItemViewHeight
////                val paddingVertical = (mRecyclerView!!.height - mItemViewHeight).div(2)
//                logE("paddingVertical: $paddingVertical")
//                mRecyclerView!!.clipToPadding = false
//                mRecyclerView!!.setPadding(0, paddingVertical, 0, paddingVertical)
//                setMeasuredDimension(mItemViewWidth, mItemViewHeight * mItemCount)
//            }
//        } else {
        super.onMeasure(recycler, state, widthSpec, heightSpec)
//        }
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        if (itemCount < 0 || state.isPreLayout) return
        if (mOrientation == HORIZONTAL) {
            scaleHorizontalChildView()
        } else if (mOrientation == VERTICAL) {
            scaleVerticalChildView()
        }
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        scaleHorizontalChildView()
        return super.scrollHorizontallyBy(dx, recycler, state)
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        scaleVerticalChildView()
        return super.scrollVerticallyBy(dy, recycler, state)
    }

    /**
     *
     */
    private fun scaleHorizontalChildView() {
        val mid = width / 2.0f
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childMid = (getDecoratedLeft(child!!) + getDecoratedRight(child)) / 2.0f
            val scale = 1.0f + -1 * (1 - mScale) * min(mid, abs(mid - childMid)) / mid
            child.scaleX = scale
            child.scaleY = scale
            if (mIsAlpha) {
                child.alpha = scale
            }
        }
    }

    /**
     *
     */
    private fun scaleVerticalChildView() {
        val mid = height / 2.0f
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childMid = (getDecoratedTop(child!!) + getDecoratedBottom(child)) / 2.0f
            val scale = 1.0f + -1 * (1 - mScale) * min(mid, abs(mid - childMid)) / mid
            child.scaleX = scale
            child.scaleY = scale
            if (mIsAlpha) {
                child.alpha = scale
            }
        }
    }

    /**
     *
     * @param state
     */
    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == 0) {
            if (mOnSelectedViewListener != null && mLinearSnapHelper != null) {
                val view = mLinearSnapHelper!!.findSnapView(this)
                val position = getPosition(view!!)
                mOnSelectedViewListener!!.onSelectedView(view, position)
            }
        }
    }

    fun setOnSelectedViewListener(listener: OnSelectedViewListener?) {
        mOnSelectedViewListener = listener
    }

    /**
     *
     */
    interface OnSelectedViewListener {
        fun onSelectedView(view: View?, position: Int)
    }
}