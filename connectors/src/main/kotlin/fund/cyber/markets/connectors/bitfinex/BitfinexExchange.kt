package fund.cyber.markets.connectors.bitfinex

import fund.cyber.markets.connectors.common.ExchangeMessage
import fund.cyber.markets.connectors.common.ws.ReconnectableWsExchange
import fund.cyber.markets.model.TokensPair
import io.undertow.websockets.core.WebSocketChannel
import io.undertow.websockets.core.WebSockets
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

class BitfinexExchange : ReconnectableWsExchange() {

    private val LOGGER = LoggerFactory.getLogger(BitfinexExchange::class.java)!!


    override val name = "Bitfinex"
    override val wsAddress = "wss://api.bitfinex.com/ws/2"

    private val channelSymbolForTokensPair = HashMap<String, TokensPair>()
    private val tradesChannelIdForTokensPair = ConcurrentHashMap<Int, TokensPair>(64, 0.75f, 5)
    override val messageParser = BitfinexMessageParser(channelSymbolForTokensPair, tradesChannelIdForTokensPair)

    override suspend fun initializeMetadata() {
        channelSymbolForTokensPair.put("tETHBTC", TokensPair("ETH", "BTC"))
    }

    override fun subscribeChannels(connection: WebSocketChannel) {
        channelSymbolForTokensPair.keys.forEach { symbol ->
            WebSockets.sendText("""{"event":"subscribe","channel":"trades","symbol":"$symbol"}""", connection, null)
        }
    }

    override fun handleUnknownMessage(message: ExchangeMessage) {
        super.handleUnknownMessage(message)
        if (message is TradeChannelSubscribed) {
            tradesChannelIdForTokensPair.put(message.channelId, message.tokensPair)
            LOGGER.debug("Subscribed to Bitfinex ${message.tokensPair.label()} channel")
        }
    }
}