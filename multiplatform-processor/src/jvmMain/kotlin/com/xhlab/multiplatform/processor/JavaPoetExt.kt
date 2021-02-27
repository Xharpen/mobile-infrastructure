package com.xhlab.multiplatform.processor

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeVariableName
import com.squareup.javapoet.WildcardTypeName
import com.squareup.javapoet.ArrayTypeName
import com.squareup.kotlinpoet.ARRAY
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

fun ClassName.asKPClassName(): com.squareup.kotlinpoet.ClassName {
    return if (simpleNames().size == 1) {
        com.squareup.kotlinpoet.ClassName(packageName(), simpleName())
    } else {
        com.squareup.kotlinpoet.ClassName(packageName(), simpleNames().first(), *simpleNames().drop(1).toTypedArray())
    }
}

fun ParameterizedTypeName.asKPParameterizedTypeName(): com.squareup.kotlinpoet.ParameterizedTypeName {
    return rawType.asKPClassName().parameterizedBy(*typeArguments.map { it.asKP() }.toTypedArray())
}

fun TypeVariableName.asKPTypeVariableName(): com.squareup.kotlinpoet.TypeVariableName {
    return if (bounds.isEmpty()) {
        com.squareup.kotlinpoet.TypeVariableName(name)
    } else {
        com.squareup.kotlinpoet.TypeVariableName(name, *bounds.map { it.asKP() }.toTypedArray())
    }
}

fun TypeName.asKP(): com.squareup.kotlinpoet.TypeName {
    return when (this) {
        is ClassName -> when (this) {
            TypeName.BOOLEAN.box() -> com.squareup.kotlinpoet.BOOLEAN
            TypeName.BYTE.box() -> com.squareup.kotlinpoet.BYTE
            TypeName.CHAR.box() -> com.squareup.kotlinpoet.CHAR
            TypeName.SHORT.box() -> com.squareup.kotlinpoet.SHORT
            TypeName.INT.box() -> com.squareup.kotlinpoet.INT
            TypeName.LONG.box() -> com.squareup.kotlinpoet.LONG
            TypeName.FLOAT.box() -> com.squareup.kotlinpoet.FLOAT
            TypeName.DOUBLE.box() -> com.squareup.kotlinpoet.DOUBLE
            TypeName.OBJECT -> com.squareup.kotlinpoet.ANY
            PoetInterop.CN_JAVA_STRING -> PoetInterop.CN_KOTLIN_STRING
            PoetInterop.CN_JAVA_LIST -> PoetInterop.CN_KOTLIN_LIST
            PoetInterop.CN_JAVA_SET -> PoetInterop.CN_KOTLIN_SET
            PoetInterop.CN_JAVA_MAP -> PoetInterop.CN_KOTLIN_MAP
            else -> asKPClassName()
        }
        is ParameterizedTypeName -> asKPParameterizedTypeName()
        is TypeVariableName -> asKPTypeVariableName()
        is WildcardTypeName -> TODO()
        is ArrayTypeName -> ARRAY.parameterizedBy(componentType.asKP())
        else -> when (unboxIfBoxedPrimitive()) {
            TypeName.BOOLEAN -> com.squareup.kotlinpoet.BOOLEAN
            TypeName.BYTE -> com.squareup.kotlinpoet.BYTE
            TypeName.CHAR -> com.squareup.kotlinpoet.CHAR
            TypeName.SHORT -> com.squareup.kotlinpoet.SHORT
            TypeName.INT -> com.squareup.kotlinpoet.INT
            TypeName.LONG -> com.squareup.kotlinpoet.LONG
            TypeName.FLOAT -> com.squareup.kotlinpoet.FLOAT
            TypeName.DOUBLE -> com.squareup.kotlinpoet.DOUBLE
            else -> TODO("Unrecognized type $this")
        }
    }
}

fun TypeName.unboxIfBoxedPrimitive(): TypeName {
    return if (isBoxedPrimitive) {
        unbox()
    } else this
}

fun TypeName.boxIfPrimitive(extraCondition: Boolean = true): TypeName {
    return if (extraCondition && isPrimitive && !isBoxedPrimitive) {
        box()
    } else this
}