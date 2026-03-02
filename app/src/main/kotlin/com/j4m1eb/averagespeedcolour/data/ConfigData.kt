package com.j4m1eb.averagespeedcolour.data

data class ConfigData(
    val targetSpeed: Double,
) {
    companion object {
        val DEFAULT = ConfigData(
            targetSpeed = 8.94,
        )
    }

    fun validate(): Boolean {
        return targetSpeed >= 0
    }
}
