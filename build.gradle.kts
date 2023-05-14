import org.springframework.boot.gradle.tasks.bundling.BootJar

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    id("org.springframework.boot") version "3.0.5"
    id("java")
    id("java-library")
    id("io.spring.dependency-management") version "1.1.0"
    id("io.freefair.lombok") version "8.0.1"
    id("jacoco")
}

allprojects {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "io.freefair.lombok")
    apply(plugin = "jacoco")

    group = "co.wadcorp.waiting"
    version = "0.0.1-SNAPSHOT"

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    repositories {
        mavenCentral()
        maven(url = "https://repo.spring.io/release")

        val inhousePackages: Array<String> = arrayOf(
                "catchtable-api-client",
                "lib-utils",
                "lib-vault",
                "lib-nhn-cloud",
                "lib-health"
        )
        inhousePackages.forEach {
            maven {
                url = uri("https://maven.pkg.github.com/CatchTable/$it")
                credentials {
                    username =
                            project.findProperty("ct_username") as String?
                                    ?: System.getenv("GH_ACTOR")
                    password =
                            project.findProperty("ct_token") as String? ?: System.getenv("GH_TOKEN")
                }
            }
        }
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
        developmentOnly
        runtimeClasspath {
            extendsFrom(configurations.developmentOnly.get())
        }
    }

    // jacoco 커버리지 설정
    // TODO 커버리지 최소 조건 추가
    jacoco {
        toolVersion = "0.8.8"
        reportsDirectory.set(layout.buildDirectory.dir("customJacocoReportDir"))
    }

    tasks {
        withType<Test> {
            useJUnitPlatform()

            // 테스트 Timezone 설정
            systemProperty("user.timezone", "Asia/Seoul")

            configure<JacocoTaskExtension> {
                enabled = true
                setDestinationFile(layout.buildDirectory.file("jacoco/jacocoTest.exec").get().asFile)
                classDumpDir = layout.buildDirectory.dir("jacoco/classpathdumps").get().asFile
            }
            finalizedBy(named("jacocoTestReport"))
        }

        named<JacocoReport>("jacocoTestReport") {
            reports {
                // 원하는 리포트를 켜고 끌 수 있다.
                xml.required.set(false)
                csv.required.set(false)

                // 각 리포트 타입 마다 리포트 저장 경로를 설정할 수 있다.
                html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
            }
        }

        named<Jar>("jar") {
            // boot 2.5 이후 빌드시에 plain jar가 만들어지는데 plain jar를 만들지 않기 위한 설정
            enabled = false
        }

        // TODO: waiting-shared에 false가 지정되어 있는데 root 수준에서 왜 enabled를 false로 지정했는지 확인 필요
        named<BootJar>("bootJar") {
            layered {
                enabled.set(true)
            }
        }
    }

    extra.apply {
        set("sentryVersion", "6.11.0")
        set("libNhnCloudVersion", "0.0.3")
        set("libUtilsVersion", "0.0.21")
        set("libVaultVersion", "1.0.0")
    }

    dependencies {
        // Spring Boot features
        implementation("org.springframework.boot:spring-boot-starter")
        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

        // Lombok features
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")

        // DB Features
        // implementation("software.aws.rds:aws-mysql-jdbc:1.1.0")
        // MariaDB JDBC Driver는 3.x 버전부터는 aurora를 지원하지 않는다.
        // aws-mysql-jdbc가 드라이버가 Write/Read Split을 지원할 때까지 2.7.x 버전을 대신 사용한다.
        implementation("org.mariadb.jdbc:mariadb-java-client:2.7.9")

        // WAD libs
        implementation("co.wadcorp.libs:lib-vault:${project.extra.get("libVaultVersion")}")
        implementation("co.wadcorp.libs:lib-utils:${project.extra.get("libUtilsVersion")}")

        // Logging Dependencies
        implementation("ch.qos.logback.contrib:logback-jackson:0.1.5")
        implementation("ch.qos.logback.contrib:logback-json-classic:0.1.5")

        // Test feature
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.mockito:mockito-core:4.11.0")
        testRuntimeOnly("com.h2database:h2")
    }

    dependencyManagement {
//        val nettyVersion = dependencyManagement.importedProperties["netty.version"]
        // "io.netty:netty-all:version" 를 사용하기 위한 설정
//        dependencies {
//            dependency("io.netty:netty-all:${nettyVersion}")
//        }

        // 이 설정은 최상위 build.gralde에 있어야 subproject에 적용된다.
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:2022.0.1")
        }
    }

}
