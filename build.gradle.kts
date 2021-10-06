plugins {
    kotlin("jvm") version "1.5.31"
    java
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = org.gradle.api.JavaVersion.VERSION_11
    targetCompatibility = org.gradle.api.JavaVersion.VERSION_11
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    // https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients
    implementation("org.apache.kafka:kafka-clients:3.0.0")
    // https://mvnrepository.com/artifact/org.apache.kafka/kafka-streams
    implementation("org.apache.kafka:kafka-streams:3.0.0")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.0")
    // https://mvnrepository.com/artifact/org.twitter4j/twitter4j-stream
    implementation("org.twitter4j:twitter4j-stream:4.0.7")

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}