package com.feb.mod.api.provider.crypto

object ETH {
    fun get(): Double {
        return ETHProvider.get()
    }
}