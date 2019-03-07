package io.feaggle.server.domain.releases

import io.feaggle.server.infrastructure.journal.register
import io.feaggle.server.infrastructure.journal.withConsumer
import io.vlingo.common.Completes
import io.vlingo.lattice.model.DomainEvent
import io.vlingo.lattice.model.sourcing.EventSourced
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry
import io.vlingo.symbio.store.journal.Journal
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class ReleaseActor(
    private val id: UUID,
    private var name: String,
    private var enabled: Boolean,
    private var lastChange: LocalDateTime
) : EventSourced(), Release {
    data class ReleaseNamed(val id: UUID, val name: String, val happened: LocalDateTime) : DomainEvent(1)
    data class ReleaseEnabled(val id: UUID, val happened: LocalDateTime) : DomainEvent(1)
    data class ReleaseDisabled(val id: UUID, val happened: LocalDateTime) : DomainEvent(1)

    constructor(id: UUID) : this(id, "", false, LocalDateTime.now())

    override fun streamName() = "release_$id"

    override fun name(name: String) {
        apply(ReleaseNamed(id, name, LocalDateTime.now()))
    }

    override fun enable() {
        if (!enabled) {
            apply(ReleaseEnabled(id, LocalDateTime.now()))
        }
    }

    override fun disable() {
        if (enabled) {
            apply(ReleaseDisabled(id, LocalDateTime.now()))
        }
    }

    override fun state(): Completes<Optional<Release.State>> {
        if (name == "") {
            return completes<Optional<Release.State>>().with(Optional.empty())
        }

        return completes<Release.State>()
            .with(
                Optional.of(
                    Release.State(
                        id.toString(), name, if (enabled) "enabled" else "disabled", lastChange.toEpochSecond(
                            ZoneOffset.UTC
                        )
                    )
                )
            )
    }

    // Events
    fun whenNamed(releaseNamed: ReleaseNamed) {
        name = releaseNamed.name
        lastChange = releaseNamed.happened
    }

    fun whenEnabled(releaseEnabled: ReleaseEnabled) {
        enabled = true
        lastChange = releaseEnabled.happened
    }

    fun whenDisabled(releaseDisabled: ReleaseDisabled) {
        enabled = false
        lastChange = releaseDisabled.happened
    }
}

fun registerReleaseActorConsumers(registry: SourcedTypeRegistry, journal: Journal<String>) {
    registry.register<ReleaseActor>(journal)
        .withConsumer(ReleaseActor::whenNamed)
        .withConsumer(ReleaseActor::whenEnabled)
        .withConsumer(ReleaseActor::whenDisabled)
}