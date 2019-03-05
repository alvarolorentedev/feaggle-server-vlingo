package io.feaggle.server.domain.releases

import io.feaggle.server.infrastructure.world.addressOf
import io.vlingo.actors.Address
import io.vlingo.actors.Definition
import io.vlingo.actors.World
import io.vlingo.common.Completes
import java.time.LocalDateTime
import java.util.*

interface Release {
    data class State(val id: String, val name: String, val status: String, val lastChange: LocalDateTime)

    fun enable()
    fun disable()

    fun state(): Completes<State>
}

fun World.releaseFor(name: String): Release {
    val id = UUID.randomUUID()

    return stage().actorFor(Release::class.java,
        Definition.has(ReleaseActor::class.java,
            Definition.parameters(id, name, false, LocalDateTime.now())),
        addressOf(id))
}

fun World.releaseOf(id: UUID): Completes<Release> =
    stage().actorOf(Release::class.java, addressOf(id))

