package com.j4m1eb.averagespeedcolour.extension

import com.j4m1eb.averagespeedcolour.KarooSystemServiceProvider
import com.j4m1eb.averagespeedcolour.BuildConfig
import com.j4m1eb.averagespeedcolour.data.CurrentVsLapAverageSpeed
import com.j4m1eb.averagespeedcolour.data.CurrentVsRideAverageSpeed
import com.j4m1eb.averagespeedcolour.data.LapVsTargetColorSpeed
import com.j4m1eb.averagespeedcolour.data.CurrentLapVsLLAverageSpeed
import com.j4m1eb.averagespeedcolour.data.SpeedVsTargetColorSpeed
import io.hammerhead.karooext.extension.KarooExtension
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.getValue


class ColorSpeedExtension : KarooExtension("karoocolorspeed", BuildConfig.VERSION_NAME) {

    private val karooSystem: KarooSystemServiceProvider by inject()


    override val types by lazy {
        listOf(
            CurrentVsRideAverageSpeed(karooSystem, extension),
            CurrentLapVsLLAverageSpeed(karooSystem, extension),
            CurrentVsLapAverageSpeed(karooSystem, extension),
            SpeedVsTargetColorSpeed(karooSystem, extension),
            LapVsTargetColorSpeed(karooSystem, extension)
        )
    }

    override fun onCreate() {
        super.onCreate()
        karooSystem.karooSystemService.connect { connected ->
            Timber.d("Karoo connected: $connected")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        karooSystem.karooSystemService.disconnect()
        Timber.d("Karoo disconnected")
    }
}

