pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

rootProject.name = "MultiPaper"

include("MultiPaper-MasterMessagingProtocol", "MultiPaper-API", "MultiPaper-Server", "MultiPaper-Master")
