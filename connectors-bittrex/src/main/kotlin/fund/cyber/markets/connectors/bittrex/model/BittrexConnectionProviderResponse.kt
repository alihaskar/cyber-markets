package fund.cyber.markets.connectors.bittrex.model

/**
 * @author mgergalov
 */
class BittrexConnectionProviderResponse (
        val url : String,
        val connectionToken : String,
        val connectionId : String,
        val keepAliveTimeout : Double,
        val disconnectTimeout : Double,
        val connectionTimeout : Double,
        val tryWebSockets : Boolean,
        val protocolVersion: String,
        val transportConnectTimeout : Double,
        val longPollDelay : Double
)