package com.feb.mod

import com.feb.mod.discord.DiscordRPC
import com.feb.mod.manager.AddonManager
import com.feb.mod.manager.CommandManager
import com.feb.mod.utils.ChatUtils
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object Febmod : ModInitializer {
    private val logger = LoggerFactory.getLogger("febmod")

    override fun onInitialize() {
        logger.info("Initializing FebMod")
        logger.info("Thank you for using FebMod")
        logger.info("You are goated")
        DiscordRPC.onInitialize()
        CommandManager.registerAll()
        AddonManager.loadAddons()
        logger.info("Loaded ${AddonManager.getAddons().size} addon(s)")
    }

    fun sendMessage(message: String) = ChatUtils.modMessage(message)
}