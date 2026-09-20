package com.feb.mod

import com.feb.mod.api.chat.ModMessage
import com.feb.mod.api.event.EventBus
import com.feb.mod.api.event.events.ChatReceivedEvent
import com.feb.mod.api.event.events.GameTickEvent
import com.feb.mod.api.event.events.RenderFrameEvent
import com.feb.mod.api.provider.ProviderRegister
import com.feb.mod.discord.DiscordRPC
import com.feb.mod.manager.AddonManager
import com.feb.mod.manager.FebModManager
import com.feb.mod.ui.hud.FebModHud
import com.feb.mod.feature.Feature
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents
import org.slf4j.LoggerFactory

object Febmod : ModInitializer {

    private val logger = LoggerFactory.getLogger("febmod")

    override fun onInitialize() {
        logger.info("Initializing FebMod")
        logger.info("Thank you for using FebMod")
        logger.info("You are goated")

        DiscordRPC.onInitialize()
        FebModManager.init()
        ProviderRegister.registerAll()
        Feature.registerAll()

        LevelRenderEvents.END_MAIN.register {
            EventBus.post(RenderFrameEvent())
        }

        ClientTickEvents.END_CLIENT_TICK.register {
            EventBus.post(GameTickEvent())
        }

        ClientReceiveMessageEvents.ALLOW_CHAT.register { text, _, _, _, _ ->
            val event = ChatReceivedEvent(
                text = text.string,
                isSystem = false
            )

            EventBus.post(event)

            !event.cancelled
        }

        FebModHud.register()

        ClientReceiveMessageEvents.GAME.register { text, overlay ->
            if (!overlay) {
                EventBus.post(
                    ChatReceivedEvent(
                        text = text.string,
                        isSystem = true
                    )
                )
            }
        }

        logger.info("Loaded ${AddonManager.getAddons().size} addon(s)")
    }

    fun sendMessage(message: String) =
        ModMessage.send(message)
}