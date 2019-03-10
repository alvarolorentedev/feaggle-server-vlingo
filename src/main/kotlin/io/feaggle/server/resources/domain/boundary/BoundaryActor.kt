package io.feaggle.server.resources.domain.boundary

import io.feaggle.server.infrastructure.journal.register
import io.feaggle.server.infrastructure.journal.withConsumer
import io.vlingo.lattice.model.sourcing.EventSourced
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry
import io.vlingo.symbio.store.journal.Journal
import java.time.LocalDateTime
import java.util.*

data class BoundaryId(val id: UUID, val name: String)
data class BoundaryInformation(val description: String)

class BoundaryActor(
    val id: BoundaryId,
    var information: BoundaryInformation
): EventSourced(), Boundary {
    constructor(id: BoundaryId): this(id, BoundaryInformation(""))

    override fun streamName() = "/resource/${id.name}"

    // Commands
    override fun build(boundaryDeclaration: Boundary.BoundaryDeclaration) {
        if (information.description != boundaryDeclaration.description) {
            apply(Boundary.BoundaryDescriptionChanged(boundaryDeclaration.description, LocalDateTime.now()))
        }
    }

    // Events
    fun whenDescriptionChanged(event: Boundary.BoundaryDescriptionChanged) {
        information = information.copy(description = event.newDescription)
    }
}

fun bootstrapBoundaryActorConsumers(registry: SourcedTypeRegistry, journal: Journal<String>) {
    registry.register<BoundaryActor>(journal)
        .withConsumer(BoundaryActor::whenDescriptionChanged)
}