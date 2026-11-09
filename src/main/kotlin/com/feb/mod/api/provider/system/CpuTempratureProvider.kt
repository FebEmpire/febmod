package com.feb.mod.api.provider.system

import com.feb.mod.api.provider.Provider
import oshi.SystemInfo

object CpuTemperatureProvider : Provider {
    override val id = "cpu_temperature"

    private val sensors = SystemInfo().hardware.sensors

    val temperature: Double
        get() = sensors.cpuTemperature

    override fun initialize() {
    }
}