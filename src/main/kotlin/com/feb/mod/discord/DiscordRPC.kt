package com.feb.mod.discord

import meteordevelopment.discordipc.DiscordIPC
import meteordevelopment.discordipc.RichPresence
import net.fabricmc.api.ModInitializer
import java.util.Timer
import kotlin.concurrent.timer
import com.feb.mod.ModInfo

/*
 * Very sick Discord RPC indeed
 * +rep @meteorclient
 * +rep @februari10
 * Disabled by default but can be enabled in the gui :fire:
 */

object DiscordRPC : ModInitializer {

    var isEnabled = false
        private set
    private var updateTimer: Timer? = null
    private var isConnected = false
    private var startTime = 0L

    override fun onInitialize() {
        if (isConnected) return
        connect()
    }

    private fun connect() {
        try {
            DiscordIPC.start(1396248557270601889, null)
            isConnected = true
            startTime = System.currentTimeMillis() / 1000L
            updateTimer?.cancel()
            updateTimer = timer(period = 5 * 60 * 1000) {
                if (isEnabled && isConnected) updateRPC()
            }
            updateRPC()
            println("Discord RPC connected")
        } catch (e: Exception) {
            println("Failed to connect Discord RPC: ${e.message}")
            isConnected = false
        }
    }

    fun toggle() {
        isEnabled = !isEnabled
        if (isEnabled) {
            if (!isConnected) connect() else updateRPC()
        } else {
            disconnect()
        }
    }

    fun setEnabled(enabled: Boolean) {
        if (isEnabled == enabled) return
        toggle()
    }

    private fun disconnect() {
        try {
            DiscordIPC.stop()
            isConnected = false
            println("Discord RPC disconnected!")
        } catch (e: Exception) {
            println("Failed to disconnect Discord RPC: ${e.message}")
        }
    }

    private fun updateRPC() {
        if (!isEnabled || !isConnected) return
        try {
            val rpc = RichPresence()
            rpc.setStart(startTime)
            rpc.setDetails("Using FebMod")
            rpc.setState(ModInfo.VERSION)
            rpc.setLargeImage("logo", "FebMod")
            DiscordIPC.setActivity(rpc)
        } catch (e: Exception) {
            println("Failed to update Discord RPC: ${e.message}")
        }
    }

    fun shutdown() {
        updateTimer?.cancel()
        updateTimer = null
        disconnect()
    }
}