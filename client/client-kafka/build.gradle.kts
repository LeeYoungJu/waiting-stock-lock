import com.github.davidmc24.gradle.plugin.avro.GenerateAvroJavaTask
import org.springframework.boot.gradle.tasks.bundling.BootJar

val jar: Jar by tasks
val bootJar: BootJar by tasks

bootJar.enabled = false
jar.enabled = true

plugins {
    id("io.spring.dependency-management") version "1.1.0"

    id("com.github.davidmc24.gradle.plugin.avro") version "1.6.0"
    id("com.github.davidmc24.gradle.plugin.avro-base") version "1.6.0"
    id("net.croz.apicurio-registry-gradle-plugin") version "1.1.0"
}

dependencies {
    implementation(project(":waiting-shared"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.springframework.kafka:spring-kafka")

    // 아래에서 생성되는 avro message java 코드의 의존성을 위해 추가되는 dependency
    implementation("io.apicurio:apicurio-registry-serdes-avro-serde:2.3.1.Final")
}

extra.apply {
    set("eventSchemaPath", file("${buildDir}/schema/event"))
    set("generatedEventCodePath", file("build/event/java"))
}

tasks {
    val eventSchemaPath: File by extra
    val generatedEventCodePath: File by extra

    val schemaRegistryUrl = System.getenv("APICURIO_SCHEMA_REGISTRY_URL")
            ?: "https://apicurio.dev.wadcorp.in/"

    // schema registry에서 avsc 파일을 다운받는 코드
    schemaRegistry {
        config {
            url(schemaRegistryUrl)
        }

        download {
            artifacts {
                artifact {
                    id = "b2b-waiting-waiting-value"
                    outputPath = eventSchemaPath.path
                }
                artifact {
                    id = "b2b-waiting-waiting-v2-value"
                    outputPath = eventSchemaPath.path
                }
                artifact {
                    id = "b2b-waiting-shopOperation-value"
                    outputPath = eventSchemaPath.path
                }
                artifact {
                    id = "b2b-waiting-shopOperation-v2-value"
                    outputPath = eventSchemaPath.path
                }
                artifact {
                    id = "b2b-waiting-table-value"
                    outputPath = eventSchemaPath.path
                }
                artifact {
                    id = "b2b-waiting-tableCurrentStatus-value"
                    outputPath = eventSchemaPath.path
                }
                artifact {
                    id = "b2b-waiting-shopSetting-value"
                    outputPath = eventSchemaPath.path
                }
                artifact {
                    id = "b2b-waiting-menuStock-value"
                    outputPath = eventSchemaPath.path
                }
            }
        }
    }

    // 아래는 avsc 파일로부터 java class 파일을 생성하는 코드
    java.sourceSets["main"].java {
        srcDirs(generatedEventCodePath)
    }

    // https://github.com/davidmc24/gradle-avro-plugin
    // java class 생성에 있어서 상세 설정은 위 링크 참고
    avro {
        setCreateSetters(false)
        setCreateOptionalGetters(true)
        setFieldVisibility("PRIVATE")
        setOutputCharacterEncoding("UTF-8")
        setStringType("String")
    }

    task("generateAvroJavaCode", GenerateAvroJavaTask::class) {
        dependsOn("schemaRegistryDownload")
        source(eventSchemaPath)
        setOutputDir(generatedEventCodePath)
    }

    compileJava {
        dependsOn("generateAvroJavaCode")
    }

}

