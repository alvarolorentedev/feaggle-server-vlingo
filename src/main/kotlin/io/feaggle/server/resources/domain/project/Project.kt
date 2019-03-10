package io.feaggle.server.resources.domain.project

import io.vlingo.lattice.model.DomainEvent
import java.time.LocalDateTime

interface Project {
    data class ProjectOwner(
        val name: String,
        val email: String
    )

    data class ProjectDeclaration(
        val boundary: String,
        val name: String,
        val description: String,
        val owners: List<ProjectOwner>
    )

    data class ProjectDescriptionChanged(val newDescription: String, val happened: LocalDateTime): DomainEvent(1)
    data class ProjectOwnerRemoved(val projectOwner: ProjectOwner, val happened: LocalDateTime): DomainEvent(1)
    data class ProjectOwnerAdded(val projectOwner: ProjectOwner, val happened: LocalDateTime): DomainEvent(1)

    fun build(declaration: ProjectDeclaration)
}