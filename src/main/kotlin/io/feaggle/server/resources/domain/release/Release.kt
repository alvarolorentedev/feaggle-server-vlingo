package io.feaggle.server.resources.domain.release

import io.vlingo.lattice.model.DomainEvent
import java.time.LocalDateTime

interface Release {
    data class ReleaseId(val declaration: String, val boundary: String, val project: String, val name: String)
    data class ReleaseInformation(val description: String)

    data class ReleaseDeclaration(val declaration: String, val boundary: String, val project: String, val name: String, val description: String)
    data class ReleaseDescriptionChanged(val id: ReleaseId, val newDescription: String, val happened: LocalDateTime): DomainEvent(1)

    fun build(releaseDeclaration: ReleaseDeclaration)
}