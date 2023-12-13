package com.github.peco2282.excel.annotations

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class Attribute(val value: String, val ns: String = "", val nullable: Boolean = true)
