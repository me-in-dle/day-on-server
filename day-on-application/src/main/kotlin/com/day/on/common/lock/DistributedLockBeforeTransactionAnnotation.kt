package com.day.on.common.lock

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributedLockBeforeTransactionAnnotation(
    val key: Array<String>,
    val prefix: DistributedLockPrefix,
    val separator: String = ":",
    val transactionalReadOnly: Boolean = false,
)