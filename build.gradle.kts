subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
    }


    dependencies {
        add("compileOnly", "org.projectlombok:lombok:1.18.38")
        add("annotationProcessor", "org.projectlombok:lombok:1.18.38")

        add("testCompileOnly", "org.projectlombok:lombok:1.18.38")
        add("testAnnotationProcessor", "org.projectlombok:lombok:1.18.38")
    }
}