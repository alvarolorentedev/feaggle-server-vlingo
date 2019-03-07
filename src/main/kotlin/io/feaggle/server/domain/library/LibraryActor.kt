package io.feaggle.server.domain.library

import com.google.gson.Gson
import io.feaggle.server.domain.releases.Release
import io.feaggle.server.infrastructure.journal.register
import io.feaggle.server.infrastructure.journal.withConsumer
import io.vlingo.common.Completes
import io.vlingo.common.Scheduled
import io.vlingo.lattice.model.sourcing.EventSourced
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry
import io.vlingo.symbio.store.journal.Journal
import io.vlingo.symbio.store.journal.JournalReader
import java.util.*

class LibraryActor(journal: Journal<String>) : EventSourced(), Library, Scheduled<Any> {
    private val gson = Gson()
    private val releases = Library.Releases(emptyMap<UUID, Library.SingleRelease>().toMutableMap())
    private val reader: JournalReader<String> = journal.journalReader("library").await()

    init {
        scheduler().schedule(selfAs(Scheduled::class.java) as Scheduled<Any>, null, 0, 5)
    }

    override fun streamName() = "library"

    // Queries
    override fun state(): Completes<Library.Releases> {
        return completes<Library.Releases>().with(releases)
    }

    // Events
    fun whenReleaseInfoChanged(info: Library.ReleaseInfoChanged) {
        releases.state[info.release] = Library.SingleRelease(info.name, info.status)
    }

    // Polling
    override fun intervalSignal(scheduled: Scheduled<Any>?, data: Any?) {
        reader.readNext().andThenConsume { event ->
            if (event.type.contains("ReleaseNamed")) {
                val named = gson.fromJson(event.entryData, Release.ReleaseNamed::class.java)
                apply(Library.ReleaseInfoChanged(named.id, named.name, false, named.happened))
            }

            if (event.type.contains("ReleaseEnabled")) {
                val enabled = gson.fromJson(event.entryData, Release.ReleaseEnabled::class.java)
                apply(Library.ReleaseInfoChanged(enabled.id, releases.state[enabled.id]!!.name, true, enabled.happened))
            }

            if (event.type.contains("ReleaseDisabled")) {
                val disabled = gson.fromJson(event.entryData, Release.ReleaseDisabled::class.java)
                apply(
                    Library.ReleaseInfoChanged(
                        disabled.id,
                        releases.state[disabled.id]!!.name,
                        false,
                        disabled.happened
                    )
                )
            }
        }
    }
}


fun registerLibraryConsumers(registry: SourcedTypeRegistry, journal: Journal<String>) {
    registry.register<LibraryActor>(journal)
        .withConsumer(LibraryActor::whenReleaseInfoChanged)
}