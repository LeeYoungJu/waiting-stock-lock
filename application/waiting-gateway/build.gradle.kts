dependencies {
    implementation(project(":waiting-shared"))

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")

    val commonsLang3Version = dependencyManagement.importedProperties["commons-lang3.version"]
    implementation("org.apache.commons:commons-lang3:${commonsLang3Version}")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.1")
    implementation("commons-net:commons-net:3.9.0")

    // Sentry
    implementation("io.sentry:sentry-spring-boot-starter-jakarta:${project.extra.get("sentryVersion")}")
    implementation("io.sentry:sentry-logback:${project.extra.get("sentryVersion")}")

    // Spring Boot 에서는 기본적으로 netty-X.X.XX 의존성을 가져오지만,
    // 로컬 개발시에 각 OS에 맞는 NativeDnsResolver(예: netty-resolver-dns-native-macos)가 기본 의존성에 포함되지 않아서
    // 로컬 실행시에 불필요한 에러가 발생하는 경우가 있다.
    // 그래서 netty-all 전체 의존성을 한번 더 명시적으로 선언해준다.
    implementation("io.netty:netty-all")
}

configurations.implementation {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-web")
}
