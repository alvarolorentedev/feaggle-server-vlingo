package io.feaggle.server.resources.domain.release

import io.feaggle.server.resources.domain.project.Project
import io.feaggle.server.resources.domain.project.ProjectActor
import io.vlingo.actors.Stage
import io.vlingo.common.Completes
import io.vlingo.lattice.model.DomainEvent
import java.time.LocalDateTime

interface Release {
    data class ReleaseId(val declaration: String, val boundary: String, val project: String, val name: String)
    data class ReleaseInformation(val description: String)
    data class ReleaseStatus(val enabled: Boolean)

    data class ReleaseDeclaration(val declaration: String, val boundary: String, val project: String, val name: String, val description: String, val enabled: Boolean)
    data class ReleaseDescriptionChanged(val id: ReleaseId, val newDescription: String, val happened: LocalDateTime): DomainEvent(1)
    data class ReleaseStatusChanged(val id: ReleaseId, val newStatus: Boolean, val happened: LocalDateTime): DomainEvent(1)

    fun build(releaseDeclaration: ReleaseDeclaration)
}

object Releases {
    fun oneOf(stage: Stage, id: Release.ReleaseId): Completes<Release> {
        val address = stage.world().addressFactory().from(id.hashCode().toString())
        val actor = stage.maybeActorOf(Release::class.java, address)
            .andThen { it.orElse(stage.actorFor(Release::class.java, ReleaseActor::class.java, id)) }

        return actor
    }
}