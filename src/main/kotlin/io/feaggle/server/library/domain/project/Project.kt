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
package io.feaggle.server.library.domain.project

import io.vlingo.actors.Definition
import io.vlingo.actors.World
import io.vlingo.common.Completes
import io.vlingo.lattice.model.DomainEvent
import io.vlingo.symbio.store.journal.Journal
import java.time.LocalDateTime

interface Project {
    data class Release(val name: String, val description: String, val active: Boolean, val lastChange: Long)
    data class ReleaseInfoChanged(
        val release: String,
        val description: String?,
        val status: Boolean?,
        val happened: LocalDateTime
    ): DomainEvent(1)

    data class Toggles(val releases: List<Release>)
    fun toggles(): Completes<Toggles>
}

fun World.project(projectId: String, journal: Journal<String>): Completes<Project> {
    return stage().actorOf(Project::class.java, addressFactory().from(projectId.hashCode().toString()))
        .andThen { it ?: instantiate(projectId, journal) }
}

private fun World.instantiate(projectId: String, journal: Journal<String>): Project {
    return stage().actorFor(
        Project::class.java,
        Definition.has(
            ProjectActor::class.java,
            Definition.parameters(projectId, journal)
        ),
        addressFactory().from(projectId.hashCode().toString())
    )
}