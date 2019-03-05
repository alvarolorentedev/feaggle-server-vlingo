package io.feaggle.server

fun main() {
    ApplicationServer(
        System.getenv("FEAGGLE_JDBC_URL"),
        System.getenv("FEAGGLE_JDBC_USER"),
        System.getenv("FEAGGLE_JDBC_PASSWORD"),
        System.getenv("FEAGGLE_PORT").toInt()
    ).start()
}