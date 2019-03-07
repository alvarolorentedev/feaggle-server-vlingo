package io.feaggle.server.domain.releases

import io.feaggle.server.infrastructure.world.addressOf
import io.vlingo.actors.Definition
import io.vlingo.actors.World
import io.vlingo.common.Completes
import io.vlingo.lattice.model.DomainEvent
import java.time.LocalDateTime
import java.util.*

interface Release {
    data class ReleaseNamed(val id: UUID, val name: String, val happened: LocalDateTime) : DomainEvent(1)
    data class ReleaseEnabled(val id: UUID, val happened: LocalDateTime) : DomainEvent(1)
    data class ReleaseDisabled(val id: UUID, val happened: LocalDateTime) : DomainEvent(1)

    data class State(val id: String, val name: String, val status: String, val lastChange: Long)

    fun name(name: String)
    fun enable()
    fun disable()

    fun state(): Completes<Optional<State>>
}

fun World.releaseFor(name: String): Release {
    val id = UUID.randomUUID()
    val release = instantiateById(id)

    release.name(name)
    return release
}

fun World.releaseOf(id: UUID): Completes<Release> =
    stage().actorOf(Release::class.java, addressOf(id))
        .andThen { it ?: instantiateById(id) }

private fun World.instantiateById(
    id: UUID
): Release {
    return stage().actorFor(
        Release::class.java,
        Definition.has(
            ReleaseActor::class.java,
            Definition.parameters(id)
        ),
        addressOf(id)
    )
}