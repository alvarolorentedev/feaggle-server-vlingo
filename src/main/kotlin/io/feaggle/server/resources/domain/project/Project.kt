package io.feaggle.server.resources.domain.project

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