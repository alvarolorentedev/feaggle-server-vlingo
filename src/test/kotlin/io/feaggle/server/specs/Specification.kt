/**
 * This file is part of feaggle-server.
 *
 * feaggle-server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * feaggle-server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with feaggle-server.  If not, see <https://www.gnu.org/licenses/>.
 **/
package io.feaggle.server.specs

import io.feaggle.server.ApplicationServer
import io.restassured.RestAssured
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.testcontainers.containers.PostgreSQLContainer

abstract class Specification {
    companion object {
        private val postgreSql = PostgreSQLContainer<Nothing>()
        private val port = 9092
        private val baseUrl = "http://localhost:$port"
        private val applicationServer: ApplicationServer

        init {
            postgreSql.start()
            RestAssured.baseURI = baseUrl
            RestAssured.config.httpClientConfig.dontReuseHttpClientInstance()

            applicationServer = ApplicationServer(postgreSql.jdbcUrl, postgreSql.username, postgreSql.password, port)
            applicationServer.start()
        }
    }
}