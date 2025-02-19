package de.jensklingenberg.ktorfit.node

import de.jensklingenberg.ktorfit.*
import de.jensklingenberg.ktorfit.model.MyParam
import de.jensklingenberg.ktorfit.model.annotations.*


/**
 * Source for the "headers" argument of [de.jensklingenberg.ktorfit.RequestData]
 */
class HeadersArgumentNode(
    private val functionAnnotations: List<FunctionAnnotation>,
    private val paramList: List<MyParam>
) : MyNode() {

    override fun toString(): String {
        val headerList = mutableListOf<Pair<String, String>>()

        val paramsWithHeaderAnno = paramList.filter { it.hasAnnotation<Header>() }
        val headersAnno = functionAnnotations.filterIsInstance<Headers>().firstOrNull()
        val paramsWithHeaderMap = paramList.filter { it.hasAnnotation<HeaderMap>() }

        if (functionAnnotations.any { it is FormUrlEncoded }) {
            headerList.add(Pair("\"Content-Type\"", "\"application/x-www-form-urlencoded\""))
        }

        paramsWithHeaderAnno.forEach { myParam ->
            val paramName = myParam.name
            val headerPath = myParam.findAnnotationOrNull<Header>()?.path ?: ""

            headerList.add(Pair("\"${headerPath}\"", paramName))
        }

        headersAnno?.let { headers ->
            headers.path.forEach {
                val (key, value) = it.split(":")

                headerList.add(Pair("\"" + key + "\"", "\"" + value + "\""))
            }
        }

        paramsWithHeaderMap.forEach {
            headerList.add(Pair("\"\"", it.name))
        }


        val headerText = headerList.joinToString {
            val (key, value) = it

            "HeaderData($key,$value)"
        }.surroundIfNotEmpty("listOf(", ")")


        return headerText.prefixIfNotEmpty("headers = ")
    }
}