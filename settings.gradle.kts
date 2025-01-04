import java.util.Locale

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "multipaper"

for (name in listOf("MultiPaper-MasterMessagingProtocol", "MultiPaper-API", "MultiPaper-Server", "MultiPaper-Master")) {
    val projName = name.toLowerCase(Locale.ENGLISH)
    include(projName)
    findProject(":$projName")!!.projectDir = file(name)
}
