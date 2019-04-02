package io.feaggle.server.specs.toggles

import io.feaggle.server.specs.Specification
import io.restassured.RestAssured.given
import org.hamcrest.Matchers.equalTo
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