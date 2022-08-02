import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    id("maven-publish")
}

group = "com.cillu"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

publishing {
    publications {
        create<MavenPublication>("mediatork") {
            from(components["java"])
        }
    }

    repositories {
        mavenLocal()
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.reflections:reflections:0.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.23")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("org.junit.jupiter:junit-jupiter:5.8.2")
    implementation("com.rabbitmq:amqp-client:5.15.0")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.sksamuel.hoplite:hoplite-core:2.4.0")
    implementation("com.sksamuel.hoplite:hoplite-yaml:2.4.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

