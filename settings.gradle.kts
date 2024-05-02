import java.util.Locale

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
        // io.github.goooler.shadow
        maven("https://plugins.gradle.org/m2/")
    }
}

rootProject.name = "multipaper"

for (name in listOf("MultiPaper-MasterMessagingProtocol", "MultiPaper-API", "MultiPaper-Server", "MultiPaper-Master")) {
    val projName = name.toLowerCase(Locale.ENGLISH)
    include(projName)
    findProject(":$projName")!!.projectDir = file(name)
}
