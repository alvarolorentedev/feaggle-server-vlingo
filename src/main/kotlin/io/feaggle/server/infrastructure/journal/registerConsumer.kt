package io.feaggle.server.infrastructure.journal

import io.vlingo.lattice.model.DomainEvent
import io.vlingo.lattice.model.sourcing.EventSourced


inline fun <reified T: EventSourced, reified E: DomainEvent> registerConsumer(crossinline method: T.(E) -> Unit) {
    EventSourced.registerConsumer(T::class.java, E::class.java) { t, u -> method(t as T, u as E) }
}