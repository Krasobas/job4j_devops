pluginManagement {
    val dotenvPath = settings.gradle.startParameter
        .projectProperties["dotenv.filename"]

    val envFile = if (dotenvPath != null) file(dotenvPath)
    else rootDir.resolve("env/.env.example")

    val envProps = java.util.Properties().apply {
        if (envFile.exists()) load(envFile.inputStream())
    }
    fun env(key: String): String = System.getenv(key) ?: envProps.getProperty(key) ?: ""

    repositories {
        val nexusUrl = env("NEXUS_URL")
        if (nexusUrl.isNotBlank()) {
            maven {
                url = uri("$nexusUrl/repository/maven-public/")
                isAllowInsecureProtocol = true
                credentials {
                    username = env("NEXUS_USERNAME")
                    password = env("NEXUS_PASSWORD")
                }
            }
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "DevOps"