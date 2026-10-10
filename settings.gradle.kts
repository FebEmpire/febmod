pluginManagement {
	val loom_version: String by settings

	repositories {
		maven("https://maven.fabricmc.net/") {
			name = "Fabric"
		}
		mavenCentral()
		gradlePluginPortal()
	}

	plugins {
		id("net.fabricmc.fabric-loom") version loom_version
		id("org.jetbrains.kotlin.jvm") version "2.3.21"
	}
}

rootProject.name = "febmod"