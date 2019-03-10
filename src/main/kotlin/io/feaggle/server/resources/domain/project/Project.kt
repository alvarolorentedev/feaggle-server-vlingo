package io.feaggle.server.resources.domain.project

import io.feaggle.server.resources.domain.declaration.Declaration
import io.feaggle.server.resources.domain.declaration.DeclarationActor
import io.vlingo.actors.Stage
import io.vlingo.common.Completes
import io.vlingo.lattice.model.DomainEvent
import java.time.LocalDateTime

interface Project {
    data class ProjectId(val declaration: String, val boundary: String, val name: String)
    data class ProjectOwner(val name: String, val email: String)
    data class ProjectInformation(val description: String, val owners: List<ProjectOwner>)

    data class ProjectOwnerDeclaration(
        val name: String,
        val email: String
    )

    data class ProjectDeclaration(
        val declaration: String,
        val boundary: String,
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