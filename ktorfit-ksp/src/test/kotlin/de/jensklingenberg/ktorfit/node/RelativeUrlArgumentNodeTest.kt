package de.jensklingenberg.ktorfit.node

import de.jensklingenberg.ktorfit.model.MyFunction
import de.jensklingenberg.ktorfit.model.MyParam
import de.jensklingenberg.ktorfit.model.MyType
import de.jensklingenberg.ktorfit.model.annotations.HttpMethod
import de.jensklingenberg.ktorfit.model.annotations.HttpMethodAnnotation
import de.jensklingenberg.ktorfit.node.RelativeUrlArgumentNode
import org.junit.Assert
import org.junit.Test

class RelativeUrlArgumentNodeTest {

    val testPathParam = MyParam(
        "test",
        MyType("String", "String"),
        annotations = listOf(de.jensklingenberg.ktorfit.model.annotations.Path("postId"))
    )

    @Test
    fun justGET() {

        val myFunction = MyFunction(
            name = "getTest",
            returnType = MyType("Int", "kotlin.Int"),
            isSuspend = true,
            params = emptyList(),
            annotations = emptyList(),
            httpMethodAnnotation = HttpMethodAnnotation("posts", HttpMethod.GET)
        )

        val expected = """relativeUrl="posts""""

        val funcText = RelativeUrlArgumentNode(myFunction).toString()
        Assert.assertEquals(expected, funcText)
    }

    @Test
    fun GETandPath() {

        val myFunction = MyFunction(
            name = "getTest",
            returnType = MyType("Int", "kotlin.Int"),
            isSuspend = true,
            params = listOf(testPathParam),
            annotations = emptyList(),
            httpMethodAnnotation = HttpMethodAnnotation("posts/{postId}", HttpMethod.GET)
        )

        val expected = "relativeUrl=\"posts/$" + "{client.encode(test)}\""

        val funcText = RelativeUrlArgumentNode(myFunction).toString()
        Assert.assertEquals(expected, funcText)
    }

    @Test
    fun GETandPathAndQueryName() {
        val testQueryNameParam = MyParam(
            "queryName",
            MyType("String", "String"),
            annotations = listOf(de.jensklingenberg.ktorfit.model.annotations.QueryName(true))
        )

        val myFunction = MyFunction(
            name = "getTest",
            returnType = MyType("Int", "kotlin.Int"),
            isSuspend = true,
            params = listOf(testPathParam, testQueryNameParam),
            annotations = emptyList(),
            httpMethodAnnotation = HttpMethodAnnotation("posts/{postId}", HttpMethod.GET)
        )

        val expected = "relativeUrl=\"posts/$" + "{client.encode(test)}?$" + "{queryName}\""

        val funcText = RelativeUrlArgumentNode(myFunction).toString()
        Assert.assertEquals(expected, funcText)
    }

}
