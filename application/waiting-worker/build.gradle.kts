dependencies {
    implementation(project(":waiting-data"))
    implementation(project(":waiting-shared"))
    implementation(project(":waiting-event-handler"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Sentry
    implementation("io.sentry:sentry-spring-boot-starter-jakarta:${project.extra.get("sentryVersion")}")
    implementation("io.sentry:sentry-logback:${project.extra.get("sentryVersion")}")

    testImplementation("org.awaitility:awaitility:4.2.0")
}
