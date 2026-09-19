package com.feb.mod.api.provider

object ProviderManager {
    private val providers = mutableMapOf<String, Provider>()

    fun register(provider: Provider) {
        providers[provider.id] = provider
        provider.initialize()
    }

    fun unregister(id: String) {
        providers.remove(id)
    }

    fun get(id: String): Provider? {
        return providers[id]
    }

    fun getAll(): Collection<Provider> {
        return providers.values
    }

    fun clear() {
        providers.clear()
    }
}