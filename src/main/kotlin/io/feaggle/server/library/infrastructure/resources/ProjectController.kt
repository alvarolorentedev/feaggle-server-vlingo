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
package io.feaggle.server.library.infrastructure.resources

import io.feaggle.server.library.domain.project.project
import io.feaggle.server.library.infrastructure.http.answerJson
import io.vlingo.actors.World
import io.vlingo.common.Completes
import io.vlingo.http.Response
import io.vlingo.http.resource.ResourceBuilder.*
import io.vlingo.symbio.store.journal.Journal

class ProjectController(private val world: World, private val journal: Journal<String>) {
    private data class UpdateToggleCommand(val active: Boolean)
    private val logger = world.defaultLogger()

    fun asResource(poolSize: Int) = resource(
        "project", poolSize,
        get("/{projectName}/toggles")
            .param(String::class.java)
            .handle(this::allToggles).onError(this::onError),
        put("/{projectName}/toggles/{releaseId}")
            .param(String::class.java)
            .param(String::class.java)
            .body(UpdateToggleCommand::class.java)
            .handle(this::updateRelease).onError(this::onError))

    private fun allToggles(projectId: String): Completes<Response> {
        logger.log("Received HTTP request GET /toggles")

        return world.project(projectId, journal)
            .andThenTo { it.toggles() }
            .andThen {
                answerJson(Response.Status.Ok, it)
            }
    }

    private fun updateRelease(projectId: String, releaseId: String, command: UpdateToggleCommand): Completes<Response> {
        return Completes.withSuccess(answerJson(Response.Status.Ok))
    }

    private fun onError(t: Throwable): Completes<Response> {
        logger.log("Unhandled error", t)
        return Completes.withSuccess(answerJson(Response.Status.InternalServerError))
    }
}