package io.feaggle.server.resources.domain.project

import io.feaggle.server.infrastructure.journal.register
import io.feaggle.server.infrastructure.journal.withConsumer
import io.vlingo.common.Completes
import io.vlingo.lattice.model.DomainEvent
import io.vlingo.lattice.model.sourcing.EventSourced
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry
import io.vlingo.symbio.store.journal.Journal
import java.time.LocalDateTime

data class ProjectId(val boundary: String, val name: String)
data class ProjectOwner(val name: String, val email: String)
data class ProjectInformation(val description: String, val owners: List<ProjectOwner>)

class ProjectActor(
    private val id: ProjectId,
    private var information: ProjectInformation
): EventSourced(), Project {
    constructor(id: ProjectId): this(id, ProjectInformation("", emptyList()))

    override fun streamName() = "/resource/${id.boundary}/project/${id.name}"

    // Commands
    override fun build(declaration: Project.ProjectDeclaration): Completes<Project.ProjectBuildResult> {
        apply(eventsIfDescriptionChange(declaration.description) + eventsIfOwnersChange(declaration.owners))
        return completes<Project.ProjectBuildResult>().with(Project.ProjectBuildSuccess)
    }

    private fun eventsIfDescriptionChange(description: String): List<DomainEvent> {
        return if (description == information.description) {
            emptyList()
        } else {
            listOf(Project.ProjectDescriptionChanged(description, LocalDateTime.now()))
        }
    }

    private fun eventsIfOwnersChange(owners: List<Project.ProjectOwner>): List<DomainEvent> {
        val ownersToAdd = owners.filter { owner -> !information.owners.any { owner.name == it.name && owner.email == it.email } }
        val ownersToRemove = information.owners.filter { owner -> !owners.any { owner.name == it.name && owner.email == it.email } }

        return ownersToAdd.map { Project.ProjectOwnerAdded(it, LocalDateTime.now()) } +
                ownersToRemove.map { Project.ProjectOwnerRemoved(Project.ProjectOwner(it.name, it.email), LocalDateTime.now()) }
    }

    // Events
    fun whenDescriptionChanged(event: Project.ProjectDescriptionChanged) {
        this.information = information.copy(description = event.newDescription)
    }

    fun whenOwnerHasBeenAdded(event: Project.ProjectOwnerAdded) {
        this.information = information.copy(owners = information.owners + ProjectOwner(event.projectOwner.name, event.projectOwner.email))
    }

    fun whenOwnerHasBeenRemoved(event: Project.ProjectOwnerRemoved) {
        this.information = information.copy(owners = information.owners - ProjectOwner(event.projectOwner.name, event.projectOwner.email))
    }
}

fun bootstrapResourceProjectActorConsumers(registry: SourcedTypeRegistry, journal: Journal<String>) {
    registry.register<ProjectActor>(journal)
        .withConsumer(ProjectActor::whenDescriptionChanged)
        .withConsumer(ProjectActor::whenOwnerHasBeenAdded)
        .withConsumer(ProjectActor::whenOwnerHasBeenRemoved)

}