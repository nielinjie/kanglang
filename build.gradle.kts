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
}

dependencies {

    implementation("org.flowable:flowable-spring-boot-starter:7.0.0")
    implementation("com.h2database:h2:2.1.214")

    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-runner-junit5:5.5.5")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(19)
}