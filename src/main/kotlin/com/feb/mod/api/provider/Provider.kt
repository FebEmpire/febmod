package com.feb.mod.api.provider

interface Provider {
    val id: String
    fun initialize()
}