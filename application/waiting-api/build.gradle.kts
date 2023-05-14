plugins {
    id("org.asciidoctor.jvm.convert") version "3.3.2"
}

dependencies {
    implementation(project(":waiting-data"))
    implementation(project(":waiting-shared"))
    implementation(project(":waiting-event-handler"))
    implementation(project(":client:client-pos"))
    implementation(project(":client:client-message-nhncloud"))

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")

    testImplementation("org.springframework.security:spring-security-test")

    /*implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("commons-codec:commons-codec:1.15")*/

    // jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Model Mapper
    implementation("org.modelmapper:modelmapper:3.1.1")

    // Sentry
    implementation("io.sentry:sentry-spring-boot-starter-jakarta:${project.extra.get("sentryVersion")}")
    implementation("io.sentry:sentry-logback:${project.extra.get("sentryVersion")}")

    // Features
    val commonsLang3Version = dependencyManagement.importedProperties["commons-lang3.version"]
    implementation("org.apache.commons:commons-lang3:${commonsLang3Version}")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("co.wadcorp.libs:lib-utils:${project.extra.get("libUtilsVersion")}")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.1")

    // Rest Docs
    val asciidoctorExt: Configuration by configurations.creating
    asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")

    // junit
    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")

}


tasks {
    val snippetsDir by extra { file("build/generated-snippets") }

    clean {
        delete("src/main/resources/static/docs")
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
        outputs.dir(snippetsDir)
    }

    build {
        dependsOn("copyDocument")
    }

    asciidoctor {
        dependsOn(test)
        attributes(
            mapOf("snippets" to snippetsDir)
        )
        inputs.dir(snippetsDir)

        // static/docs 폴더 비우기
        doFirst {
            delete(file("src/main/resources/static/docs"))
        }
        sources {
            include("**/index.adoc")
        }
        baseDirFollowsSourceFile()
        finalizedBy("copyDocument")
    }

    // asccidoctor 작업 이후 생성된 HTML 파일을 static/docs 로 copy
    register<Copy>("copyDocument") {
        destinationDir = file(".")
        from(asciidoctor.get().outputDir) {
            into("src/main/resources/static/docs")
            duplicatesStrategy = DuplicatesStrategy.INHERIT
        }
    }

    bootJar {
        dependsOn(asciidoctor)

        into("BOOT-INF/classes/static/docs") {
            from("src/main/resources/static/docs")
        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}


