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

    @Test
    internal fun queryRelease_IfCreatedBefore_Returns200() {
        val toggleName = UUID.randomUUID().toString()

        val response = given()
            .body("""{"name": "$toggleName"}""")
            .post("/release")
            .andReturn()
            .body()

        val responseJson = response.print()
        val toggleId = response.jsonPath().getString("id")

        given().get("/release/$toggleId")
            .then()
            .statusCode(200)
            .body(equalTo(responseJson))
    }
}