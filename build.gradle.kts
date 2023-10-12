import io.papermc.paperweight.util.*
import io.papermc.paperweight.util.constants.*

plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.paperweight.patcher") version "1.5.7-SNAPSHOT"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        content { onlyForConfigurations(PAPERCLIP_CONFIG) }
    }
}

dependencies {
    remapper("net.fabricmc:tiny-remapper:0.8.6:fat")
    decompiler("net.minecraftforge:forgeflower:2.0.627.2")
    paperclip("io.papermc:paperclip:3.0.3")
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}

subprojects {
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }
    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://ci.emc.gs/nexus/content/groups/aikar/")
        maven("https://repo.aikar.co/content/groups/aikar")
        maven("https://repo.md-5.net/content/repositories/releases/")
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
        maven("https://jitpack.io")
    }
}

paperweight {
    serverProject.set(project(":multipaper-server"))

    remapRepo.set("https://repo.papermc.io/repository/maven-public/")
    decompileRepo.set("https://repo.papermc.io/repository/maven-public/")

    useStandardUpstream("Purpur") {
        url.set(github("PurpurMC", "Purpur"))
        ref.set(providers.gradleProperty("purpurRef"))
        
        withStandardPatcher {
            apiSourceDirPath.set("Purpur-API") 
            apiPatchDir.set(layout.projectDirectory.dir("patches/api"))
            apiOutputDir.set(layout.projectDirectory.dir("MultiPaper-API"))

            serverSourceDirPath.set("Purpur-Server")
            serverPatchDir.set(layout.projectDirectory.dir("patches/server"))
            serverOutputDir.set(layout.projectDirectory.dir("MultiPaper-Server"))
        }
    }

    tasks.register("purpurRefLatest") {
        // Update the paperRef in gradle.properties to be the latest commit
        val tempDir = layout.cacheDir("purpurRefLatest");
        val file = "gradle.properties";
        
        doFirst {
            data class GithubCommit(
                    val sha: String
            )

            val purpurLatestCommitJson = layout.cache.resolve("purpurLatestCommit.json");
            download.get().download("https://api.github.com/repos/PurpurMC/Purpur/commits/ver/1.20.1", purpurLatestCommitJson);
            val purpurLatestCommit = gson.fromJson<paper.libs.com.google.gson.JsonObject>(purpurLatestCommitJson)["sha"].asString;

            copy {
                from(file)
                into(tempDir)
                filter { line: String ->
                    line.replace("purpurRef = .*".toRegex(), "purpurRef = $purpurLatestCommit")
                }
            }
        }

        doLast {
            copy {
                from(tempDir.file("gradle.properties"))
                into(project.file(file).parent)
            }
        }
    }
}

tasks.generateDevelopmentBundle {
    apiCoordinates.set("puregero.multipaper:MultiPaper-API")
    mojangApiCoordinates.set("io.papermc.paper:paper-mojangapi")
    libraryRepositories.set(
        listOf(
            "https://repo.maven.apache.org/maven2/",
            "https://repo.papermc.io/repository/maven-public/",
            "https://jitpack.io"
        )
    )
}
publishing {
    publications.create<MavenPublication>("devBundle") {
        artifact(tasks.generateDevelopmentBundle) {
            artifactId = "dev-bundle"
        }
    }
}