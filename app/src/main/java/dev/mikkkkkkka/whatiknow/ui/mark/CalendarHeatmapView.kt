package dev.mikkkkkkka.whatiknow.ui.mark

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.max
import androidx.core.graphics.toColorInt

class CalendarHeatmapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    var onDateClick: ((LocalDate) -> Unit)? = null

    private var visibleMonth: YearMonth = YearMonth.now()
    private var selectedDate: LocalDate? = null
    private var activityCounts: Map<LocalDate, Int> = emptyMap()
    private val dayBounds = mutableMapOf<LocalDate, RectF>()

    private val gridGap = dp(6f)
    private val dayLabelHeight = dp(16f)
    private val topSpacing = dp(12f)
    private val bottomSpacing = dp(8f)
    private val cornerRadius = dp(8f)
    private val strokeWidth = dp(2f)

    private val emptyColor = "#EBEDF0".toColorInt()
    private val levelOneColor = "#C6E48B".toColorInt()
    private val levelTwoColor = "#7BC96F".toColorInt()
    private val levelThreeColor = "#239A3B".toColorInt()
    private val levelFourColor = "#196127".toColorInt()
    private val todayStrokeColor = "#1F2937".toColorInt()
    private val selectedStrokeColor = "#2563EB".toColorInt()
    private val labelTextColor = "#6B7280".toColorInt()
    private val dayNumberColor = "#111827".toColorInt()

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = this@CalendarHeatmapView.strokeWidth
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = labelTextColor
        textAlign = Paint.Align.CENTER
        textSize = sp(11f)
    }
    private val dayNumberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = dayNumberColor
        textAlign = Paint.Align.CENTER
        textSize = sp(11f)
    }

    fun setMonth(month: YearMonth) {
        if (visibleMonth == month) return
        visibleMonth = month
        invalidate()
    }

    fun setData(values: Map<LocalDate, Int>) {
        activityCounts = values
        invalidate()
    }

    fun setSelectedDate(date: LocalDate?) {
        selectedDate = date
        date?.let { visibleMonth = YearMonth.from(it) }
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = (paddingLeft + paddingRight + dp(320f)).toInt()
        val measuredWidth = resolveSize(desiredWidth, widthMeasureSpec)
        val contentWidth = (measuredWidth - paddingLeft - paddingRight).coerceAtLeast(0).toFloat()
        val cellSize = calculateCellSize(contentWidth)
        val contentHeight = topSpacing + dayLabelHeight + gridGap + cellSize * 6 + gridGap * 5 + bottomSpacing
        val desiredHeight = (paddingTop + paddingBottom + contentHeight).toInt()
        setMeasuredDimension(measuredWidth, resolveSize(desiredHeight, heightMeasureSpec))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        dayBounds.clear()

        val contentWidth = (width - paddingLeft - paddingRight).toFloat().coerceAtLeast(0f)
        val cellSize = calculateCellSize(contentWidth)
        val startX = paddingLeft.toFloat()
        val labelBaseline = paddingTop + topSpacing - labelPaint.fontMetrics.ascent
        val gridTop = paddingTop + topSpacing + dayLabelHeight + gridGap

        drawWeekdayLabels(canvas, startX, labelBaseline, cellSize)

        val firstDayOfMonth = visibleMonth.atDay(1)
        val daysInMonth = visibleMonth.lengthOfMonth()
        val firstDayColumn = firstDayOfMonth.dayOfWeek.toColumnIndex()
        val today = LocalDate.now()

        for (day in 1..daysInMonth) {
            val date = visibleMonth.atDay(day)
            val index = firstDayColumn + day - 1
            val row = index / 7
            val column = index % 7

            val left = startX + column * (cellSize + gridGap)
            val top = gridTop + row * (cellSize + gridGap)
            val rect = RectF(left, top, left + cellSize, top + cellSize)
            dayBounds[date] = rect

            fillPaint.color = colorForCount(activityCounts[date] ?: 0)
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, fillPaint)

            if (date == today) {
                strokePaint.color = todayStrokeColor
                canvas.drawRoundRect(rect, cornerRadius, cornerRadius, strokePaint)
            }

            if (date == selectedDate) {
                strokePaint.color = selectedStrokeColor
                canvas.drawRoundRect(rect, cornerRadius, cornerRadius, strokePaint)
            }

            val textX = rect.centerX()
            val textY = rect.centerY() - (dayNumberPaint.fontMetrics.ascent + dayNumberPaint.fontMetrics.descent) / 2f
            canvas.drawText(day.toString(), textX, textY, dayNumberPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val selected = dayBounds.entries.firstOrNull { it.value.contains(event.x, event.y) }?.key
            if (selected != null) {
                selectedDate = selected
                invalidate()
                onDateClick?.invoke(selected)
                performClick()
                return true
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    private fun drawWeekdayLabels(canvas: Canvas, startX: Float, baseline: Float, cellSize: Float) {
        WEEKDAY_LABELS.forEachIndexed { index, label ->
            val x = startX + index * (cellSize + gridGap) + cellSize / 2f
            canvas.drawText(label, x, baseline, labelPaint)
        }
    }

    private fun calculateCellSize(contentWidth: Float): Float {
        val totalGaps = gridGap * 6
        return max(dp(28f), (contentWidth - totalGaps) / 7f)
    }

    private fun colorForCount(count: Int): Int {
        return when {
            count <= 0 -> emptyColor
            count == 1 -> levelOneColor
            count <= 3 -> levelTwoColor
            count <= 5 -> levelThreeColor
            else -> levelFourColor
        }
    }

    private fun DayOfWeek.toColumnIndex(): Int = when (this) {
        DayOfWeek.MONDAY -> 0
        DayOfWeek.TUESDAY -> 1
        DayOfWeek.WEDNESDAY -> 2
        DayOfWeek.THURSDAY -> 3
        DayOfWeek.FRIDAY -> 4
        DayOfWeek.SATURDAY -> 5
        DayOfWeek.SUNDAY -> 6
    }

    private fun dp(value: Float): Float = value * resources.displayMetrics.density

    private fun sp(value: Float): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        value,
        resources.displayMetrics,
    )

    companion object {
        private val WEEKDAY_LABELS = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
    }
}
