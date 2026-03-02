package com.j4m1eb.averagespeedcolour.data

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentSize
import androidx.glance.layout.wrapContentWidth
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
    var bottomTextPadding = 0f
    var finalTextSize: Float = config.textSize.toFloat()

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
            Color(context.getColor(R.color.dark_green)),
            Color(context.getColor(R.color.white))
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

    val headerTextSize = TextUnit(17f, TextUnitType.Sp)
    val averageSpeedFormatted: String = ((averageSpeed * 10.0).roundToInt() / 10.0).formated()
    
    val topRowHeight = 20f
    val bottomRowHeight: Float = viewHeightInDp - topRowHeight

    if (config.viewSize.first <= 238) {
        if (config.viewSize.second > 300) {
            bottomTextPadding += 11f
            if (currentSpeed >= 100.0) {
                finalTextSize -= 24f
            } else {
                finalTextSize -= 6f
            }
        } else if (config.viewSize.second < 128) {
            //(238,126)
            topRowPadding += 4f
            bottomTextPadding += 6f
        } else {
            //(238,148)
            topRowPadding += 6f
            bottomTextPadding += 11f
            if (currentSpeed >= 100.0) {
                finalTextSize -= 16f
                bottomTextPadding += 4f
            } else {
                finalTextSize -= 6f
            }
        }
    }

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(start = 5.dp, end = 5.dp, top = topRowPadding.dp)
            .cornerRadius(8.dp)
            .background(backgroundColor)
    ) {
        when (config.alignment) {
            ViewConfig.Alignment.CENTER ->
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(topRowHeight.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = GlanceModifier
                            .defaultWeight()
                            .wrapContentSize()
                            .padding(end = 2.dp, top = 5.dp),
                        provider = ImageProvider(
                            resId = R.drawable.icon_gauge,
                        ),
                        contentDescription = description,
                        colorFilter = ColorFilter.tint(ColorProvider(textColor)),
                    )
                    Text(
                        modifier = GlanceModifier
                            .padding(end = 2.dp, top = 0.dp),
                        text = averageSpeedFormatted,
                        style = TextStyle(
                            color = ColorProvider(textColor),
                            fontSize = headerTextSize,
                            textAlign = textAlignment,
                            fontFamily = FontFamily.SansSerif
                        )
                    )
                }

            ViewConfig.Alignment.LEFT ->
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(topRowHeight.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = GlanceModifier
                            .defaultWeight()
                            .padding(end = 2.dp, top = 0.dp),
                        text = averageSpeedFormatted,
                        style = TextStyle(
                            color = ColorProvider(textColor),
                            fontSize = headerTextSize,
                            textAlign = textAlignment,
                            fontFamily = FontFamily.SansSerif
                        )
                    )
                    Image(
                        modifier = GlanceModifier
                            .defaultWeight()
                            .wrapContentSize()
                            .padding(end = 2.dp, top = 5.dp),
                        provider = ImageProvider(
                            resId = R.drawable.icon_gauge,
                        ),
                        contentDescription = description,
                        colorFilter = ColorFilter.tint(ColorProvider(textColor)),
                    )
                }

            else ->
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(topRowHeight.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        modifier = GlanceModifier
                            .defaultWeight()
                            .wrapContentSize()
                            .padding(end = 2.dp, top = 5.dp),
                        provider = ImageProvider(
                            resId = R.drawable.icon_gauge,
                        ),
                        contentDescription = description,
                        colorFilter = ColorFilter.tint(ColorProvider(textColor)),
                    )
                    Text(
                        modifier = GlanceModifier
                            .defaultWeight()
                            .padding(end = 2.dp, top = 0.dp),
                        text = averageSpeedFormatted,
                        style = TextStyle(
                            color = ColorProvider(textColor),
                            fontSize = headerTextSize,
                            textAlign = textAlignment,
                            fontFamily = FontFamily.SansSerif
                        )
                    )
                }
        }
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(bottomRowHeight.dp)
                .padding(start = 0.dp, end = 8.dp),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.Start
        ) {
            ArrowProvider(
                modifier = GlanceModifier
                    .fillMaxHeight()
                    .defaultWeight()
                    .wrapContentWidth()
                    .width(30.dp),
                level = barLevel,
                color = textColor
            )
            Text(
                modifier = GlanceModifier
                    .padding(top = bottomTextPadding.dp)
                    .defaultWeight()
                    .fillMaxWidth(),
                text = ((currentSpeed * 10.0).roundToInt() / 10.0).formated(),
//                text = "${config.viewSize.second}",
//                text = "${config.viewSize.first}, ${config.viewSize.second}",
                style = TextStyle(
                    color = ColorProvider(textColor),
                    fontSize = TextUnit(finalTextSize, TextUnitType.Sp),
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

