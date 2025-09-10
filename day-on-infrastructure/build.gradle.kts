dependencies {
    implementation(project(":day-on-core"))

    implementation("org.springframework:spring-messaging")
    // redis (분산 세션 및 pub + sub)
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

}