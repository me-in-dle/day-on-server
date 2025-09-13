val springCloudVersion: String by rootProject

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}


dependencies {
    val openFeignVersion: String by rootProject
    val googleAuthVersion: String by rootProject
    val retrofit2Version: String by rootProject
    val jacksonModuleKotlinVersion: String by rootProject

    implementation(project(":day-on-core"))

    implementation("com.squareup.retrofit2:retrofit:$retrofit2Version")
    implementation("com.squareup.retrofit2:converter-gson:$retrofit2Version")
    implementation("com.squareup.retrofit2:adapter-java8:$retrofit2Version")

    implementation("com.google.api-client:google-api-client:$googleAuthVersion")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:$openFeignVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonModuleKotlinVersion")
}