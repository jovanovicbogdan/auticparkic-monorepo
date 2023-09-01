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
    val appImageName: String = findProject("appImageName") as String? ?: "auticparkic-api"
    val appImageTag: String = findProject("appImageTag") as String? ?: "latest"
    image = "bogdanjovanovic/${appImageName}:${appImageTag}"
}

jib.from {
    image = "openjdk:17-jdk-alpine"
}

jib.container {
    format = ImageFormat.OCI
    ports = listOf("10000")
//    jvmFlags = listOf("-Xms512m", "-Xmx512m")
//    environment = mapOf("SPRING_PROFILES_ACTIVE" to "production")
}

//frontend {
//    nodeInstallDirectory.set(project.layout.projectDirectory.dir("node"))
//
//    nodeVersion.set("18.17.1")
//    assembleScript.set("run build")
//    verboseModeEnabled.set(true)
//
//    val frontendProjectDir = project.layout.projectDirectory.dir("../frontend")
//    packageJsonDirectory.set(frontendProjectDir)
//}

//tasks.named<InstallFrontendTask>("installFrontend") {
//    doLast {
//        copy {
//            from("../frontend/build")
//            into("src/main/resources/static")
//        }
//    }
//
//    val ciPlatformPresent = providers.environmentVariable("CI").isPresent()
//    val lockFilePath = "../frontend/package-lock.json"
//    val retainedMetadataFileNames: Set<String>
//    if (ciPlatformPresent) {
//        // If the host is a CI platform, execute a strict install of dependencies based on the lock file.
//        installScript.set("ci")
//        retainedMetadataFileNames = setOf(lockFilePath)
//    } else {
//        // The naive configuration below allows to skip the task if the last successful execution did not change neither
//        // the package.json file, nor the package-lock.json file, nor the node_modules directory. Any other scenario
//        // where for example the lock file is regenerated will lead to another execution before the task is "up-to-date"
//        // because the lock file is both an input and an output of the task.
//        val acceptableMetadataFileNames = listOf(lockFilePath, "../frontend/yarn.lock")
//        retainedMetadataFileNames = mutableSetOf("../frontend/package.json")
//        for (acceptableMetadataFileName in acceptableMetadataFileNames) {
//            if (Files.exists(Paths.get(acceptableMetadataFileName))) {
//                retainedMetadataFileNames.add(acceptableMetadataFileName)
//                break
//            }
//        }
//        outputs.file(lockFilePath).withPropertyName("lockFile")
//    }
//    inputs.files(retainedMetadataFileNames).withPropertyName("metadataFiles")
//    outputs.dir("../frontend/node_modules").withPropertyName("nodeModulesDirectory")
//}

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
    implementation(platform("software.amazon.awssdk:bom:2.20.128"))
    implementation("software.amazon.awssdk:s3")

    implementation("commons-io:commons-io:2.13.0")

    runtimeOnly("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.integration:spring-integration-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-integration")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.flywaydb:flyway-core")
    implementation("com.github.javafaker:javafaker:1.0.2")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
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
