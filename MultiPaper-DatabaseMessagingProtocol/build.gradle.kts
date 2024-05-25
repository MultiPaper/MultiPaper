plugins {
    java
    `maven-publish`
}

version = "${properties["databaseVersion"]}-${properties["mcVersion"]}"

repositories {
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    compileOnly("io.netty:netty-all:4.1.87.Final")
}

publishing {
    publications.create<MavenPublication>("maven") {
        artifact(tasks.jar)
    }
}

