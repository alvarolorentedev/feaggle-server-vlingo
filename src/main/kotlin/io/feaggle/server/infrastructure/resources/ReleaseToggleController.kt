package io.feaggle.server.infrastructure.resources

import io.vlingo.common.Completes
import io.vlingo.common.serialization.JsonSerialization.serialized
import io.vlingo.http.Response
import io.vlingo.http.ResponseHeader
import io.vlingo.http.resource.ResourceBuilder.get
import io.vlingo.http.resource.ResourceBuilder.resource

class ReleaseToggleController(private val poolSize: Int) {
    fun asResource() = resource("releases", poolSize,
        get("/release/{id}").param(String::class.java).handle(this::getRelease)
    )

    private fun getRelease(id: String) =
        Completes.withSuccess(
            Response.of(Response.Status.Ok, serialized(mapOf("id" to id.trim())))
                .include(ResponseHeader.contentType("application/json"))
        )
}