/**
 * This file is part of feaggle-server.
 *
 * feaggle-server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * feaggle-server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with feaggle-server.  If not, see <https://www.gnu.org/licenses/>.
 **/
package io.feaggle.server.library.domain.library

import com.google.gson.Gson
import io.feaggle.server.library.infrastructure.journal.register
import io.feaggle.server.library.infrastructure.journal.withConsumer
import io.feaggle.server.resources.domain.release.Release
import io.vlingo.common.Completes
import io.vlingo.common.Scheduled
import io.vlingo.lattice.model.sourcing.EventSourced
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry
import io.vlingo.symbio.store.journal.Journal
import io.vlingo.symbio.store.journal.JournalReader
import java.time.ZoneOffset.UTC

private const val maximumEntries: Int = 100
private const val pollingInterval: Long = 100

class LibraryActor(val projectId: String, journal: Journal<String>) : EventSourced(), Library, Scheduled<Any> {
    private val gson = Gson()
    private val releases = emptyMap<String, Library.Release>().toMutableMap()
    private val reader: JournalReader<String> = journal.journalReader("project-$projectId").await()

    init {
        scheduler().schedule(selfAs(Scheduled::class.java) as Scheduled<Any>, null, 0, pollingInterval)
    }

    override fun streamName() = "project-$projectId"

    // Queries
    override fun toggles(): Completes<Library.Toggles> {
        logger().log("Library received query toggles()")

        return completes<Library.Toggles>().with(Library.Toggles(releases.values.toList()))
    }

    // Events
    fun whenReleaseInfoChanged(info: Library.ReleaseInfoChanged) {
        logger().log("Library received event $info")

        val currentRelease = releases.getOrDefault(info.release, Library.Release(info.release, "", false, 0L))
        releases[info.release] =
            Library.Release(info.release, info.description ?: currentRelease.description, info.status ?: currentRelease.active, info.happened.toEpochSecond(UTC))
    }

    // Polling
    override fun intervalSignal(scheduled: Scheduled<Any>?, data: Any?) {
        reader.readNext(maximumEntries).andThenConsume { stream ->
            stream.forEach { event ->
                logger().log("Library polled event ${event.type}\n\twith data ${event.entryData}")

                if (event.type.endsWith("ReleaseDescriptionChanged")) {
                    val changed = gson.fromJson(event.entryData, Release.ReleaseDescriptionChanged::class.java)
                    apply(Library.ReleaseInfoChanged(changed.id.toPublicIdentifier(), changed.newDescription, null, changed.happened))
                }

                if (event.type.endsWith("ReleaseStatusChanged")) {
                    val changed = gson.fromJson(event.entryData, Release.ReleaseStatusChanged::class.java)
                    apply(
                        Library.ReleaseInfoChanged(
                            changed.id.toPublicIdentifier(),
                            null,
                            changed.newStatus,
                            changed.happened
                        )
                    )
                }
            }
        }
    }
}


fun bootstrapLibraryConsumers(registry: SourcedTypeRegistry, journal: Journal<String>) {
    registry.register<LibraryActor>(journal)
        .withConsumer(LibraryActor::whenReleaseInfoChanged)
}