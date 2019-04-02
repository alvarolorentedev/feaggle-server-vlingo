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

import io.feaggle.server.base.UnitTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ReleaseTest: UnitTest() {
    private lateinit var release: Release

    @BeforeEach
    internal fun setUp() {
        release = world().actorFor(
            Release::class.java, ReleaseActor::class.java, Release.ReleaseId( "project", "my-release")
        )
    }

    @Test
    internal fun shouldDetectChangesInDescription() {
        waitForEvents(1)

        release.build(Release.ReleaseDeclaration("declaration", "project", "my-release", "new-description", false))

        assertEquals(
            "new-description",
            appliedEventAs<Release.ReleaseDescriptionChanged>(0).newDescription
        )
    }

    @Test
    internal fun shouldDetectChangesInTheStatus() {
        waitForEvents(1)

        release.build(Release.ReleaseDeclaration("declaration", "project", "my-release", "", true))

        assertEquals(
            true,
            appliedEventAs<Release.ReleaseStatusChanged>(0).newStatus
        )
    }
}