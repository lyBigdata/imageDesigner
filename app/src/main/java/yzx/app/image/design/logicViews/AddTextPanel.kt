package yzx.app.image.design.logicViews

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import kotlinx.android.synthetic.main.layout_add_text_panel.view.*
import yzx.app.image.design.R
import yzx.app.image.design.utils.dp2px
import yzx.app.image.design.utils.inflateView


class AddTextPanel : FrameLayout {


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    init {
        inflateView(context, R.layout.layout_add_text_panel, this, true)
    }


    /** 修改文案按钮点击 */
    var onChangeTextButtonClick: (() -> Unit)? = null

    /** 获取关闭按钮 */
    var onCloseButtonClick: (() -> Unit)? = null

    /** 删除按钮点击 */
    var onDeleteButtonClick: (() -> Unit)? = null

    /** 设置对应的TextView */
    fun setTextView(tv: TextView) {
        restoreTextViewUIInfo(tv)
        setUIEvent(tv)
    }

    /** 设置新的文字内容 */
    fun setNewText(str: String) = { tv_text_content.text = str }.invoke()


    /* 设置UI事件 */
    @SuppressLint("SetTextI18n")
    private fun setUIEvent(tv: TextView) {

        repeat(container.childCount) { index -> container.getChildAt(index).apply { tag = alpha } }

        changeText.setOnClickListener { onChangeTextButtonClick?.invoke() }
        close.setOnClickListener { onCloseButtonClick?.invoke() }
        delete.setOnClickListener { onDeleteButtonClick?.invoke() }

        colorPicker.onStartTrackingTouch = { transparentAllExcept(colorPicker, tv_color) }
        colorPicker.onStopTrackingTouch = { transparentBack() }
        colorPicker.onColorChanged = { newColor -> tv.setTextColor(newColor) }


        val sizeSeekBarListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) = transparentAllExcept(tv_size, sizeSeekBar)
            override fun onStopTrackingTouch(seekBar: SeekBar) = transparentBack()
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                tv_size.text = "文字大小: (${progress})"
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, progress.toFloat())
            }
        }
        sizeSeekBar.setOnSeekBarChangeListener(sizeSeekBarListener)

        val rotationSeekBarListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) = transparentAllExcept(tv_rotation, rotationSeekBar)
            override fun onStopTrackingTouch(seekBar: SeekBar) = transparentBack()
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                tv.rotation = progress.toFloat()
                tv_rotation.text = "文字旋转角度: (${tv.rotation.toInt()}°)"
            }
        }
        rotationSeekBar.setOnSeekBarChangeListener(rotationSeekBarListener)

        val colorDrawable = ColorDrawable()
        bgColorPicker.onStartTrackingTouch = { transparentAllExcept(bgColorPicker, tv_background_color) }
        bgColorPicker.onStopTrackingTouch = { transparentBack() }
        bgColorPicker.onColorChanged = { newColor ->
            tv.background = colorDrawable.apply { this.color = newColor(alphaSeekBar.progress, newColor) }
        }

        alphaSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) = transparentAllExcept(alphaSeekBar, tv_alpha)
            override fun onStopTrackingTouch(seekBar: SeekBar?) = transparentBack()
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv.background = colorDrawable.apply { this.color = newColor(progress, bgColorPicker.getCurrentColor()) }
            }
        })
    }


    /* 部分View透明 */
    private fun transparentAllExcept(vararg views: View) {
        repeat(container.childCount) { index ->
            val child = container.getChildAt(index)
            if (!views.contains(child)) {
                child.animate().cancel()
                child.animate().alpha(0f).setDuration(100).start()
            } else {
                child.alpha = child.alpha / 2f
            }
        }
    }

    /* 还原透明状态 */
    private fun transparentBack() {
        repeat(container.childCount) { index ->
            val child = container.getChildAt(index)
            val originAlpha = child.tag as Float
            child.animate().cancel()
            child.animate().alpha(originAlpha).setDuration(100).start()
        }
    }


    /* 根据TextView, 恢复UI显示的数据 */
    @SuppressLint("SetTextI18n")
    private fun restoreTextViewUIInfo(tv: TextView) {
        tv_text_content.text = tv.text

        val tvColor = tv.textColors.defaultColor
        colorPicker.setColor(tvColor)

        val size = tv.textSize.toInt()
        sizeSeekBar.max = dp2px(80)
        sizeSeekBar.progress = size
        tv_size.text = "文字大小: (${size})"

        val rotation = tv.rotation.toInt()
        rotationSeekBar.max = 359
        rotationSeekBar.progress = rotation
        tv_rotation.text = "文字旋转角度: (${rotation}°)"

        alphaSeekBar.max = 255
        val bgColor = (tv.background as? ColorDrawable)?.color
        if (bgColor != null) {
            val alpha = Color.alpha(bgColor)
            bgColorPicker.setColor(Color.rgb(Color.red(bgColor), Color.green(bgColor), Color.blue(bgColor)))
            alphaSeekBar.progress = alpha
        } else {
            bgColorPicker.setColor(Color.BLACK)
            alphaSeekBar.progress = 0
        }
    }


    private fun newColor(alpha: Int, color: Int): Int {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
    }

}