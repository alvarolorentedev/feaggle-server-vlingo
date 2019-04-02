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

import io.feaggle.server.library.infrastructure.world.actor
import io.feaggle.server.library.infrastructure.world.addressOfString
import io.feaggle.server.resources.domain.release.Release
import io.feaggle.server.resources.domain.release.ReleaseActor
import io.vlingo.actors.Definition
import io.vlingo.actors.Stage
import io.vlingo.common.Completes
import io.vlingo.lattice.model.DomainEvent
import io.vlingo.symbio.store.journal.Journal
import java.time.LocalDateTime

interface Library {
    data class Release(val name: String, val description: String, val active: Boolean, val lastChange: Long)
    data class ReleaseInfoChanged(
        val release: String,
        val description: String?,
        val status: Boolean?,
        val happened: LocalDateTime
    ) : DomainEvent(1)

    data class Toggles(val releases: List<Release>)

    fun toggles(): Completes<Toggles>
}

object Libraries {
    fun oneOf(stage: Stage, libraryId: String, journal: Journal<String>): Completes<Library> {
        val address = stage.world().addressOfString(libraryId)
        return stage.world().actor<Library, LibraryActor>(arrayOf(libraryId, journal), address)
    }
}
