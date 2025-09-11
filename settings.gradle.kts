rootProject.name = "day-on-server"

pluginManagement {
    val springBootVersion: String by settings
    val springDependencyManagementVersion: String by settings
    val kotlinVersion: String by settings
    val kotlinAllOpenVersion: String by settings
    val kotlinNoArgVersion: String by settings
    val kaptVersion: String by settings
    val klintVersion: String by settings

    plugins {
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyManagementVersion
        id("org.jlleitschuh.gradle.ktlint") version klintVersion
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        kotlin("plugin.noarg") version kotlinNoArgVersion
        kotlin("plugin.allopen") version kotlinAllOpenVersion
        kotlin("kapt") version kaptVersion
    }
}

include("day-on-api")
include("day-on-core")
include("day-on-provider")
include("day-on-provider:day-on-provider-jwt")
include("day-on-provider:day-on-provider-google")
include("day-on-provider:day-on-provider-claude")
include("day-on-persistence")
include("day-on-application")
include("day-on-infrastructure")