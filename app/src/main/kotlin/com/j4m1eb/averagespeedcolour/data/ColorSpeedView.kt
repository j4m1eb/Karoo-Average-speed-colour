package com.j4m1eb.averagespeedcolour.data

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontFamily
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.j4m1eb.averagespeedcolour.R
import io.hammerhead.karooext.models.ViewConfig
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.roundToInt

private fun Double.formated(): String {
    val currentLocale = Locale.getDefault()
    val numberFormat = NumberFormat.getInstance(currentLocale).apply {
        minimumFractionDigits = 1
        maximumFractionDigits = 1
    }
    return numberFormat.format(this)
}

@SuppressLint("RestrictedApi", "DiscouragedApi")
@Composable
fun ColorSpeedView(
    context: Context,
    currentSpeed: Double,
    averageSpeed: Double,
    config: ViewConfig,
    colorConfig: ConfigData,
    titleResource: String,
    description: String,
    speedUnits: Double,
) {

    var topRowPadding = 0f
    if (config.viewSize.first <= 238) {
        topRowPadding = 2f
    }

    val finalTextSize: Float = config.textSize.toFloat()

    val viewHeightInDp: Float = ceil(config.viewSize.second / context.resources.displayMetrics.density)

    val speedDiff: Double = if (currentSpeed > 0 && averageSpeed > 0) {
        currentSpeed - averageSpeed
    } else {
        0.0
    }

    val textAlignment: TextAlign = when (config.alignment) {
            ViewConfig.Alignment.CENTER -> TextAlign.Center
            ViewConfig.Alignment.LEFT -> TextAlign.Start
            ViewConfig.Alignment.RIGHT -> TextAlign.End
    }

    val (backgroundColor: Color, textColor: Color) = when {
        currentSpeed <= (2.0 / 3.6) * speedUnits -> Pair(
            Color.Transparent,
            Color(context.getColor(R.color.text_color))
        )
        speedDiff > 1.0 -> Pair(
            if (colorConfig.useTeal) Color(context.getColor(R.color.teal))
            else Color(context.getColor(R.color.dark_green)),
            Color(context.getColor(R.color.black))
        )
        speedDiff < -1.0 -> Pair(
            Color(context.getColor(R.color.dark_red)),
            Color(context.getColor(R.color.white))
        )
        else -> Pair(Color.Transparent, Color(context.getColor(R.color.text_color)))
    }

    val barLevel: Int = when {
        currentSpeed <= (2.0 / 3.6) * speedUnits -> 0
        speedDiff > 1.0 -> 5
        speedDiff < -1.0 -> 1
        else -> 3
    }

    // Measure the actual Monospace font metrics to find how far the baseline sits
    // below the top of a TextView (includes the top-font-padding Android adds by default).
    // Cap-height digits (0–9) end exactly at the baseline, so placing the baseline
    // 2 dp above the field bottom seats digits flush with the edge without being
    // clipped by the corner radius.  This bottom-aligns the number correctly for any
    // field size where the text fits; for very wide fields with larger SDK textSizes the
    // 20 dp floor keeps the header visible.
    val density = context.resources.displayMetrics.density
    val numPaint = Paint().apply {
        textSize = finalTextSize * density
        typeface = Typeface.MONOSPACE
    }
    val fm = numPaint.fontMetrics
    val baselineFromTopDp = (-fm.top) / density   // top of TextView → baseline
    val topRowHeight = maxOf(20f, viewHeightInDp - topRowPadding - baselineFromTopDp - 5f)
    val headerTextSize = TextUnit(18f, TextUnitType.Sp)
    val averageSpeedFormatted: String = ((averageSpeed * 10.0).roundToInt() / 10.0).formated()
    val currentSpeedFormatted: String = ((currentSpeed * 10.0).roundToInt() / 10.0).formated()
    val isDoubleWidth = config.viewSize.first > 400

    // Header group (arrow + avg speed) honours the same alignment the user picked for
    // the main number, so left/centre/right fields all look consistent.
    val headerHorizontalAlignment: Alignment.Horizontal = when (config.alignment) {
        ViewConfig.Alignment.CENTER -> Alignment.CenterHorizontally
        ViewConfig.Alignment.LEFT   -> Alignment.Start
        ViewConfig.Alignment.RIGHT  -> Alignment.End
    }

    if (isDoubleWidth) {
        // Double-width: show both numbers side by side at full size.
        // swap toggles left/right positions.
        val leftSpeed = if (colorConfig.swapRows) currentSpeedFormatted else averageSpeedFormatted
        val rightSpeed = if (colorConfig.swapRows) averageSpeedFormatted else currentSpeedFormatted
        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(start = 5.dp, end = 5.dp)
                .cornerRadius(8.dp)
                .background(backgroundColor),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = GlanceModifier.defaultWeight(),
                text = leftSpeed,
                style = TextStyle(
                    color = ColorProvider(textColor),
                    fontSize = TextUnit(finalTextSize, TextUnitType.Sp),
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Monospace,
                )
            )
            Text(
                modifier = GlanceModifier.defaultWeight(),
                text = rightSpeed,
                style = TextStyle(
                    color = ColorProvider(textColor),
                    fontSize = TextUnit(finalTextSize, TextUnitType.Sp),
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Monospace,
                )
            )
        }
    } else {
        // Single-width: stacked layout. swap toggles which value is top/bottom.
        val topText = if (colorConfig.swapRows) currentSpeedFormatted else averageSpeedFormatted
        val bottomText = if (colorConfig.swapRows) averageSpeedFormatted else currentSpeedFormatted
        val bottomTextSize = TextUnit(finalTextSize, TextUnitType.Sp)
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(start = 5.dp, end = 5.dp, top = topRowPadding.dp)
                .cornerRadius(8.dp)
                .background(backgroundColor)
        ) {
            // Header row: icon + top value
            Row(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(topRowHeight.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = headerHorizontalAlignment
            ) {
                if (colorConfig.showIcons) {
                    ArrowProvider(
                        modifier = GlanceModifier
                            .height(18.dp)
                            .width(18.dp)
                            .padding(end = 3.dp),
                        level = barLevel,
                        color = textColor
                    )
                } else {
                    Image(
                        modifier = GlanceModifier
                            .height(18.dp)
                            .width(18.dp)
                            .padding(top = 4.dp, end = 3.dp),
                        provider = ImageProvider(resId = R.drawable.icon_avg_pace),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(ColorProvider(textColor))
                    )
                }
                Text(
                    text = topText,
                    style = TextStyle(
                        color = ColorProvider(textColor),
                        fontSize = headerTextSize,
                        textAlign = textAlignment
                    )
                )
            }
            // Bottom row: big number
            Text(
                modifier = GlanceModifier.fillMaxWidth(),
                text = bottomText,
                style = TextStyle(
                    color = ColorProvider(textColor),
                    fontSize = bottomTextSize,
                    textAlign = textAlignment,
                    fontFamily = FontFamily.Monospace,
                )
            )
        }
    }
}

