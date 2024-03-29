import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.21"
    id("org.springframework.boot") version "3.0.1"
    id("io.spring.dependency-management") version "1.1.0"
    val kotlinVersion = "1.9.21"
    kotlin("plugin.spring") version kotlinVersion
    application

}

group = "xyz.nietongxue"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {

    implementation("org.flowable:flowable-spring-boot-starter:7.0.0")
    implementation("com.h2database:h2:2.1.214")
    implementation("xyz.nietongxue:common:1.0-SNAPSHOT")
    implementation("org.redundent:kotlin-xml-builder:1.9.1")
    implementation("dev.langchain4j:langchain4j:0.25.0")
    implementation("org.yaml:snakeyaml:2.2")
    implementation("dev.langchain4j:langchain4j-open-ai:0.25.0")
    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5:5.5.5")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(19)
}
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
    }
}