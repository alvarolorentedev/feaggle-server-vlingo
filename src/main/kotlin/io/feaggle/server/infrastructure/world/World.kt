package io.feaggle.server.infrastructure.world

import io.vlingo.actors.Address
import io.vlingo.actors.World
import java.util.*

fun World.addressOf(id: UUID): Address =
    addressFactory().from("" + (id.mostSignificantBits and Long.MAX_VALUE))