version = "${properties["masterVersion"]}"

plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow")
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
    implementation(project(":multipaper-mastermessagingprotocol"))
    implementation("org.jetbrains:annotations:22.0.0")
    implementation("org.json:json:20211205")
    implementation("org.yaml:snakeyaml:1.33")
    implementation("io.netty:netty-all:4.1.87.Final")
    implementation("se.llbit:jo-nbt:1.3.0")
    implementation("ch.qos.logback:logback-classic:1.5.3")
    implementation("org.slf4j:slf4j-api:2.0.12")
    compileOnly("net.md-5:bungeecord-api:1.16-R0.4")
    compileOnly("com.velocitypowered:velocity-api:3.0.1")
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("com.velocitypowered:velocity-api:3.0.1")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "puregero.multipaper.server.bootstrap.MultiPaperStandalone",
            "Minecraft-Version" to "${properties["mcVersion"]}",
            "Master-Version" to "${properties["masterVersion"]}"
        )
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    relocate("io.netty", "puregero.multipaper.master.libs.netty")
    relocate("org.yaml.snakeyaml", "puregero.multipaper.master.libs.snakeyaml")
    relocate("se.llbit.nbt", "puregero.multipaper.master.libs.nbt")
}

publishing {
    publications.create<MavenPublication>("maven") {
        artifact(tasks.jar)
    }
}