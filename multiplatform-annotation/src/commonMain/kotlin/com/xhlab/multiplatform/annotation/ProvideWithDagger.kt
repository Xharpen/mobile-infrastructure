package com.xhlab.multiplatform.annotation

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ProvideWithDagger(val moduleName: String)
