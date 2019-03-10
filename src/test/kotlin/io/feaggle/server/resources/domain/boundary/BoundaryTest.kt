package io.feaggle.server.resources.domain.boundary

import io.feaggle.server.base.UnitTest
import io.feaggle.server.resources.domain.project.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class BoundaryTest: UnitTest() {
    private lateinit var boundary: Boundary

    @BeforeEach
    internal fun setUp() {
        boundary = world().actorFor(
            Boundary::class.java, BoundaryActor::class.java, Boundary.BoundaryId(UUID.randomUUID(), "boundary")
        )
    }

    @Test
    internal fun shouldDetectChangesInDescription() {
        waitForEvents(1)

        boundary.build(Boundary.BoundaryDeclaration("boundary", "new-description"))

        assertEquals("new-description", appliedEventAs<Boundary.BoundaryDescriptionChanged>(0).newDescription)
    }
}