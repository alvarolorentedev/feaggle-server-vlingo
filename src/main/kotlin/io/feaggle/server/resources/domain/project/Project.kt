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
package io.feaggle.server.resources.domain.project

import io.vlingo.actors.Stage
import io.vlingo.common.Completes
import io.vlingo.lattice.model.DomainEvent
import java.time.LocalDateTime

interface Project {
    data class ProjectId(val declaration: String, val name: String)
    data class ProjectOwner(val name: String, val email: String)
    data class ProjectInformation(val description: String, val owners: List<ProjectOwner>)

    data class ProjectOwnerDeclaration(
        val name: String,
        val email: String
    )

    data class ProjectDeclaration(
        val declaration: String,
        val name: String,
        val description: String,
        val owners: List<ProjectOwnerDeclaration>
    )

    data class ProjectDescriptionChanged(val id: ProjectId, val newDescription: String, val happened: LocalDateTime): DomainEvent(1)
    data class ProjectOwnerRemoved(val id: ProjectId, val declaration: ProjectOwnerDeclaration, val happened: LocalDateTime): DomainEvent(1)
    data class ProjectOwnerAdded(val id: ProjectId, val declaration: ProjectOwnerDeclaration, val happened: LocalDateTime): DomainEvent(1)

    fun build(declaration: ProjectDeclaration)
}

object Projects {
    fun oneOf(stage: Stage, id: Project.ProjectId): Completes<Project> {
        val address = stage.world().addressFactory().from(id.hashCode().toString())
        val actor = stage.maybeActorOf(Project::class.java, address)
            .andThen { it.orElse(stage.actorFor(Project::class.java, ProjectActor::class.java, id)) }

        return actor
    }
}