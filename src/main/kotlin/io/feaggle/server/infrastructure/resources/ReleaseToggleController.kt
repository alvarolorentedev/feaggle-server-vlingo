package io.feaggle.server.infrastructure.resources

import io.feaggle.server.domain.library.library
import io.feaggle.server.domain.releases.releaseFor
import io.feaggle.server.domain.releases.releaseOf
import io.feaggle.server.infrastructure.http.answerJson
import io.vlingo.actors.World
import io.vlingo.common.Completes
import io.vlingo.common.serialization.JsonSerialization.serialized
import io.vlingo.http.Response
import io.vlingo.http.resource.ResourceBuilder.*
import io.vlingo.symbio.store.journal.Journal
import java.util.*

class ReleaseToggleController(private val world: World, private val journal: Journal<String>, private val poolSize: Int) {
    data class CreateReleaseCommand(val name: String)
    data class ChangeReleaseStatusCommand(val enabled: Boolean)

    fun asResource() = resource(
        "releases", poolSize,
        get("/release/{id}")
            .param(String::class.java)
            .handle(this::getRelease).onError(this::onError),
        put("/release/{id}")
            .param(String::class.java)
            .body(ChangeReleaseStatusCommand::class.java)
            .handle(this::changeReleaseStatus).onError(this::onError),
        get("/releases")
            .handle(this::allReleases).onError(this::onError),
        post("/release")
            .body(CreateReleaseCommand::class.java)
            .handle(this::createRelease).onError(this::onError)
    )

    private fun getRelease(id: String) =
        world.releaseOf(UUID.fromString(id))
            .andThenTo { it.state() }
            .andThen {
                it.map {
                    answerJson(Response.Status.Ok, it)
                }.orElseGet {
                    answerJson(Response.Status.NotFound)
                }
            }

    private fun changeReleaseStatus(id: String, command: ChangeReleaseStatusCommand): Completes<Response> {
        return world.releaseOf(UUID.fromString(id))
            .andThenConsume { release ->
                when (command.enabled) {
                    true -> release.enable()
                    false -> release.disable()
                }
            }.andThenTo {
                it.state()
            }
            .andThen { state -> answerJson(Response.Status.Ok, state.get()) }
    }

    private fun allReleases() =
        world.library(journal)
            .andThenTo { it.state() }
            .andThen {
                answerJson(Response.Status.Ok, it)
            }

    private fun createRelease(command: CreateReleaseCommand): Completes<Response> {
        return world.releaseFor(command.name)
            .state()
            .andThen { state -> answerJson(Response.Status.Created, state.get()) }
    }

    private fun onError(t: Throwable): Completes<Response> {
        world.logger("release").log("Unhandled error", t)
        return Completes.withSuccess(answerJson(Response.Status.InternalServerError))
    }
}