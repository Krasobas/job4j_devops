pluginManagement {
    val envFile = rootDir.resolve("env/.env.example")
    val envProps = java.util.Properties().apply {
        if (envFile.exists()) load(envFile.inputStream())
    }
    fun env(key: String): String = System.getenv(key) ?: envProps.getProperty(key) ?: ""

    repositories {
        maven {
            url = uri("${env("NEXUS_URL")}/repository/maven-public/")
            isAllowInsecureProtocol = true
            credentials {
                username = env("NEXUS_USERNAME")
                password = env("NEXUS_PASSWORD")
            }
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "DevOps"