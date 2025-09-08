dependencies {
    val kotlinModuleVersion: String by rootProject
    implementation(project(":day-on-core"))
    implementation(project(":day-on-provider"))
    implementation(project(":day-on-persistence"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$kotlinModuleVersion")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}