package io.feaggle.server.library.domain.project

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

class ProjectActor(journal: Journal<String>) : EventSourced(), Project, Scheduled<Any> {
    private val gson = Gson()
    private val releases = emptyMap<String, Project.Release>().toMutableMap()
    private val reader: JournalReader<String> = journal.journalReader("project").await()

    init {
        scheduler().schedule(selfAs(Scheduled::class.java) as Scheduled<Any>, null, 0, pollingInterval)
    }

    override fun streamName() = "project"

    // Queries
    override fun toggles(): Completes<Project.Toggles> {
        logger().log("Project received query toggles()")

        return completes<Project.Toggles>().with(Project.Toggles(releases.values.toList()))
    }

    // Events
    fun whenReleaseInfoChanged(info: Project.ReleaseInfoChanged) {
        logger().log("Project received event $info")

        val currentRelease = releases.getOrDefault(info.release, Project.Release(info.release, "", false, 0L))
        releases[info.release] =
            Project.Release(info.release, info.description ?: currentRelease.description, info.status ?: currentRelease.enabled, info.happened.toEpochSecond(UTC))
    }

    // Polling
    override fun intervalSignal(scheduled: Scheduled<Any>?, data: Any?) {
        reader.readNext(maximumEntries).andThenConsume { stream ->
            stream.forEach { event ->
                logger().log("Project polled event ${event.type}\n\twith data ${event.entryData}")

                if (event.type.endsWith("ReleaseDescriptionChanged")) {
                    val changed = gson.fromJson(event.entryData, Release.ReleaseDescriptionChanged::class.java)
                    apply(Project.ReleaseInfoChanged(changed.id.toPublicIdentifier(), changed.newDescription, null, changed.happened))
                }

                if (event.type.endsWith("ReleaseStatusChanged")) {
                    val changed = gson.fromJson(event.entryData, Release.ReleaseStatusChanged::class.java)
                    apply(
                        Project.ReleaseInfoChanged(
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
    registry.register<ProjectActor>(journal)
        .withConsumer(ProjectActor::whenReleaseInfoChanged)
}