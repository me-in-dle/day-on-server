package com.day.on.common.logger

import org.slf4j.LoggerFactory

/* utils 모듈로 이동 예정 */
open class Logger {
    val log = LoggerFactory.getLogger(this.javaClass)!!
}