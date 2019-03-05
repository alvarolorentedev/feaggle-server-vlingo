package io.feaggle.server.specs

import io.feaggle.server.ApplicationServer
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.testcontainers.containers.PostgreSQLContainer

abstract class Specification {
    private val applicationServer = ApplicationServer()
    private val postgreSql = PostgreSQLContainer<Nothing>()

    @BeforeEach
    internal fun setUp() {
        postgreSql.start()

        Flyway(
            Flyway.configure()
                .dataSource(postgreSql.jdbcUrl, postgreSql.username, postgreSql.password)
        ).migrate()

        applicationServer.start()
    }

    @AfterEach
    internal fun tearDown() {
        applicationServer.stop()
        postgreSql.stop()
    }
}