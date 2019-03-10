package io.feaggle.server.resources.domain.declaration

import io.vlingo.lattice.model.DomainEvent
import java.time.LocalDateTime

interface Declaration {
    data class DeclarationId(val name: String)

    data class DeclarationResourceFound(val id: DeclarationId, val resource: String, val happened: LocalDateTime): DomainEvent(1)
    data class DeclarationResourceDropped(val id: DeclarationId, val resource: String, val happened: LocalDateTime): DomainEvent(1)

    fun build(declaration: String)
}