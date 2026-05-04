package dev.mikkkkkkka.whatiknow.ui.mark

import android.content.Context
import android.graphics.Canvas
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
    private val dayCells = mutableListOf<DayCell>()
    private var dayCellsDirty = true

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
        dayCellsDirty = true
        invalidate()
    }

    fun setData(values: Map<LocalDate, Int>) {
        activityCounts = values
        invalidate()
    }

    fun setSelectedDate(date: LocalDate?) {
        val previousMonth = visibleMonth
        selectedDate = date
        date?.let { visibleMonth = YearMonth.from(it) }
        if (previousMonth != visibleMonth) {
            dayCellsDirty = true
        }
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
        ensureDayCells()

        val contentWidth = (width - paddingLeft - paddingRight).toFloat().coerceAtLeast(0f)
        val cellSize = calculateCellSize(contentWidth)
        val startX = paddingLeft.toFloat()
        val labelBaseline = paddingTop + topSpacing - labelPaint.fontMetrics.ascent

        drawWeekdayLabels(canvas, startX, labelBaseline, cellSize)

        for (cell in dayCells) {
            val date = cell.date
            val rect = cell.bounds
            fillPaint.color = colorForCount(activityCounts[date] ?: 0)
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, fillPaint)

            if (cell.isToday) {
                strokePaint.color = todayStrokeColor
                canvas.drawRoundRect(rect, cornerRadius, cornerRadius, strokePaint)
            }

            if (date == selectedDate) {
                strokePaint.color = selectedStrokeColor
                canvas.drawRoundRect(rect, cornerRadius, cornerRadius, strokePaint)
            }

            val textX = rect.centerX()
            val textY = rect.centerY() - (dayNumberPaint.fontMetrics.ascent + dayNumberPaint.fontMetrics.descent) / 2f
            canvas.drawText(cell.dayLabel, textX, textY, dayNumberPaint)
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val selected = dayCells.firstOrNull { it.bounds.contains(event.x, event.y) }?.date
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != oldw || h != oldh) {
            dayCellsDirty = true
        }
    }

    private fun drawWeekdayLabels(canvas: Canvas, startX: Float, baseline: Float, cellSize: Float) {
        WEEKDAY_LABELS.forEachIndexed { index, label ->
            val x = startX + index * (cellSize + gridGap) + cellSize / 2f
            canvas.drawText(label, x, baseline, labelPaint)
        }
    }

    private fun ensureDayCells() {
        if (!dayCellsDirty) return

        dayCells.clear()

        val contentWidth = (width - paddingLeft - paddingRight).toFloat().coerceAtLeast(0f)
        val cellSize = calculateCellSize(contentWidth)
        val startX = paddingLeft.toFloat()
        val gridTop = paddingTop + topSpacing + dayLabelHeight + gridGap

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
            dayCells += DayCell(
                date = date,
                bounds = RectF(left, top, left + cellSize, top + cellSize),
                dayLabel = day.toString(),
                isToday = date == today,
            )
        }

        dayCellsDirty = false
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

    private data class DayCell(
        val date: LocalDate,
        val bounds: RectF,
        val dayLabel: String,
        val isToday: Boolean,
    )
}
