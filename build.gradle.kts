plugins {
    checkstyle
    java
    jacoco
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
    id("com.github.spotbugs") version "6.0.26"
    id("org.liquibase.gradle") version "3.1.0"
    id("co.uzzu.dotenv.gradle") version "4.0.0"
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.liquibase:liquibase-core:4.30.0")
    }
}

liquibase {
    activities.register("main") {
        this.arguments = mapOf(
            "logLevel"      to "info",
            "url"           to env.JOB4J_DEVOPS_DB_URL.value,
            "username"      to env.DB_USERNAME.value,
            "password"      to env.DB_PASSWORD.value,
            "changelogFile" to "src/main/resources/db/changelog/db.changelog-master.xml"
        )
    }
    runList = "main"
}

group = "ru.job4j.devops"
version = "1.0.0"

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.8".toBigDecimal()
            }
        }

        rule {
            isEnabled = false
            element = "CLASS"
            includes = listOf("org.gradle.*")

            limit {
                counter = "LINE"
                value = "TOTALCOUNT"
                maximum = "0.3".toBigDecimal()
            }
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Production dependencies
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.postgresql)
    implementation(libs.liquibase.core)

    // Test dependencies
    testImplementation(libs.spring.boot.starter.test)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.assertj.core)
    testImplementation(libs.h2database)

    // Liquibase Runtime unified via libs
    liquibaseRuntime(libs.liquibase.core)
    liquibaseRuntime(libs.postgresql)
    liquibaseRuntime(libs.picocli)
    liquibaseRuntime(libs.commons.lang3)
    liquibaseRuntime(libs.logback.core)
    liquibaseRuntime(libs.logback.classic)
}

tasks.spotbugsMain {
    reports.create("html") {
        required = true
        outputLocation.set(layout.buildDirectory.file("reports/spotbugs/spotbugs.html"))
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register<Zip>("zipJavaDoc") {
    group = "documentation"
    description = "Packs the generated Javadoc into a zip archive"

    dependsOn("javadoc")

    from("build/docs/javadoc")
    archiveFileName.set("javadoc.zip")
    destinationDirectory.set(layout.buildDirectory.dir("archives"))
}

tasks.register("checkJarSize") {
    group = "verification"
    description = "Checks the size of the generated JAR file."

    dependsOn("jar")

    doLast {
        val jarFile = file("build/libs/${project.name}-${project.version}.jar")
        if (jarFile.exists()) {
            val sizeInMB = jarFile.length() / (1024 * 1024)
            if (sizeInMB > 5) {
                println("WARNING: JAR file exceeds the size limit of 5 MB. Current size: ${sizeInMB} MB")
            } else {
                println("JAR file is within the acceptable size limit. Current size: ${sizeInMB} MB")
            }
        } else {
            println("JAR file not found. Please make sure the build process completed successfully.")
        }
    }
}

tasks.register<Zip>("archiveResources") {
    group = "custom optimization"
    description = "Archives the resources folder into a ZIP file"

    val inputDir = file("src/main/resources")
    val outputDir = layout.buildDirectory.dir("archives")

    inputs.dir(inputDir)
    outputs.file(outputDir.map { it.file("resources.zip") })

    from(inputDir)
    destinationDirectory.set(outputDir)
    archiveFileName.set("resources.zip")

    doLast {
        println("Resources archived successfully at ${outputDir.get().asFile.absolutePath}")
    }
}

tasks.register("profile") {
    doFirst {
        println(env.JOB4J_DEVOPS_DB_URL.value)
    }
}

tasks.named<Test>("test") {
    systemProperty("spring.datasource.url", env.JOB4J_DEVOPS_DB_URL.value)
    systemProperty("spring.datasource.username", env.DB_USERNAME.value)
    systemProperty("spring.datasource.password", env.DB_PASSWORD.value)
    systemProperty("spring.datasource.driver-class-name", "org.postgresql.Driver")
    systemProperty("spring.jpa.database-platform", "org.hibernate.dialect.PostgreSQLDialect")
}