import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    id("maven-publish")
    jacoco
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
    implementation("aws.sdk.kotlin:sns:0.16.0")
    implementation("aws.sdk.kotlin:sqs:0.16.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
    exclude("/*log*.class")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

sourceSets {
    create("integrationTest") {
        kotlin {
            compileClasspath += main.get().output + configurations.testRuntimeClasspath
            runtimeClasspath += output + compileClasspath
        }
    }
}

val integrationTest = task<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
}


jacoco {
    toolVersion = "0.8.8"
    reportsDirectory.set(layout.buildDirectory.dir("customJacocoReportDir"))
}

tasks.jacocoTestReport {

    executionData.setFrom(fileTree(buildDir).include("/jacoco/*.exec"))

    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
    dependsOn("test", "integrationTest")
}



tasks["test"].finalizedBy("integrationTest")
tasks["integrationTest"].finalizedBy("jacocoTestReport")