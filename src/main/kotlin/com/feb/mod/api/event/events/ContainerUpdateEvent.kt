package com.feb.mod.api.event.events

import com.feb.mod.api.event.Event
import com.feb.mod.api.gui.ContainerView

class ContainerUpdateEvent(
    val container: ContainerView
) : Event()