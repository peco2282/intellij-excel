package com.github.peco2282.excel.annotations

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class Element(val value: String, val collection: Boolean = false)
