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
package io.feaggle.server.specs.toggles

import io.feaggle.server.specs.Specification
import io.restassured.RestAssured.given
import org.junit.jupiter.api.Test
import java.util.*

class ReleaseToggleTest: Specification() {
    @Test
    internal fun queryRelease_IfDoesNotExist_Returns404() {
        val releaseId = UUID.randomUUID().toString()

        given().get("/release/$releaseId").then()
            .statusCode(404)
    }
}