package com.day.on.common.lock

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributedLockAnnotation(
    val key: Array<String>,
    val prefix: DistributedLockPrefix,
    val separator: String = ":",
)
