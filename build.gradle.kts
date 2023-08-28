plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
}

group = "io.github.riazufila"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    // Paper API.
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")

    // FAWE Core API.
    implementation(platform("com.intellectualsites.bom:bom-newest:1.37"))
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")

    // WorldGuard API.
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.1.0-SNAPSHOT")

    // dotenv.
    implementation("io.github.cdimascio:dotenv-java:3.0.0")

    // Apache Commons.
    implementation("org.apache.commons:commons-rng-simple:1.5")
    implementation("org.apache.commons:commons-rng-sampling:1.5")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}