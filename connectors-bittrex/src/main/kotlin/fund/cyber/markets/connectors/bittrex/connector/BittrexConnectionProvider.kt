package fund.cyber.markets.connectors.bittrex.connector

import com.sun.deploy.net.URLEncoder
import fund.cyber.markets.connectors.bittrex.model.BittrexConnectionProviderResponse
import fund.cyber.markets.connectors.common.BITTREX_WS_ENDPOINT
import fund.cyber.markets.connectors.common.ConnectionProvider
import fund.cyber.markets.connectors.httpClient
import fund.cyber.markets.connectors.jsonParser
import fund.cyber.markets.helpers.await
import okhttp3.Request

/**
 * @author mgergalov
 */
class BittrexConnectionProvider : ConnectionProvider {
    suspend override fun getConnectionToken(): String {
        val bittrexTokenApiPrefix = "https://socket.bittrex.com/signalr/negotiate?"
        val clientProtocol = "1.5"
        val connectionData = "[{\"name\":\"corehub\"}]"
        val connectionString = bittrexTokenApiPrefix +
                "clientProtocol=" + clientProtocol +
                "&connectionData=" + connectionData +
                "&_" + System.currentTimeMillis()
        val connectionTokenRequest = Request.Builder().url(connectionString).build()!!
        val response = httpClient.newCall(connectionTokenRequest).await()
        val result = jsonParser.readTree(response.body()?.string())
        val bittrexConnectionProviderResponse = BittrexConnectionProviderResponse(
                url = result.get("Url").asText(),
                connectionToken = result.get("ConnectionToken").asText(),
                connectionId = result.get("ConnectionId").asText(),
                keepAliveTimeout = result.get("KeepAliveTimeout").asDouble(),
                disconnectTimeout = result.get("DisconnectTimeout").asDouble(),
                connectionTimeout = result.get("ConnectionTimeout").asDouble(),
                tryWebSockets = result.get("TryWebSockets").asBoolean(),
                protocolVersion = result.get("ProtocolVersion").asText(),
                transportConnectTimeout = result.get("TransportConnectTimeout").asDouble(),
                longPollDelay = result.get("LongPollDelay").asDouble()
        )
        val wsConnectionLine = BITTREX_WS_ENDPOINT +
                "transport=" + "webSockets" + "&" +
                "clientProtocol=" + clientProtocol + "&" +
                "connectionToken=" + URLEncoder.encode(bittrexConnectionProviderResponse.connectionToken, "UTF-8") + "&" +
                "connectionData=" + URLEncoder.encode(connectionData, "UTF-8") + "&" +
                "tid=" + 5
        return wsConnectionLine
    }
}