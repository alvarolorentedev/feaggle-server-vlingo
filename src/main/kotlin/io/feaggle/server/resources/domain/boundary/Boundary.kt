package io.feaggle.server.resources.domain.boundary

import io.vlingo.actors.Stage
import io.vlingo.common.Completes
import io.vlingo.lattice.model.DomainEvent
import java.time.LocalDateTime

interface Boundary {
    data class BoundaryId(val declaration: String, val name: String)
    data class BoundaryInformation(val description: String)

    data class BoundaryDescriptionChanged(val boundary: BoundaryId, val newDescription: String, val happened: LocalDateTime): DomainEvent(1)
    data class BoundaryDeclaration(val declaration: String, val name: String, val description: String)

    fun build(boundaryDeclaration: BoundaryDeclaration)
}

object Boundaries {
    fun oneOf(stage: Stage, id: Boundary.BoundaryId): Completes<Boundary> {
        val address = stage.world().addressFactory().from(id.hashCode().toString())
        val actor = stage.maybeActorOf(Boundary::class.java, address)
            .andThen { it.orElse(stage.actorFor(Boundary::class.java, BoundaryActor::class.java, id)) }

        return actor
    }
}