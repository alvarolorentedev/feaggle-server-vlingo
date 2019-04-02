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

import io.feaggle.server.library.infrastructure.journal.register
import io.feaggle.server.library.infrastructure.journal.withConsumer
import io.vlingo.lattice.model.DomainEvent
import io.vlingo.lattice.model.sourcing.EventSourced
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry
import io.vlingo.symbio.store.journal.Journal
import java.time.LocalDateTime


class ProjectActor(
    private val id: Project.ProjectId,
    private var information: Project.ProjectInformation
) : EventSourced(), Project {
    constructor(id: Project.ProjectId) : this(id, Project.ProjectInformation("", emptyList()))

    override fun streamName() = "/project/${id.name}"

    // Commands
    override fun build(declaration: Project.ProjectDeclaration) {
        apply(eventsIfDescriptionChange(declaration.description) + eventsIfOwnersChange(declaration.owners))
    }

    private fun eventsIfDescriptionChange(description: String): List<DomainEvent> {
        return if (description == information.description) {
            emptyList()
        } else {
            listOf(Project.ProjectDescriptionChanged(id, description, LocalDateTime.now()))
        }
    }

    private fun eventsIfOwnersChange(ownerDeclarations: List<Project.ProjectOwnerDeclaration>): List<DomainEvent> {
        val ownersToAdd =
            ownerDeclarations.filter { owner -> !information.owners.any { owner.name == it.name && owner.email == it.email } }
        val ownersToRemove =
            information.owners.filter { owner -> !ownerDeclarations.any { owner.name == it.name && owner.email == it.email } }

        return ownersToAdd.map { Project.ProjectOwnerAdded(id, it, LocalDateTime.now()) } +
                ownersToRemove.map {
                    Project.ProjectOwnerRemoved(
                        id,
                        Project.ProjectOwnerDeclaration(it.name, it.email),
                        LocalDateTime.now()
                    )
                }
    }

    // Events
    fun whenDescriptionChanged(event: Project.ProjectDescriptionChanged) {
        this.information = information.copy(description = event.newDescription)
    }

    fun whenOwnerHasBeenAdded(event: Project.ProjectOwnerAdded) {
        this.information = information.copy(
            owners = information.owners + Project.ProjectOwner(
                event.declaration.name,
                event.declaration.email
            )
        )
    }

    fun whenOwnerHasBeenRemoved(event: Project.ProjectOwnerRemoved) {
        this.information = information.copy(
            owners = information.owners - Project.ProjectOwner(
                event.declaration.name,
                event.declaration.email
            )
        )
    }
}

fun bootstrapResourceProjectActorConsumers(registry: SourcedTypeRegistry, journal: Journal<String>) {
    registry.register<ProjectActor>(journal)
        .withConsumer(ProjectActor::whenDescriptionChanged)
        .withConsumer(ProjectActor::whenOwnerHasBeenAdded)
        .withConsumer(ProjectActor::whenOwnerHasBeenRemoved)

}