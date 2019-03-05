package io.feaggle.server.domain.releases

import io.feaggle.server.infrastructure.journal.registerConsumer
import io.vlingo.lattice.model.DomainEvent
import io.vlingo.lattice.model.sourcing.EventSourced
import java.time.LocalDateTime

class ReleaseActor(
    private val name: String,
    private var enabled: Boolean,
    private var lastChange: LocalDateTime
): EventSourced(), Release {
    data class ReleaseEnabled(val release: String, val happened: LocalDateTime): DomainEvent(1)
    data class ReleaseDisabled(val release: String, val happened: LocalDateTime): DomainEvent(1)

    override fun streamName() = "release_$name"

    // Commands
    override fun enable() {
        if (!enabled) {
            apply(ReleaseEnabled(name, LocalDateTime.now()))
        }
    }

    override fun disable() {
        if (enabled) {
            apply(ReleaseDisabled(name, LocalDateTime.now()))
        }
    }

    // Events
    fun whenEnabled(releaseEnabled: ReleaseEnabled) {
        enabled = true
        lastChange = releaseEnabled.happened
    }

    fun whenDisabled(releaseDisabled: ReleaseDisabled) {
        enabled = false
        lastChange = releaseDisabled.happened
    }
}

fun registerReleaseActorConsumers() {
    registerConsumer(ReleaseActor::whenEnabled)
    registerConsumer(ReleaseActor::whenDisabled)
}