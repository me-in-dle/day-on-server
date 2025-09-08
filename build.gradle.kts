import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled

plugins {
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management")
    id("org.jlleitschuh.gradle.ktlint")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
    kotlin("plugin.allopen")
    kotlin("kapt")
}

allprojects {
    apply(plugin = "java")

    group = project.findProperty("projectGroup") as String
    version = project.findProperty("applicationVersion") as String

    extensions.configure<JavaPluginExtension>("java") {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(20))
        }
    }

    repositories {
        mavenCentral()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(20)
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "20"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    if (hasProperty("buildScan")) {
        extensions.findByName("buildScan")?.withGroovyBuilder {
            setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
            setProperty("termsOfServiceAgree", "yes")
        }
    }
}

subprojects {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-spring")
    apply(plugin = "kotlin-noarg")
    apply(plugin = "kotlin-allopen")

    dependencies {
        val kotestVersion: String by rootProject
        val kotestSpringVersion: String by rootProject
        val kotlinLoggingVersion: String by rootProject
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")
        testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
        testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
        testImplementation("io.kotest:kotest-property:$kotestVersion")
        testImplementation("io.kotest:kotest-extensions-spring:$kotestSpringVersion")
    }

    tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
        enabled = false
    }
    tasks.withType<Jar> {
        enabled = true
    }

    tasks.register("prepareKotlinBuildScriptModel") {}

    if (name == "api") {
        tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
            enabled = true
        }
    }

    if (!name.contains("core")) {
        val springBootVersion: String by rootProject
        val coroutinesVersion: String by rootProject

        dependencies {
            implementation("org.springframework.boot:spring-boot-starter")
            testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$coroutinesVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$coroutinesVersion")

        }
    }

    if (name.contains("persistence") || name.contains("application")) {
        apply(plugin = "kotlin-kapt")

        val kaptVersion: String by rootProject
        val mysqlConnectorVersion: String by rootProject
        val queryDslVersion: String by rootProject

        dependencies {
            implementation("org.springframework.boot:spring-boot-starter-data-jpa")
            kapt("com.querydsl:querydsl-apt:$queryDslVersion:jakarta")
            runtimeOnly("com.mysql:mysql-connector-j")

            implementation("com.querydsl:querydsl-jpa:$queryDslVersion:jakarta")
        }

        noArg {
            invokeInitializers = true
            annotation("javax.persistence.Entity")
            annotation("javax.persistence.MappedSuperclass")
            annotation("javax.persistence.Embeddable")
            annotation("jakarta.persistence.Entity")
            annotation("jakarta.persistence.MappedSuperclass")
            annotation("jakarta.persistence.Embeddable")
        }
    }
}