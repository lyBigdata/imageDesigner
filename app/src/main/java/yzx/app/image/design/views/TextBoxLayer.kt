package yzx.app.image.design.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.renderscript.Int2
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.customview.widget.ViewDragHelper
import yzx.app.image.design.utils.dp2px

class TextBoxLayer : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    /** 添加文字 */
    fun add(text: String = "", color: Int = Color.WHITE): TextView {
        return AddTextTextView(context).apply {
            this.text = text
            setTextColor(color)
            background = ColorDrawable(Color.BLACK)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, dp2px(20).toFloat())
            val pd = dp2px(5)
            setPadding(pd, pd, pd, pd)
            addView(this, LayoutParams(-2, -2).apply { gravity = Gravity.CENTER })
            setOnClickListener { onTextViewClick?.invoke(this) }
            post { dragXYInfo[this] = Int2().apply { x = left; y = top } }
        }
    }


    /** 内部TextView点击回调 */
    var onTextViewClick: ((TextView) -> Unit)? = null


    /** 移除一个TextView */
    fun delete(tv: TextView) {
        dragXYInfo.remove(tv)
        removeView(tv)
    }


    //--  --\\
    //--    --\\
    //--      --\\
    //--        --\\
    //--          --\\


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        dragXYInfo.entries.forEach {
            val childView = it.key
            var originX = it.value.x
            var originY = it.value.y
            if (originX + childView.measuredWidth <= 0) {
                it.value.x = 0
                originX = 0
            }
            if (originY + childView.measuredHeight <= 0) {
                it.value.y = 0
                originY = 0
            }
            it.key.layout(originX, originY, originX + childView.measuredWidth, originY + childView.measuredHeight)
        }
    }

    private val dragXYInfo = HashMap<View, Int2>()


    private val dragHelper: ViewDragHelper = ViewDragHelper.create(this, 1f, object : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean = true
        override fun getViewHorizontalDragRange(child: View): Int = Integer.MAX_VALUE
        override fun getViewVerticalDragRange(child: View): Int = Integer.MAX_VALUE
        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
//            if (left < 0) return 0
//            if (left > measuredWidth - child.measuredWidth) return measuredWidth - child.measuredWidth
            return left
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
//            if (top < 0) return 0
//            if (top > measuredHeight - child.measuredHeight) return measuredHeight - child.measuredHeight
            return top
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            dragXYInfo[releasedChild] = Int2(releasedChild.left, releasedChild.top)
        }
    })


    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) invalidate()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return dragHelper.shouldInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }

}