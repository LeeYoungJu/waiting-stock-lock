import org.gradle.api.tasks.bundling.Jar
import org.springframework.boot.gradle.tasks.bundling.BootJar

val jar: Jar by tasks
val bootJar: BootJar by tasks

bootJar.enabled = false
jar.enabled = true

dependencies {
    implementation(project(":waiting-shared"))

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.redisson:redisson-spring-boot-starter:3.21.1")


    // Features
    val commonsLang3Version = dependencyManagement.importedProperties["commons-lang3.version"]
    implementation("org.apache.commons:commons-lang3:${commonsLang3Version}")
    implementation("co.wadcorp.libs:lib-nhn-cloud:${project.extra.get("libNhnCloudVersion")}")

    // flyway
    val flywayVersion = dependencyManagement.importedProperties["flyway.version"]
    implementation("org.flywaydb:flyway-core:${flywayVersion}")
    // FlywayException: Unsupported Database: MySQL 8.0 에 대한 대응
    implementation("org.flywaydb:flyway-mysql:${flywayVersion}")

    implementation("org.springframework:spring-web")

    // jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // QueryDSL & JPA
    val queryDslVersion = dependencyManagement.importedProperties["querydsl.version"]
    api("com.querydsl:querydsl-jpa:${queryDslVersion}:jakarta")
    // querydsl JPAAnnotationProcessor 사용 지정
    annotationProcessor("com.querydsl:querydsl-apt:${queryDslVersion}:jakarta")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")

}
