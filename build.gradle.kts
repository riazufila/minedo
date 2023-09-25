plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
}

group = "net.minedo.mc"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    // Paper API.
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")

    // WorldEdit API.
    compileOnly("com.sk89q.worldedit:worldedit-core:7.2.9")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.9")

    // dotenv.
    implementation("io.github.cdimascio:dotenv-java:3.0.0")

    // Apache Commons.
    implementation("org.apache.commons:commons-rng-simple:1.5")
    implementation("org.apache.commons:commons-rng-sampling:1.5")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}