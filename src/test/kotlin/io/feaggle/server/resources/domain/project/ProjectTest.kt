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
package io.feaggle.server.resources.domain.project

import io.feaggle.server.base.UnitTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProjectTest : UnitTest() {
    private lateinit var project: Project

    @BeforeEach
    internal fun setUp() {
        project = world().actorFor(Project::class.java, ProjectActor::class.java,
            Project.ProjectId("declaration", "project"),
            Project.ProjectInformation(
                "..", listOf(
                    Project.ProjectOwner("A", "A@a.com")
                )
            )
        )
    }

    @Test
    internal fun shouldDetectChangesInTheDescription() {
        waitForEvents(1)
        project.build(Project.ProjectDeclaration("declaration", "project", "new-description", emptyList()))

        assertEquals("new-description", appliedEventAs<Project.ProjectDescriptionChanged>(0).newDescription)
    }

    @Test
    internal fun shouldDetectChangesInTheOwners() {
        waitForEvents(2)
        project.build(Project.ProjectDeclaration("declaration", "project", "..",
            listOf(
                Project.ProjectOwnerDeclaration("B", "B@b.com")
            )
        ))

        assertEquals("B", appliedEventAs<Project.ProjectOwnerAdded>(0).declaration.name)
        assertEquals("A", appliedEventAs<Project.ProjectOwnerRemoved>(1).declaration.name)
    }
}