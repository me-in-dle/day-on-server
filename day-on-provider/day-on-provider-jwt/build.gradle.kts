dependencies {
    val jwtVersion: String by rootProject

    implementation(project(":day-on-core"))
    implementation(project(":day-on-infrastructure"))

    implementation("io.jsonwebtoken:jjwt-api:$jwtVersion")
    implementation("io.jsonwebtoken:jjwt-impl:$jwtVersion")
    implementation("io.jsonwebtoken:jjwt-jackson:$jwtVersion")
}