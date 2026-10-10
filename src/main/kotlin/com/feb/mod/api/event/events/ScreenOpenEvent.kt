package com.feb.mod.api.event.events

import com.feb.mod.api.event.Event
import com.feb.mod.api.gui.ScreenView

class ScreenOpenEvent(
    val screen: ScreenView
) : Event()