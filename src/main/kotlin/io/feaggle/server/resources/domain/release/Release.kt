package io.feaggle.server.resources.domain.release

import io.vlingo.lattice.model.DomainEvent
import java.time.LocalDateTime

interface Release {
    data class ReleaseDeclaration(val boundary: String, val project: String, val name: String, val description: String)
    data class ReleaseDescriptionChanged(val newDescription: String, val happened: LocalDateTime): DomainEvent(1)

    fun build(releaseDeclaration: ReleaseDeclaration)
}