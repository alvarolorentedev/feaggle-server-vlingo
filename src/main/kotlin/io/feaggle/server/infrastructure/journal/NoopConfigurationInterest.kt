package io.feaggle.server.infrastructure.journal

import io.vlingo.symbio.store.common.jdbc.Configuration
import java.sql.Connection

class NoopConfigurationInterest: Configuration.ConfigurationInterest {
    override fun afterConnect(connection: Connection?) {

    }

    override fun beforeConnect(configuration: Configuration?) {
    }

    override fun dropDatabase(connection: Connection?, databaseName: String?) {
    }

    override fun createDatabase(connection: Connection?, databaseName: String?) {
    }
}