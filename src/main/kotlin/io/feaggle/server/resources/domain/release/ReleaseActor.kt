package io.feaggle.server.resources.domain.release

import io.feaggle.server.infrastructure.journal.register
import io.feaggle.server.infrastructure.journal.withConsumer
import io.vlingo.lattice.model.sourcing.EventSourced
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry
import io.vlingo.symbio.store.journal.Journal
import java.time.LocalDateTime

class ReleaseActor(
    val id: Release.ReleaseId,
    var information: Release.ReleaseInformation,
    var status: Release.ReleaseStatus
): EventSourced(), Release {
    constructor(id: Release.ReleaseId): this(id, Release.ReleaseInformation(""), Release.ReleaseStatus(false))

    override fun streamName() = "/declaration/${id.declaration}/project/${id.project}/${id.name}"

    // Command
    override fun build(releaseDeclaration: Release.ReleaseDeclaration) {
        if (information.description != releaseDeclaration.description) {
            apply(Release.ReleaseDescriptionChanged(id, releaseDeclaration.description, LocalDateTime.now()))
        }

        if (status.enabled != releaseDeclaration.enabled) {
            apply(Release.ReleaseStatusChanged(id, releaseDeclaration.enabled, LocalDateTime.now()))
        }
    }

    // Events
    fun whenDescriptionChanged(event: Release.ReleaseDescriptionChanged) {
        information = information.copy(description = event.newDescription)
    }

    fun whenStatusChanged(event: Release.ReleaseStatusChanged) {
        status = status.copy(enabled = event.newStatus)
    }
}

fun bootstrapReleaseActorConsumers(registry: SourcedTypeRegistry, journal: Journal<String>) {
    registry.register<ReleaseActor>(journal)
        .withConsumer(ReleaseActor::whenDescriptionChanged)
        .withConsumer(ReleaseActor::whenStatusChanged)
}