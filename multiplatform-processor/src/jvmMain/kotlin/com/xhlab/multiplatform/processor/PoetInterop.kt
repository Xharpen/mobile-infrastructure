package com.xhlab.multiplatform.processor

/** Various JavaPoet and KotlinPoet representations of some common types. */
internal object PoetInterop {
    internal val CN_KOTLIN_STRING = com.squareup.kotlinpoet.ClassName("kotlin", "String")
    internal val CN_KOTLIN_LIST = com.squareup.kotlinpoet.ClassName("kotlin", "List")
    internal val CN_KOTLIN_SET = com.squareup.kotlinpoet.ClassName("kotlin", "Set")
    internal val CN_KOTLIN_MAP = com.squareup.kotlinpoet.ClassName("kotlin", "Map")
    internal val CN_JAVA_STRING = com.squareup.javapoet.ClassName.get("java.lang", "String")
    internal val CN_JAVA_LIST = com.squareup.javapoet.ClassName.get("java.util", "List")
    internal val CN_JAVA_SET = com.squareup.javapoet.ClassName.get("java.util", "Set")
    internal val CN_JAVA_MAP = com.squareup.javapoet.ClassName.get("java.util", "Map")
}