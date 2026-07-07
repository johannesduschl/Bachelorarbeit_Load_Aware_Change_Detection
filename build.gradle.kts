plugins {
    id("com.google.protobuf") version "0.9.4" apply false
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.google.protobuf")

    repositories {
        mavenCentral()
    }

    dependencies {
        add("compileOnly", "org.projectlombok:lombok:1.18.38")
        add("annotationProcessor", "org.projectlombok:lombok:1.18.38")

        add("testCompileOnly", "org.projectlombok:lombok:1.18.38")
        add("testAnnotationProcessor", "org.projectlombok:lombok:1.18.38")

        add("implementation", "io.grpc:grpc-netty-shaded:1.65.1")
        add("implementation", "io.grpc:grpc-protobuf:1.65.1")
        add("implementation", "io.grpc:grpc-stub:1.65.1")
        add("implementation", "com.google.protobuf:protobuf-java:3.25.3")
        add("implementation", "javax.annotation:javax.annotation-api:1.3.2")
    }

    configure<com.google.protobuf.gradle.ProtobufExtension> {
        protoc {
            artifact = "com.google.protobuf:protoc:3.25.3"
        }

        plugins {
            create("grpc") {
                artifact = "io.grpc:protoc-gen-grpc-java:1.65.1"
            }
        }

        generateProtoTasks {
            all().forEach {
                it.plugins {
                    create("grpc")
                }
            }
        }
    }
}