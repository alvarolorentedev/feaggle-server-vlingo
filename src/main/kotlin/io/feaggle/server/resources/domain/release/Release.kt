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
package io.feaggle.server.resources.domain.release

import io.vlingo.actors.Stage
import io.vlingo.common.Completes
import io.vlingo.lattice.model.DomainEvent
import java.time.LocalDateTime

interface Release {
    data class ReleaseId(val declaration: String, val project: String, val name: String) {
        fun toPublicIdentifier() = name
    }

    data class ReleaseInformation(val description: String)
    data class ReleaseStatus(val enabled: Boolean)

    data class ReleaseDeclaration(val declaration: String, val project: String, val name: String, val description: String, val enabled: Boolean)
    data class ReleaseDescriptionChanged(val id: ReleaseId, val newDescription: String, val happened: LocalDateTime): DomainEvent(1)
    data class ReleaseStatusChanged(val id: ReleaseId, val newStatus: Boolean, val happened: LocalDateTime): DomainEvent(1)

    fun build(releaseDeclaration: ReleaseDeclaration)
}

object Releases {
    fun oneOf(stage: Stage, id: Release.ReleaseId): Completes<Release> {
        val address = stage.world().addressFactory().from(id.hashCode().toString())
        val actor = stage.maybeActorOf(Release::class.java, address)
            .andThen { it.orElse(stage.actorFor(Release::class.java, ReleaseActor::class.java, id)) }

        return actor
    }
}