val springCloudVersion: String by rootProject

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}


dependencies {
    val openFeignVersion: String by rootProject

    implementation(project(":day-on-core"))
    implementation(project(":day-on-infrastructure"))

    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:$openFeignVersion")
}