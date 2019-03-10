package io.feaggle.server.resources.domain.project

import io.feaggle.server.base.UnitTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProjectTest : UnitTest() {
    private lateinit var project: Project

    @BeforeEach
    internal fun setUp() {
        project = world().actorFor(Project::class.java, ProjectActor::class.java, ProjectId("boundary", "project"),
            ProjectInformation("..", listOf(
                ProjectOwner("A", "A@a.com")
            )))
    }

    @Test
    internal fun shouldDetectChangesInTheDescription() {
        waitForEvents(1)
        project.build(Project.ProjectDeclaration("boundary", "project", "new-description", emptyList()))

        assertEquals("new-description", appliedEventAs<Project.ProjectDescriptionChanged>(0).newDescription)
    }

    @Test
    internal fun shouldDetectChangesInTheOwners() {
        waitForEvents(2)
        project.build(Project.ProjectDeclaration("boundary", "project", "..",
            listOf(
                Project.ProjectOwner("B", "B@b.com")
            )
        ))

        assertEquals("B", appliedEventAs<Project.ProjectOwnerAdded>(0).projectOwner.name)
        assertEquals("A", appliedEventAs<Project.ProjectOwnerRemoved>(1).projectOwner.name)
    }
}