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
//
//    @Test
//    internal fun shouldDetectNewBoundaries() {
//        waitForEvents(2)
//
//        declaration.build(
//            """
//                declaration:
//                    my-boundary:
//                        is-a: Boundary
//                        description: My first boundary!
//            """.trimIndent()
//        )
//
//        val events = appliedEvents()
//        assertTrue(events.any { it.type.endsWith("BoundaryDescriptionChanged") })
//        assertTrue(events.any { it.type.endsWith("DeclarationResourceFound") })
//    }
}