package de.jensklingenberg.ktorfit.http

/**
 *  Add headers to a request
 *
 *  @Headers(value = ["Accept: application/json","Content-Type: application/json"])
 *  @GET("comments")
 *  suspend fun requestWithHeaders(): List<Comment>
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Headers(val value: Array<String>)