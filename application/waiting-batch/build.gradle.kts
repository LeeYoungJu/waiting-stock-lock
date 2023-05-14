dependencies {
    implementation(project(":waiting-data"))
    implementation(project(":waiting-shared"))

    implementation(project(":client:client-pos"))
    implementation(project(":client:client-kafka"))

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-batch")
    testImplementation("org.springframework.batch:spring-batch-test")
    runtimeOnly("com.h2database:h2")
}
