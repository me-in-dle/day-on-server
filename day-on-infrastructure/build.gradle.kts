dependencies {
    val gsonVersion: String by project

    implementation(project(":day-on-core"))

    implementation("org.springframework:spring-messaging")
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

}