package de.jensklingenberg.ktorfit.http


/** Make a PUT request.
 * @param value relative url path, if empty, you need to have a parameter with [Url]
 * */
@Target(AnnotationTarget.FUNCTION)
annotation class PUT(val value: String)