import io.papermc.paperweight.util.*
import io.papermc.paperweight.util.constants.*

plugins {
    java
    `maven-publish`
    id("io.github.goooler.shadow") version "8.1.7"
    id("io.papermc.paperweight.patcher") version "1.7.1"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        content { onlyForConfigurations(PAPERCLIP_CONFIG) }
    }
}

dependencies {
    remapper("net.fabricmc:tiny-remapper:0.10.1:fat")
    decompiler("org.vineflower:vineflower:1.10.1")
    paperclip("io.papermc:paperclip:3.0.3")
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}

subprojects {
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }
    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }
    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }

    repositories {
        mavenCentral()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") // TODO - Adventure snapshot
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

    useStandardUpstream("ShreddedPaper") {
        url.set(github("MultiPaper", "ShreddedPaper"))
        ref.set(providers.gradleProperty("shreddedpaperRef"))
        
        withStandardPatcher {
            apiSourceDirPath.set("ShreddedPaper-API")
            apiPatchDir.set(layout.projectDirectory.dir("patches/api"))
            apiOutputDir.set(layout.projectDirectory.dir("MultiPaper-API"))

            serverSourceDirPath.set("ShreddedPaper-Server")
            serverPatchDir.set(layout.projectDirectory.dir("patches/server"))
            serverOutputDir.set(layout.projectDirectory.dir("MultiPaper-Server"))
        }

        patchTasks.register("generatedApi") {
            isBareDirectory = true
            upstreamDirPath = "paper-api-generator/generated"
            patchDir = layout.projectDirectory.dir("patches/generated-api")
            outputDir = layout.projectDirectory.dir("paper-api-generator/generated")
        }
    }

    tasks.register("shreddedpaperRefLatest") {
        // Update the paperRef in gradle.properties to be the latest commit
        val tempDir = layout.cacheDir("shreddedpaperRefLatest");
        val file = "gradle.properties";
        
        doFirst {
            data class GithubCommit(
                    val sha: String
            )

            val shreddedpaperLatestCommitJson = layout.cache.resolve("shreddedpaperLatestCommit.json");
            download.get().download("https://api.github.com/repos/MultiPaper/ShreddedPaper/commits/main", shreddedpaperLatestCommitJson);
            val shreddedpaperLatestCommit = gson.fromJson<paper.libs.com.google.gson.JsonObject>(shreddedpaperLatestCommitJson)["sha"].asString;

            copy {
                from(file)
                into(tempDir)
                filter { line: String ->
                    line.replace("shreddedpaperRef = .*".toRegex(), "shreddedpaperRef = $shreddedpaperLatestCommit")
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