package com.feb.mod.api.provider.crypto

object LTC {
    fun get(): Double {
        return LTCProvider.get()
    }
}