package io.feaggle.server.specs.toggles

import io.feaggle.server.specs.Specification
import io.restassured.RestAssured.`when`
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import java.util.*

class ReleaseToggleTest: Specification() {
    @Test
    internal fun queryRelease_Returns200() {
        val releaseId = UUID.randomUUID().toString()

        `when`().get("$baseUrl/release/$releaseId")
            .then()
            .statusCode(200)
            .body("id", equalTo(releaseId))
    }
}