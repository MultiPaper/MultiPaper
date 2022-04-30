version = "2.8.0"

plugins {
    `java`
    `maven-publish`
    id("com.github.johnrengelman.shadow")
}

repositories {
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    implementation(project(":MultiPaper-MasterMessagingProtocol"))
    implementation("org.jetbrains:annotations:22.0.0")
    implementation("org.json:json:20211205")
    implementation("io.netty:netty-all:4.1.75.Final")
    implementation("com.hazelcast:hazelcast:5.0.3")
    compileOnly("net.md-5:bungeecord-api:1.16-R0.4")
}

tasks.jar {
    manifest {
        attributes(
                "Main-Class" to "puregero.multipaper.server.MultiPaperServer"
        )
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    relocate("io.netty", "puregero.multipaper.server.libs.netty")
    relocate("com.hazelcast", "puregero.multipaper.server.libs.hazelcast")
    mergeServiceFiles()
}