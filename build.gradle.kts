plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
}

group = "io.github.riazufila"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")
    implementation("io.github.cdimascio:dotenv-java:3.0.0")
    implementation("org.apache.commons:commons-rng-simple:1.5")
    implementation("org.apache.commons:commons-rng-sampling:1.5")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}