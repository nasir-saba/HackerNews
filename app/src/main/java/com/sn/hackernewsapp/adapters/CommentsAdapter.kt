package com.sn.hackernewsapp.adapters

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.Html
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sn.hackernewsapp.R
import com.sn.hackernewsapp.models.Comment
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlin.collections.ArrayList


class CommentsAdapter(
    private val itemsCells: ArrayList<Comment>,
    private val expandedSize: ArrayList<Int>) :
    RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    private lateinit var context: Context
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return itemsCells.size
    }

//    private var lastTappedCell: Int? = null
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val drawableLeft = ContextCompat.getDrawable(context, R.drawable.outline_comment_24)
        val drawableRightExpandMore = ContextCompat.getDrawable(context, R.drawable.outline_expand_more_24)
        val drawableRightExpandLess = ContextCompat.getDrawable(context, R.drawable.outline_expand_less_24)
        holder.itemView.tvCommentBy.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableRightExpandMore, null)
        holder.itemView.tvCommentBy.text =
            " " + itemsCells[position].user + ", " + itemsCells[position].time_ago
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.itemView.tvCommentContent.text =
                Html.fromHtml(itemsCells[position].content, Html.FROM_HTML_MODE_COMPACT)
        } else {
            holder.itemView.tvCommentContent.text = Html.fromHtml(itemsCells[position].content)
        }
        holder.itemView.tvCommentContent.layoutParams.height = expandedSize[position]
        holder.itemView.tvCommentBy.setOnClickListener {

            if (expandedSize[position] == 0) {
                holder.itemView.tvCommentBy.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableRightExpandLess, null)
                val answerTextViewHeight = height(
                    context,
                    itemsCells[position].content,
                    Typeface.DEFAULT,
                    16,
                    dp2px(15f, context)
                )
                changeViewSizeWithAnimation(
                    holder.itemView.tvCommentContent,
                    answerTextViewHeight,
                    300L
                )
                expandedSize[position] = answerTextViewHeight
            } else {
                holder.itemView.tvCommentBy.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableRightExpandMore, null)
                changeViewSizeWithAnimation(holder.itemView.tvCommentContent, 0, 300L)
                expandedSize[position] = 0
            }

            // to expand only one at a time
/*            if (lastTappedCell != null) {
                expandedSize[lastTappedCell!!] = 0
                notifyItemChanged(lastTappedCell!!)
            }
            lastTappedCell = position*/
        }
    }

    private fun changeViewSizeWithAnimation(view: View, viewSize: Int, duration: Long) {
        val startViewSize = view.measuredHeight
        val endViewSize: Int =
            if (viewSize < startViewSize) (viewSize) else (view.measuredHeight + viewSize)
        val valueAnimator =
            ValueAnimator.ofInt(startViewSize, endViewSize)
        valueAnimator.duration = duration
        valueAnimator.addUpdateListener {
            val animatedValue = valueAnimator.animatedValue as Int
            val layoutParams = view.layoutParams
            layoutParams.height = animatedValue
            view.layoutParams = layoutParams
        }
        valueAnimator.start()
    }

    private fun height(
        context: Context,
        text: String,
        typeface: Typeface?,
        textSize: Int,
        padding: Int
    ): Int {
        val textView = TextView(context)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize.toFloat())
        textView.setPadding(padding, padding, padding, padding)
        textView.typeface = typeface
        textView.text = text
        val mMeasureSpecWidth =
            View.MeasureSpec.makeMeasureSpec(getDeviceWidth(context), View.MeasureSpec.AT_MOST)
        val mMeasureSpecHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        textView.measure(mMeasureSpecWidth, mMeasureSpecHeight)
        return textView.measuredHeight
    }

    private fun dp2px(dpValue: Float, context: Context): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    private fun getDeviceWidth(context: Context): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val displayMetrics = DisplayMetrics()
            val display: Display? = context.display
            display?.getRealMetrics(displayMetrics)
            displayMetrics.widthPixels
        } else {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val displayMetrics = DisplayMetrics()
            wm.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }

}

