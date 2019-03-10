package io.feaggle.server.resources.domain.release

import io.feaggle.server.infrastructure.journal.register
import io.feaggle.server.infrastructure.journal.withConsumer
import io.vlingo.lattice.model.sourcing.EventSourced
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry
import io.vlingo.symbio.store.journal.Journal
import java.time.LocalDateTime

data class ReleaseId(val boundary: String, val project: String, val name: String)
data class ReleaseInformation(val description: String)

class ReleaseActor(
    val id: ReleaseId,
    var information: ReleaseInformation
): EventSourced(), Release {
    constructor(id: ReleaseId): this(id, ReleaseInformation(""))

    override fun streamName() = "/resource/${id.boundary}/project/${id.project}/release/${id.name}"

    // Command
    override fun build(releaseDeclaration: Release.ReleaseDeclaration) {
        if (information.description != releaseDeclaration.description) {
            apply(Release.ReleaseDescriptionChanged(releaseDeclaration.description, LocalDateTime.now()))
        }
    }

    // Events
    fun whenDescriptionChanged(event: Release.ReleaseDescriptionChanged) {
        information = information.copy(description = event.newDescription)
    }
}

fun bootstrapReleaseActorConsumers(registry: SourcedTypeRegistry, journal: Journal<String>) {
    registry.register<ReleaseActor>(journal)
        .withConsumer(ReleaseActor::whenDescriptionChanged)
}