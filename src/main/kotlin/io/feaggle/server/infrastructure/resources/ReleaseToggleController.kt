package io.feaggle.server.infrastructure.resources

import io.feaggle.server.domain.library.library
import io.feaggle.server.infrastructure.http.answerJson
import io.vlingo.actors.World
import io.vlingo.common.Completes
import io.vlingo.http.Response
import io.vlingo.http.resource.ResourceBuilder.get
import io.vlingo.http.resource.ResourceBuilder.resource
import io.vlingo.symbio.store.journal.Journal

class ReleaseToggleController(private val world: World, private val journal: Journal<String>) {
    private val logger = world.defaultLogger()

    fun asResource(poolSize: Int) = resource(
        "releases", poolSize,
        get("/releases")
            .handle(this::allReleases).onError(this::onError))
    private fun allReleases(): Completes<Response> {
        logger.log("Received HTTP request GET /releases")

        return world.library(journal)
            .andThenTo { it.releases() }
            .andThen {
                answerJson(Response.Status.Ok, it)
            }
    }

    private fun onError(t: Throwable): Completes<Response> {
        logger.log("Unhandled error", t)
        return Completes.withSuccess(answerJson(Response.Status.InternalServerError))
    }
}