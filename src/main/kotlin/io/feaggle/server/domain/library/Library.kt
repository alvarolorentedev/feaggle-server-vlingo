package io.feaggle.server.domain.library

import io.vlingo.actors.Definition
import io.vlingo.actors.World
import io.vlingo.common.Completes
import io.vlingo.lattice.model.DomainEvent
import io.vlingo.symbio.store.journal.Journal
import java.time.LocalDateTime
import java.util.*

interface Library {
    data class SingleRelease(val name: String, val enabled: Boolean, val toEpochSecond: Long)
    data class Releases(val state: MutableMap<UUID, SingleRelease>)
    data class ReleaseInfoChanged(
        val release: UUID,
        val name: String,
        val status: Boolean,
        val happened: LocalDateTime
    ): DomainEvent(1)

    fun state(): Completes<Releases>
}

private const val addressId = "0"

fun World.library(journal: Journal<String>): Completes<Library> {
    return stage().actorOf(Library::class.java, addressFactory().from(addressId))
        .andThen { it ?: instantiate(journal) }
}

private fun World.instantiate(journal: Journal<String>): Library {
    return stage().actorFor(
        Library::class.java,
        Definition.has(
            LibraryActor::class.java,
            Definition.parameters(journal)
        ),
        addressFactory().from(addressId)
    )
}