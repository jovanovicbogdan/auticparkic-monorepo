import com.google.cloud.tools.jib.api.buildplan.ImageFormat

plugins {
    java
    application
    `java-library`
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.0"
    id("com.google.cloud.tools.jib") version "3.3.2"
//    id("org.siouan.frontend-jdk17") version "8.0.0"
}

group = "com.jovanovicbogdan"
version = "1.0.0"

jib.to {
    val appImageName: String = findProperty("appImageName") as String? ?: "auticparkic-api"
    val appImageTag: String = findProperty("appImageTag") as String? ?: "latest"
    image = "bogdanjovanovic/${appImageName}:${appImageTag}"
}

jib.from {
    image = "eclipse-temurin:17"

    platforms {
        platform {
            architecture = "arm64"
            os = "linux"
        }
    }
}

jib.container {
    format = ImageFormat.OCI
    ports = listOf("10000")
//    jvmFlags = listOf("-Xms512m", "-Xmx512m")
//    environment = mapOf("SPRING_PROFILES_ACTIVE" to "production")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
    withJavadocJar()
}

application {
    mainClass.set("com.jovanovicbogdan.auticparkic.Application")
}

repositories {
    mavenCentral()
}

dependencies {
    val springBootStarterVersion = "3.1.3"
    val springIntegrationJdbcVersion = "6.1.2"
    val flywayCoreVersion = "9.22.0"
    val javaFakerVersion = "1.0.2"
    val springdocOpenapiVersion = "2.2.0"
    val mockitoCoreVersion = "5.5.0"
    val testcontainersVersion = "1.17.6"

    implementation(platform("software.amazon.awssdk:bom:2.20.128"))
    implementation("software.amazon.awssdk:s3")

    implementation("commons-io:commons-io:2.13.0")

    runtimeOnly("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-starter-web:${springBootStarterVersion}")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc:${springBootStarterVersion}")
    implementation("org.springframework.integration:spring-integration-jdbc:${springIntegrationJdbcVersion}")
    implementation("org.springframework.boot:spring-boot-starter-integration:${springBootStarterVersion}")
    implementation("org.springframework.boot:spring-boot-starter-validation:${springBootStarterVersion}")
    implementation("org.flywaydb:flyway-core:${flywayCoreVersion}")
    implementation("com.github.javafaker:javafaker:${javaFakerVersion}")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${springdocOpenapiVersion}")

    testImplementation("org.springframework.boot:spring-boot-starter-test:${springBootStarterVersion}")
    testImplementation("org.mockito:mockito-core:${mockitoCoreVersion}")
    testImplementation("org.testcontainers:testcontainers:${testcontainersVersion}")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql:${testcontainersVersion}")
    testImplementation("org.assertj:assertj-core")
}

tasks.withType<Test> {
    useJUnitPlatform() {
        val includeTags = System.getProperty("includeTags")
        val excludeTags = System.getProperty("excludeTags")

        includeTags?.split(',')?.let {
            includeTags(*it.toTypedArray())
        }

        excludeTags?.split(',')?.let {
            excludeTags(*it.toTypedArray())
        }
    }
}

tasks.jar {
    manifest {
        attributes(mapOf("Implementation-Title" to project.name,
                "Implementation-Version" to project.version))
    }
}
