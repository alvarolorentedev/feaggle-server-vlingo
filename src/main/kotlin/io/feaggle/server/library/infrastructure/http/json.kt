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