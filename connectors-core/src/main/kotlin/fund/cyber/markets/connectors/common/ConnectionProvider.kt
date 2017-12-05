package fund.cyber.markets.connectors.common

/**
 * @author mgergalov
 */
interface ConnectionProvider {
    suspend fun getConnectionToken(): String
}