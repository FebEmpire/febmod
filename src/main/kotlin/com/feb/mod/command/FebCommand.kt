package com.feb.mod.command

import com.feb.mod.api.command.CommandApi
import com.feb.mod.ui.gui.FebModGui
import net.minecraft.client.Minecraft

object FebCommand {

    fun register(commands: CommandApi) {
        commands.register("f", "feb", "febmod") {
            execute(it.args)
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