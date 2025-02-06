val ktorVersion: String by project
val seleniumVersion: String by project

plugins {
    kotlin("jvm") version "2.1.0"
}

group = "org.bread_experts_group"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("org.seleniumhq.selenium:selenium-java:$seleniumVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.4")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}