package fund.cyber.markets.connectors.bittrex

import fund.cyber.markets.connectors.bittrex.connector.BittrexOrdersEndpoint
import fund.cyber.markets.connectors.bittrex.connector.BittrexTradesEndpoint
import fund.cyber.markets.connectors.common.kafka.ConnectorKafkaProducer
import fund.cyber.markets.connectors.common.kafka.OrdersUpdateProducerRecord
import fund.cyber.markets.connectors.common.kafka.TradeProducerRecord
import fund.cyber.markets.connectors.helpers.concurrent
import fund.cyber.markets.model.OrdersBatch
import fund.cyber.markets.model.Trade
import java.sql.Timestamp

val tradeKafkaProducer = ConnectorKafkaProducer<Trade>()
val orderKafkaProducer = ConnectorKafkaProducer<OrdersBatch>()
fun main(args: Array<String>) {
    val debugMode = System.getProperty("debug") != null
    val bittrexEndpoint = BittrexTradesEndpoint(
            clientProtocol = "1.5",
            connectionData = "[{\"name\":\"corehub\"}]",
            queryTimestamp = System.currentTimeMillis(),
            transport = "webSockets",
            connectionToken = "",
            tid = 3
    )

//    supportedTradesEndpoints.forEach { exchange ->
        concurrent {
            val dataChannel = bittrexEndpoint.subscribe()
            concurrent {
                while (true) {
                    val message = dataChannel.receive()
                    message.trades.forEach { trade ->
                        if (true) println(trade) else tradeKafkaProducer.send(TradeProducerRecord(trade))
                    }
                }
            }
        }
//    }

//    supportedOrdersEndpoints.forEach { exchange ->
//        concurrent {
//            val dataChannel = exchange.subscribe()
//            concurrent {
//                while (true) {
//                    val message = dataChannel.receive()
//                    if (debugMode) println(message)
//                    else orderKafkaProducer.send(OrdersUpdateProducerRecord(message.ordersBatch))
//                }
//            }
//        }
//    }
}