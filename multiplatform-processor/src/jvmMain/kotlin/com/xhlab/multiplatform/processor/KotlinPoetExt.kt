package com.xhlab.multiplatform.processor

import com.squareup.javapoet.ArrayTypeName
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ARRAY
import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.BYTE
import com.squareup.kotlinpoet.CHAR
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.Dynamic
import com.squareup.kotlinpoet.FLOAT
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.SHORT
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.WildcardTypeName

fun ClassName.asJPClassName(shouldBox: Boolean = false): com.squareup.javapoet.TypeName {
    return when (copy(nullable = false)) {
        BOOLEAN -> com.squareup.javapoet.TypeName.BOOLEAN.boxIfPrimitive(shouldBox || isNullable)
        BYTE -> com.squareup.javapoet.TypeName.BYTE.boxIfPrimitive(shouldBox || isNullable)
        CHAR -> com.squareup.javapoet.TypeName.CHAR.boxIfPrimitive(shouldBox || isNullable)
        SHORT -> com.squareup.javapoet.TypeName.SHORT.boxIfPrimitive(shouldBox || isNullable)
        INT -> com.squareup.javapoet.TypeName.INT.boxIfPrimitive(shouldBox || isNullable)
        LONG -> com.squareup.javapoet.TypeName.LONG.boxIfPrimitive(shouldBox || isNullable)
        FLOAT -> com.squareup.javapoet.TypeName.FLOAT.boxIfPrimitive(shouldBox || isNullable)
        DOUBLE -> com.squareup.javapoet.TypeName.DOUBLE.boxIfPrimitive(shouldBox || isNullable)
        ANY -> com.squareup.javapoet.TypeName.OBJECT
        PoetInterop.CN_KOTLIN_STRING -> PoetInterop.CN_JAVA_STRING
        PoetInterop.CN_KOTLIN_LIST -> PoetInterop.CN_JAVA_LIST
        PoetInterop.CN_KOTLIN_SET -> PoetInterop.CN_JAVA_SET
        PoetInterop.CN_KOTLIN_MAP -> PoetInterop.CN_JAVA_MAP
        else -> {
            if (simpleNames.size == 1) {
                com.squareup.javapoet.ClassName.get(packageName, simpleName)
            } else {
                com.squareup.javapoet.ClassName.get(packageName, simpleNames.first(), *simpleNames.drop(1).toTypedArray())
            }
        }
    }
}

fun ParameterizedTypeName.asJPParameterizedOrArrayTypeName(): com.squareup.javapoet.TypeName {
    return when (rawType) {
        ARRAY -> {
            val componentType = typeArguments.firstOrNull()?.asJP()
                ?: throw IllegalStateException("Array with no type! $this")
            ArrayTypeName.of(componentType)
        }
        else -> {
            com.squareup.javapoet.ParameterizedTypeName.get(rawType.asJPClassName() as com.squareup.javapoet.ClassName,
                *typeArguments.map { it.asJP(shouldBox = true) }.toTypedArray())
        }
    }
}

fun ParameterizedTypeName.asJPParameterizedTypeName(): com.squareup.javapoet.ParameterizedTypeName {
    check(rawType != ARRAY) {
        "Array type! JavaPoet arrays are a custom TypeName. Use this function only for things you know are not arrays"
    }
    return asJPParameterizedOrArrayTypeName() as com.squareup.javapoet.ParameterizedTypeName
}

fun TypeVariableName.asJPTypeVariableName(): com.squareup.javapoet.TypeVariableName {
    return com.squareup.javapoet.TypeVariableName.get(name, *bounds.map { it.asJP(shouldBox = true) }.toTypedArray())
}

fun TypeName.asJP(shouldBox: Boolean = false): com.squareup.javapoet.TypeName {
    return when (this) {
        is ClassName -> asJPClassName(shouldBox)
        Dynamic -> throw IllegalStateException("Not applicable in Java!")
        is LambdaTypeName -> throw IllegalStateException("Not applicable in Java!")
        is ParameterizedTypeName -> asJPParameterizedOrArrayTypeName()
        is TypeVariableName -> asJPTypeVariableName()
        is WildcardTypeName -> TODO()
    }
}