@Suppress("unused")
@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(
    widthDp = (238 / 1.9).toInt(),
    heightDp = (148 / 1.9).toInt()
)
@Composable
fun PreviewNoSpeed() {
    ColorSpeedView(
        context = LocalContext.current,
        currentSpeed = 0.0,
        averageSpeed = 100.0,
        titleResource = "lap_speed_title",
        description = "Stuff",
        config = ViewConfig(
            alignment = ViewConfig.Alignment.RIGHT,
            textSize = 50,
            gridSize = Pair(30, 15),
            viewSize = Pair(238, 148),
            preview = true
        ),
        colorConfig = ConfigData.DEFAULT,
        speedUnits = 2.23694
    )
}

@Suppress("unused")
@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = (238 / 1.9).toInt(), heightDp = (148 / 1.9).toInt())
@Composable
fun PreviewBelowAverage() {
    ColorSpeedView(
        context = LocalContext.current,
        currentSpeed = 18.0,
        averageSpeed = 20.0,
        titleResource = "speed_title",
        description = "Below average",
        config = ViewConfig(
            alignment = ViewConfig.Alignment.CENTER,
            textSize = 50,
            gridSize = Pair(30, 15),
            viewSize = Pair(238, 148),
            preview = true
        ),
        colorConfig = ConfigData.DEFAULT,
        speedUnits = 2.23694
    )
}

@Suppress("unused")
@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = (238 / 1.9).toInt(), heightDp = (148 / 1.9).toInt())
@Composable
fun PreviewAtAverage() {
    ColorSpeedView(
        context = LocalContext.current,
        currentSpeed = 20.0,
        averageSpeed = 20.0,
        titleResource = "speed_title",
        description = "At average",
        config = ViewConfig(
            alignment = ViewConfig.Alignment.CENTER,
            textSize = 50,
            gridSize = Pair(30, 15),
            viewSize = Pair(238, 148),
            preview = true
        ),
        colorConfig = ConfigData.DEFAULT,
        speedUnits = 2.23694
    )
}

@Suppress("unused")
@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = (238 / 1.9).toInt(), heightDp = (148 / 1.9).toInt())
@Composable
fun PreviewAboveAverage() {
    ColorSpeedView(
        context = LocalContext.current,
        currentSpeed = 22.0,
        averageSpeed = 20.0,
        titleResource = "speed_title",
        description = "Above average",
        config = ViewConfig(
            alignment = ViewConfig.Alignment.CENTER,
            textSize = 50,
            gridSize = Pair(30, 15),
            viewSize = Pair(238, 148),
            preview = true
        ),
        colorConfig = ConfigData.DEFAULT,
        speedUnits = 2.23694
    )
}

