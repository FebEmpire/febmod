package com.feb.mod.api.provider.crypto

import com.feb.mod.api.provider.Provider
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object ETHProvider : Provider {
    override val id = "eth"

    private const val API_URL = "https://api.coingecko.com/api/v3/simple/price?ids=ethereum&vs_currencies=usd"
    private const val REFRESH_INTERVAL_SECONDS = 60L

    private val client = HttpClient.newHttpClient()
    private val executor = Executors.newSingleThreadScheduledExecutor()

    @Volatile
    private var price = 0.0

    override fun initialize() {
        refresh()

        executor.scheduleAtFixedRate(
            ::refresh,
            REFRESH_INTERVAL_SECONDS,
            REFRESH_INTERVAL_SECONDS,
            TimeUnit.SECONDS
        )
    }

    fun get(): Double {
        return price
    }

    private fun refresh() {
        runCatching {
            val request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build()

            val response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            )

            if (response.statusCode() != 200) {
                return
            }

            val body = response.body()
            val marker = "\"usd\":"
            val start = body.indexOf(marker)

            if (start == -1) {
                return
            }

            val valueStart = start + marker.length
            val valueEnd = body.indexOf('}', valueStart)

            if (valueEnd == -1) {
                return
            }

            val newPrice = body
                .substring(valueStart, valueEnd)
                .trim()
                .toDoubleOrNull()
                ?: return

            if (newPrice > 0.0) {
                price = newPrice
            }
        }
    }
}