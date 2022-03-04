version = "2.5.2"

plugins {
    `java`
    `maven-publish`
}

repositories {
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

val masterDependency = configurations.create("masterDependency")
configurations.compileClasspath.extendsFrom(masterDependency)

dependencies {
    implementation("org.jetbrains:annotations:22.0.0")
    implementation("net.md-5:bungeecord-api:1.16-R0.4")

    masterDependency("org.json:json:20211205")
}

tasks.jar {
    manifest {
        attributes(
                "Main-Class" to "puregero.multipaper.server.MultiPaperServer"
        )
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    val dependencies = masterDependency.map(::zipTree)
    from(dependencies)
}
