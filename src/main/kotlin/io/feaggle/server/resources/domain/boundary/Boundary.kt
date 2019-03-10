package io.feaggle.server.resources.domain.boundary

import io.vlingo.lattice.model.DomainEvent
import java.time.LocalDateTime
import java.util.*

interface Boundary {
    data class BoundaryId(val id: UUID, val name: String)
    data class BoundaryInformation(val description: String)

    data class BoundaryDescriptionChanged(val boundary: BoundaryId, val newDescription: String, val happened: LocalDateTime): DomainEvent(1)
    data class BoundaryDeclaration(val name: String, val description: String)

    fun build(boundaryDeclaration: BoundaryDeclaration)
}