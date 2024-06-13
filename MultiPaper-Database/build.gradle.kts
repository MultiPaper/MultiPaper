version = "${properties["databaseVersion"]}"

plugins {
    java
    `maven-publish`
    id("io.github.goooler.shadow")
}

repositories {
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
    maven {
        url = uri("https://repo.velocitypowered.com/snapshots/")
    }
}

dependencies {
    implementation(project(":multipaper-databasemessagingprotocol"))
    implementation("org.jetbrains:annotations:22.0.0")
    implementation("org.json:json:20211205")
    implementation("org.yaml:snakeyaml:1.33")
    implementation("io.netty:netty-all:4.1.87.Final")
    implementation("se.llbit:jo-nbt:1.3.0")
    compileOnly("net.md-5:bungeecord-api:1.16-R0.4")
    compileOnly("com.velocitypowered:velocity-api:3.0.1")
    annotationProcessor("com.velocitypowered:velocity-api:3.0.1")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "io.multipaper.database.MultiPaperDatabase",
            "Minecraft-Version" to "${properties["mcVersion"]}",
            "Database-Version" to "${properties["databaseVersion"]}"
        )
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    relocate("io.netty", "puregero.multipaper.database.libs.netty")
    relocate("org.yaml.snakeyaml", "puregero.multipaper.database.libs.snakeyaml")
    relocate("se.llbit.nbt", "puregero.multipaper.database.libs.nbt")
}

publishing {
    publications.create<MavenPublication>("maven") {
        artifact(tasks.jar)
    }
}