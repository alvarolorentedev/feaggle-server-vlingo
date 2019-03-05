package io.feaggle.server.infrastructure.resources

import io.feaggle.server.domain.releases.releaseFor
import io.feaggle.server.domain.releases.releaseOf
import io.vlingo.actors.World
import io.vlingo.common.Completes
import io.vlingo.common.serialization.JsonSerialization.serialized
import io.vlingo.http.Response
import io.vlingo.http.ResponseHeader
import io.vlingo.http.resource.ResourceBuilder.*
import java.util.*

class ReleaseToggleController(private val world: World, private val poolSize: Int) {
    data class CreateReleaseCommand(val name: String)

    fun asResource() = resource("releases", poolSize,
        get("/release/{id}")
            .param(String::class.java)
            .handle(this::getRelease).onError(this::onError),
        post("/release")
            .body(CreateReleaseCommand::class.java)
            .handle(this::createRelease).onError(this::onError)
    )

    private fun getRelease(id: String) =
        world.releaseOf(UUID.fromString(id))
            .andThenTo { it.state() }
            .andThen { state -> Response.of(Response.Status.Ok, serialized(state)) }
            .otherwise { Response.of(Response.Status.NotFound) }

    private fun createRelease(command: CreateReleaseCommand): Completes<Response> {
        return world.releaseFor(command.name)
            .state()
            .andThen { state -> Response.of(Response.Status.Ok, serialized(state)) }
    }

    private fun onError(t: Throwable): Completes<Response> {
        world.logger("release").log("Unhandled error", t)
        return Completes.withSuccess(Response.of(Response.Status.InternalServerError))
    }
}