dependencies {
    val kotlinModuleVersion: String by rootProject
    implementation(project(":day-on-core"))
    implementation(project(":day-on-provider"))
    implementation(project(":day-on-application"))
    implementation(project(":day-on-persistence"))
    implementation(project(":day-on-infrastructure"))
    implementation(project(":day-on-provider:day-on-provider-jwt"))
    implementation(project(":day-on-provider:day-on-provider-google"))
    implementation(project(":day-on-provider:day-on-provider-claude"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$kotlinModuleVersion")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.springframework.boot:spring-boot-starter-websocket")

    implementation("org.springframework.boot:spring-boot-starter-security")

    val openFeignVersion: String by rootProject
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:$openFeignVersion")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}
