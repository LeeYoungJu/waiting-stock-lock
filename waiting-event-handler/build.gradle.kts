import org.gradle.api.tasks.bundling.Jar
import org.springframework.boot.gradle.tasks.bundling.BootJar

val jar: Jar by tasks
val bootJar: BootJar by tasks

bootJar.enabled = false
jar.enabled = true

dependencies {
    implementation(project(":waiting-data"))
    implementation(project(":waiting-shared"))
    implementation(project(":client:client-message-nhncloud"))
    implementation(project(":client:client-kafka"))
}
