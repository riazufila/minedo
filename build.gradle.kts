plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
}

group = "net.minedo.mc"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Paper API.
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")

    // FAWE API.
    implementation(platform("com.intellectualsites.bom:bom-newest:1.35"))
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core:2.8.1")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.8.1") { isTransitive = false }

    // dotenv.
    implementation("io.github.cdimascio:dotenv-java:3.0.0")

    // Apache Commons.
    implementation("org.apache.commons:commons-rng-simple:1.5")
    implementation("org.apache.commons:commons-rng-sampling:1.5")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}