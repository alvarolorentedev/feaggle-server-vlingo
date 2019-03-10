package io.feaggle.server.resources.domain.declaration

import io.vlingo.actors.Stage
import io.vlingo.common.Completes
import io.vlingo.lattice.model.DomainEvent
import java.time.LocalDateTime

interface Declaration {
    data class DeclarationId(val name: String)

    data class DeclarationResourceFound(val id: DeclarationId, val resource: String, val happened: LocalDateTime): DomainEvent(1)
    data class DeclarationResourceDropped(val id: DeclarationId, val resource: String, val happened: LocalDateTime): DomainEvent(1)

    fun build(declaration: String)
}

object Declarations {
    fun oneOf(stage: Stage, id: Declaration.DeclarationId): Completes<Declaration> {
        val address = stage.world().addressFactory().from(id.hashCode().toString())
        val actor = stage.maybeActorOf(Declaration::class.java, address)
            .andThen { it.orElse(stage.actorFor(Declaration::class.java, DeclarationActor::class.java, id)) }

        return actor
    }
}