package io.feaggle.server.library.domain.project

import io.vlingo.actors.Definition
import io.vlingo.actors.World
import io.vlingo.common.Completes
import io.vlingo.lattice.model.DomainEvent
import io.vlingo.symbio.store.journal.Journal
import java.time.LocalDateTime

interface Project {
    data class Release(val name: String, val description: String, val enabled: Boolean, val lastChange: Long)
    data class ReleaseInfoChanged(
        val release: String,
        val description: String?,
        val status: Boolean?,
        val happened: LocalDateTime
    ): DomainEvent(1)

    data class Toggles(val releases: List<Release>)
    fun toggles(): Completes<Toggles>
}

private const val addressId = "0"

fun World.project(journal: Journal<String>): Completes<Project> {
    return stage().actorOf(Project::class.java, addressFactory().from(addressId))
        .andThen { it ?: instantiate(journal) }
}

private fun World.instantiate(journal: Journal<String>): Project {
    return stage().actorFor(
        Project::class.java,
        Definition.has(
            ProjectActor::class.java,
            Definition.parameters(journal)
        ),
        addressFactory().from(addressId)
    )
}