plugins {
    kotlin("jvm") version "2.2.21"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "me.thedivazo"
version = "0.0.1"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    // Paper API
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")

    // Kotlin stdlib
    implementation(kotlin("stdlib"))

    compileOnly("net.dmulloy2:ProtocolLib:5.4.0")

    testImplementation(platform("org.junit:junit-bom:6.0.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
    runServer {
        minecraftVersion("1.21")
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.processResources {
    val props = mapOf("version" to project.version.toString())

    inputs.properties(props)
    filteringCharset = "UTF-8"

    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.test {
    useJUnitPlatform()
}