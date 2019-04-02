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
package io.feaggle.server.resources.domain.declaration

import io.feaggle.server.base.UnitTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DeclarationTest: UnitTest() {
    private lateinit var declaration: Declaration

    @BeforeEach
    internal fun setUp() {
        declaration = world().actorFor(
            Declaration::class.java, DeclarationActor::class.java, Declaration.DeclarationId("declaration")
        )
    }

    @Test
    internal fun shouldDetectDroppedResources() {
        declaration = world().actorFor(
            Declaration::class.java, DeclarationActor::class.java, Declaration.DeclarationId("declaration"), setOf("my-boundary")
        )

        waitForEvents(1)

        declaration.build(
            """
                declaration:
                    version: 0.0.1
                    resources:
            """.trimIndent()
        )

        val events = appliedEvents()
        assertTrue(events.any { it.type.endsWith("DeclarationResourceDropped") })
    }

    @Test
    internal fun shouldDetectNewProjects() {
        waitForEvents(3)

        declaration.build(
            """
                declaration:
                    version: 0.0.1
                    resources:
                        my-project:
                            is-a: project
                            description: My first project!
                            owners:
                                - name: John
                                  email: john-email@feaggle.com
                                - name: Bob
                                  email: bob-email@feaggle.com
            """.trimIndent()
        )

        val events = appliedEvents()
        assertTrue(events.any { it.type.endsWith("DeclarationResourceFound") })
        assertTrue(events.any { it.type.endsWith("ProjectDescriptionChanged") })
        assertTrue(events.any { it.type.endsWith("ProjectOwnerAdded") })
    }

    @Test
    internal fun shouldDetectNewReleases() {
        waitForEvents(7)

        declaration.build(
            """
                declaration:
                    version: 0.0.1
                    resources:
                        my-project:
                            is-a: project
                            description: My first project!
                            owners:
                                - name: John
                                  email: john-email@feaggle.com
                                - name: Bob
                                  email: bob-email@feaggle.com
                        my-release:
                            is-a: release
                            in-project: my-project
                            description: My first release!
                            enabled: true
            """.trimIndent()
        )

        val events = appliedEvents()
        assertTrue(events.any { it.type.endsWith("DeclarationResourceFound") })
        assertTrue(events.any { it.type.endsWith("ReleaseDescriptionChanged") })
        assertTrue(events.any { it.type.endsWith("ReleaseStatusChanged") })
    }
}