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
package io.feaggle.server.base

import io.feaggle.server.ApplicationServer
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import org.testcontainers.containers.PostgreSQLContainer
import java.lang.AssertionError
import java.lang.Thread.sleep

private const val EVENTUALLY_MAX_TIMES = 10
private const val EVENTUALLY_SLEEP_TIME_MS = 500L

abstract class Specification {
    protected fun declare(nameOfDeclaration: String) {
        val declaration = javaClass.classLoader.getResource("declarations/$nameOfDeclaration.yml").readText()
        print("Sending declaration $nameOfDeclaration with content:\n$declaration")

        given()
            .body(declaration)
            .put("/declaration/$nameOfDeclaration")
            .then()
            .statusCode(202)
    }

    protected fun eventually(f: () -> Unit) {
        var times = 0
        val initTime = System.currentTimeMillis()
        var lastExcp: Throwable = RuntimeException()

        while (times < EVENTUALLY_MAX_TIMES) {
            try {
                f()
                return
            } catch (e: AssertionError) {
                sleep(EVENTUALLY_SLEEP_TIME_MS)
                lastExcp = e
            }
            times++
        }

        val elapsed = System.currentTimeMillis() - initTime
        print("Test failed after $times times. Total elapsed time: $elapsed ms")
        throw lastExcp
    }

    companion object {
        private val postgreSql = PostgreSQLContainer<Nothing>()
        private val port = 9092
        private val baseUrl = "http://localhost:$port"
        private val applicationServer: ApplicationServer

        init {
            postgreSql.start()
            RestAssured.baseURI = baseUrl
            RestAssured.config.httpClientConfig.dontReuseHttpClientInstance()

            applicationServer = ApplicationServer(
                postgreSql.jdbcUrl, postgreSql.username, postgreSql.password,
                port
            )
            applicationServer.start()
        }
    }
}