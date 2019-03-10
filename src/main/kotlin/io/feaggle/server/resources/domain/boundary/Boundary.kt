package io.feaggle.server.resources.domain.boundary

import io.vlingo.lattice.model.DomainEvent
import java.time.LocalDateTime

interface Boundary {
    data class BoundaryDescriptionChanged(val newDescription: String, val happened: LocalDateTime): DomainEvent(1)
    data class BoundaryDeclaration(val name: String, val description: String)

    fun build(boundaryDeclaration: BoundaryDeclaration)
}