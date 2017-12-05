package fund.cyber.markets.connectors.bittrex.connector

import fund.cyber.markets.connectors.common.ExchangeMessage
import fund.cyber.markets.connectors.common.GDAX_WS_ENDPOINT
import fund.cyber.markets.connectors.common.ws.OrdersWsEndpoint
import fund.cyber.markets.connectors.common.ws.TradesWsEndpoint
import fund.cyber.markets.connectors.common.ws.pusher.PusherMessage
import org.slf4j.LoggerFactory

class BittrexTradesEndpoint(
        val wsConnectionUrl : String
) : TradesWsEndpoint(wsConnectionUrl) {

    private val LOGGER = LoggerFactory.getLogger(BittrexTradesEndpoint::class.java)!!

    override val name: String = "Bittrex Trades"
    override val messageParser = BittrexTradesMessageParser(channelSymbolForTokensPairs)
    override val pairsProvider = BittrexPairsProvider()

    override fun getSubscriptionMsgByChannelSymbol(pairSymbol: String): String {
//        return """{"type":"subscribe","channels":[{"name":"matches","product_ids":["$pairSymbol"]}]}"""
        return """{"H":"corehub","M":"SubscribeToExchangeDeltas","A":["BTC-LTC"],"I":0}"""
    }

    override fun handleUnknownMessage(message: ExchangeMessage) {
        if (message is PusherMessage) {
            LOGGER.debug(message.message(name))
        } else {
            super.handleUnknownMessage(message)
        }
    }


}

class BittrexOrdersEndpoint : OrdersWsEndpoint(GDAX_WS_ENDPOINT) {
    private val LOGGER = LoggerFactory.getLogger(BittrexOrdersEndpoint::class.java)!!

    override val name: String = "Bittrex Orders"
    override val messageParser = BittrexOrdersMessageParser(channelSymbolForTokensPairs)
    override val pairsProvider = BittrexPairsProvider()

    override fun getSubscriptionMsgByChannelSymbol(pairSymbol: String): String {
        return """{"type":"subscribe","channels":[{"name":"level2_50","product_ids":["$pairSymbol"]}]}"""
    }

    override fun handleUnknownMessage(message: ExchangeMessage) {
        if (message is PusherMessage) {
            LOGGER.debug(message.message(name))
        } else {
            super.handleUnknownMessage(message)
        }
    }
}
