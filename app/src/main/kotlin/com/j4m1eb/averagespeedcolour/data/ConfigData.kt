package com.j4m1eb.averagespeedcolour.data

data class ConfigData(
    val targetSpeed: Double,
    val useTeal: Boolean = false,
    val showIcons: Boolean = true,
    val hasSeenWelcome: Boolean = false,
    val swapRows: Boolean = false,
) {
    companion object {
        val DEFAULT = ConfigData(
            targetSpeed = 8.94,
            useTeal = false,
            showIcons = true,
            hasSeenWelcome = false,
            swapRows = false,
        )
    }

    fun validate(): Boolean {
        return targetSpeed >= 0
    }
}
