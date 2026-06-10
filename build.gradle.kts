plugins {
    java
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "java")

    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    dependencies {
        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.test {
        useJUnitPlatform()
    }
}
tasks.register("initModules") {
    doLast {
        val modules = listOf("changeDetector", "weatherSensor", "oilSensor", "receiver")

        modules.forEach { m ->
            val dir = file(m)

            file("$dir/src/main/java").mkdirs()
            file("$dir/src/test/java").mkdirs()

            val buildFile = file("$dir/build.gradle.kts")
            if (!buildFile.exists()) {
                buildFile.writeText("""
plugins {
    java
}

repositories {
    mavenCentral()
}
""".trimIndent())
            }
        }
    }
}