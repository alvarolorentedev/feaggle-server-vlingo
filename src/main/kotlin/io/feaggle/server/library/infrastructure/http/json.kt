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
package io.feaggle.server.library.infrastructure.http

import io.vlingo.http.Response
import io.vlingo.http.ResponseHeader
import io.vlingo.http.resource.serialization.JsonSerialization.serialized

fun answerJson(status: Response.Status, content: Any): Response {
    return Response.of(status, serialized(content))
        .include(ResponseHeader.contentType("application/json"))
}

fun answerJson(status: Response.Status): Response {
    return Response.of(status)
        .include(ResponseHeader.contentType("application/json"))
}