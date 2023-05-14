import org.gradle.api.tasks.bundling.Jar
import org.springframework.boot.gradle.tasks.bundling.BootJar

val jar: Jar by tasks
val bootJar: BootJar by tasks

bootJar.enabled = false
jar.enabled = true

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")

    val commonsLang3Version = dependencyManagement.importedProperties["commons-lang3.version"]
    implementation("org.apache.commons:commons-lang3:${commonsLang3Version}")
    implementation("org.apache.commons:commons-configuration2:2.8.0")

    // Guava
    implementation("com.google.guava:guava:11.0.2")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}