plugins {
    java
    `maven-publish`
}

repositories {
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    compileOnly("io.netty:netty-all:4.1.75.Final")
}

publishing {
    publications.create<MavenPublication>("messagingProtocol") {
        artifact(tasks.jar)
    }
}
