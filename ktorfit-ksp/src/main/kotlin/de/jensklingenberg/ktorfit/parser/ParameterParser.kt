package de.jensklingenberg.ktorfit.parser

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSValueParameter
import de.jensklingenberg.ktorfit.*
import de.jensklingenberg.ktorfit.model.MyParam
import de.jensklingenberg.ktorfit.model.MyType
import de.jensklingenberg.ktorfit.model.annotations.ParamAnnotation


fun getMyParamList(ksValueParameter: KSValueParameter, logger: KSPLogger): MyParam {
    if (ksValueParameter.isVararg) {
        logger.ktorfitError("vararg not supported use List or Array", ksValueParameter)
    }
    if (ksValueParameter.type.resolve().isMarkedNullable) {
        logger.ktorfitError("Nullable Parameters are not supported", ksValueParameter)
    }

    val pararameterAnnotations = getParamAnnotationList(ksValueParameter, logger)

    val reqBuilderAnno = ksValueParameter.getRequestBuilderAnnotation()
    val parameterName = ksValueParameter.name?.asString() ?: ""
    val parameterType = ksValueParameter.type.resolve()
    val hasRequestBuilderAnno = reqBuilderAnno != null

    if (pararameterAnnotations.isEmpty() && reqBuilderAnno == null) {
        logger.ktorfitError(
            "No Ktorfit Annotation found at " + parameterName + " " + ksValueParameter.parent.toString(),
            ksValueParameter
        )
    }

    if (hasRequestBuilderAnno && parameterType.resolveTypeName() != "[@kotlin.ExtensionFunctionType] Function1<HttpRequestBuilder, Unit>") {
        logger.ktorfitError(
            "@ReqBuilder parameter type needs to be HttpRequestBuilder.()->Unit",
            ksValueParameter
        )
    }

    val type = if (hasRequestBuilderAnno) {
        MyType(
            "HttpRequestBuilder.()->Unit",
            "HttpRequestBuilder.()->Unit"
        )
    } else {
        MyType(
            parameterType.resolveTypeName(),
            parameterType.declaration.qualifiedName?.asString() ?: ""
        )
    }

    return MyParam(parameterName, type, pararameterAnnotations, hasRequestBuilderAnno)

}


fun getParamAnnotationList(ksValueParameter: KSValueParameter, logger: KSPLogger): List<ParamAnnotation> {

    val pararamAnnos = mutableListOf<ParamAnnotation>()
    ksValueParameter.getBodyAnnotation()?.let {
        pararamAnnos.add(it)
    }

    ksValueParameter.getPathAnnotation()?.let {
        if (ksValueParameter.type.resolve().isMarkedNullable) {
            logger.ktorfitError("Path parameter type may not be nullable", ksValueParameter.type)
        }
        pararamAnnos.add(it)
    }

    ksValueParameter.getHeadersAnnotation()?.let {
        pararamAnnos.add(it)
    }

    ksValueParameter.getHeaderMapAnnotation()?.let {
        //TODO: Find out how isAssignableFrom works
        if (!ksValueParameter.type.toString().endsWith("Map")) {
            logger.ktorfitError("@HeaderMap parameter type must be Map.", ksValueParameter)
        }
        val mapKey = ksValueParameter.type.resolve().arguments.first()
        if (mapKey.type.toString() != "String" || mapKey.type?.resolve()?.isMarkedNullable == true) {
            logger.error("@HeaderMap keys must be of type String:", ksValueParameter)
        }
        pararamAnnos.add(it)
    }

    ksValueParameter.getQueryAnnotation()?.let {
        pararamAnnos.add(it)
    }

    ksValueParameter.getQueryNameAnnotation()?.let {
        pararamAnnos.add(it)
    }

    ksValueParameter.getQueryMapAnnotation()?.let {
        if (!ksValueParameter.type.toString().endsWith("Map")) {
            logger.error("@QueryMap parameter type must be Map.", ksValueParameter)
        }

        val mapKey = ksValueParameter.type.resolve().arguments.first()
        if (mapKey.type.toString() != "String" || mapKey.type?.resolve()?.isMarkedNullable == true) {
            logger.error("@QueryMap keys must be of type String:", ksValueParameter)
        }
        if (ksValueParameter.type.resolve().isMarkedNullable) {
            logger.ktorfitError("QueryMap parameter type may not be nullable", ksValueParameter.type)
        }
        pararamAnnos.add(it)
    }

    ksValueParameter.getFieldAnnotation()?.let {
        pararamAnnos.add(it)
    }

    ksValueParameter.getFieldMapAnnotation()?.let {
        if (!ksValueParameter.type.toString().endsWith("Map")) {
            logger.ktorfitError("@FieldMap parameter type must be Map.", ksValueParameter)
        }

        val mapKey = ksValueParameter.type.resolve().arguments.first()
        if (mapKey.type.toString() != "String" || mapKey.type?.resolve()?.isMarkedNullable == true) {
            logger.error("@FieldMap keys must be of type String:", ksValueParameter)
        }
        pararamAnnos.add(it)
    }

    ksValueParameter.getPartAnnotation()?.let {
        if (ksValueParameter.type.resolve().isMarkedNullable) {
            logger.ktorfitError("Part parameter type may not be nullable", ksValueParameter.type)
        }
        pararamAnnos.add(it)
    }

    ksValueParameter.getPartMapAnnotation()?.let {
        if (!ksValueParameter.type.toString().endsWith("Map")) {
            logger.ktorfitError("@PartMap parameter type must be Map.", ksValueParameter)
        }
        pararamAnnos.add(it)
    }

    ksValueParameter.getUrlAnnotation()?.let {
        pararamAnnos.add(it)
    }
    return pararamAnnos
}




