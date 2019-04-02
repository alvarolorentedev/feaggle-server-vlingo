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

import io.feaggle.server.base.Specification
import io.restassured.RestAssured.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.Matchers.lessThan
import org.junit.jupiter.api.Test

class ReleaseQueryTest: Specification() {
    @Test
    internal fun queryAllReleases_findReleaseName() {
        declare("release-query-declaration")

        eventually {
            given()
                .get("/my-project/toggles")
                .then()
                .time(lessThan(5000L))
                .statusCode(200)
                .body("releases[0].name", equalTo("my-release"))
        }
    }
}