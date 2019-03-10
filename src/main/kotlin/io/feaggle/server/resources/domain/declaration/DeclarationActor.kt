package io.feaggle.server.resources.domain.declaration

import io.vlingo.lattice.model.sourcing.EventSourced

class DeclarationActor(
    val id: Declaration.DeclarationId
): EventSourced(), Declaration {
    override fun streamName() = "/declaration/${id.name}"

    override fun build(declaration: String) {

    }
}