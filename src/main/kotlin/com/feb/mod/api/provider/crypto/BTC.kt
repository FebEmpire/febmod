package com.feb.mod.api.provider.crypto

object BTC {
    fun get(): Double {
        return BTCProvider.get()
    }
}