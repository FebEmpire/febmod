package com.feb.mod.command

import com.feb.mod.ui.gui.FebModGui
import net.minecraft.client.Minecraft

object FebCommand {
    fun register() {
        DotCommands.register("f", "feb", "febmod") { args ->
            execute(args)
        }
    }

    private fun execute(args: List<String>) {
        val mc = Minecraft.getInstance()

        when (args.firstOrNull()?.lowercase()) {
            "addon", "addons" -> {
                mc.execute {
                    mc.setScreen(
                        FebModGui(FebModGui.TopTab.ADDONS)
                    )
                }
            }

            else -> {
                mc.execute {
                    mc.setScreen(FebModGui())
                }
            }
        }
    }
}
