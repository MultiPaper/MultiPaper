import io.papermc.paperweight.util.*
import io.papermc.paperweight.util.constants.*

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.papermc.paperweight.patcher") version "1.4.1"
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/") {
        content { onlyForConfigurations(PAPERCLIP_CONFIG) }
    }
}

dependencies {
    remapper("net.fabricmc:tiny-remapper:0.8.6:fat")
    decompiler("net.minecraftforge:forgeflower:2.0.605.1")
    paperclip("io.papermc:paperclip:3.0.2")
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
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://ci.emc.gs/nexus/content/groups/aikar/")
        maven("https://repo.aikar.co/content/groups/aikar")
        maven("https://repo.md-5.net/content/repositories/releases/")
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
        maven("https://jitpack.io")
    }
}

paperweight {
    serverProject.set(project(":MultiPaper-Server"))

    remapRepo.set("https://maven.fabricmc.net/")
    decompileRepo.set("https://maven.quiltmc.org")

    useStandardUpstream("pufferfish") {
        url.set(github("pufferfish-gg", "Pufferfish"))
        ref.set(providers.gradleProperty("pufferfishRef"))
        
        withStandardPatcher {
            apiSourceDirPath.set("pufferfish-api") 
            apiPatchDir.set(layout.projectDirectory.dir("patches/api"))
            apiOutputDir.set(layout.projectDirectory.dir("MultiPaper-API"))

            serverSourceDirPath.set("pufferfish-server")
            serverPatchDir.set(layout.projectDirectory.dir("patches/server"))
            serverOutputDir.set(layout.projectDirectory.dir("MultiPaper-Server"))
        }
    }

    tasks.register("pufferfishRefLatest") {
        // Update the pufferfishRef in gradle.properties to be the latest commit
        val tempDir = layout.cacheDir("pufferfishRefLatest");
        val file = "gradle.properties";
        
        doFirst {
            data class GithubCommit(
                    val sha: String
            )

            val pufferfishLatestCommitJson = layout.cache.resolve("pufferfishLatestCommit.json");
            download.get().download("https://api.github.com/repos/pufferfish-gg/Pufferfish/commits/ver/1.19", pufferfishLatestCommitJson);
            val pufferfishLatestCommit = gson.fromJson<paper.libs.com.google.gson.JsonObject>(pufferfishLatestCommitJson)["sha"].asString;

            copy {
                from(file)
                into(tempDir)
                filter { line: String ->
                    line.replace("pufferfishRef = .*".toRegex(), "pufferfishRef = $pufferfishLatestCommit")
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
