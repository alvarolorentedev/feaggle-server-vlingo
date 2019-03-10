package io.feaggle.server.resources.domain.boundary

import io.vlingo.lattice.model.DomainEvent
import java.time.LocalDateTime

interface Boundary {
    data class BoundaryId(val declaration: String, val name: String)
    data class BoundaryInformation(val description: String)

    data class BoundaryDescriptionChanged(val boundary: BoundaryId, val newDescription: String, val happened: LocalDateTime): DomainEvent(1)
    data class BoundaryDeclaration(val declaration: String, val name: String, val description: String)

    fun build(boundaryDeclaration: BoundaryDeclaration)
}