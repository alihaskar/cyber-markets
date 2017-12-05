package fund.cyber.markets.connectors.bittrex.connector

import fund.cyber.markets.connectors.common.PairsProvider
import fund.cyber.markets.connectors.httpClient
import fund.cyber.markets.connectors.jsonParser
import fund.cyber.markets.helpers.await
import fund.cyber.markets.model.TokensPairInitializer
import okhttp3.Request

class BittrexPairsProvider : PairsProvider {

    val symbolsRequest = Request.Builder().url("https://bittrex.com/api/v1.1/public/getmarkets").build()!!

    suspend override fun getPairs(): Map<String, TokensPairInitializer> {
        val result = mutableMapOf<String, TokensPairInitializer>()
        val response = httpClient.newCall(symbolsRequest).await()
        //todo: add checkResponse isAvailableResource()
        val pairsTickers = jsonParser.readTree(response.body()?.string())
        pairsTickers.get("result").toList()?.forEach { pair ->
            if(pair.get("IsActive").asBoolean()) {
                result.put(pair.get("MarketName").asText(),
                        TokensPairInitializer(pair.get("BaseCurrency").asText(), pair.get("MarketCurrency").asText()))
            }
        }
        return result
    }
}