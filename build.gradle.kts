import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	id("net.fabricmc.fabric-loom")
	`maven-publish`
	id("org.jetbrains.kotlin.jvm")
}

val mod_version: String by project
val maven_group: String by project
val minecraft_version: String by project
val loader_version: String by project
val fabric_api_version: String by project
val fabric_kotlin_version: String by project

version = mod_version
group = maven_group

repositories {
	mavenCentral()
	maven {
		name = "meteor-maven"
		url = uri("https://maven.meteordev.org/releases")
	}
}

fabricApi {
	configureDataGeneration {
		client = true
	}
}

dependencies {
	minecraft("com.mojang:minecraft:$minecraft_version")
	// no mappings block

	implementation("net.fabricmc:fabric-loader:$loader_version")
	implementation("net.fabricmc.fabric-api:fabric-api:$fabric_api_version")
	implementation("net.fabricmc:fabric-language-kotlin:$fabric_kotlin_version")

	implementation("meteordevelopment:discord-ipc:1.1")
	include("meteordevelopment:discord-ipc:1.1")
}

tasks.processResources {
	val version = project.version
	inputs.property("version", version)

	filesMatching("fabric.mod.json") {
		expand("version" to version)
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release.set(25)
}

kotlin {
	compilerOptions {
		jvmTarget.set(JvmTarget.JVM_25)
	}
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_25
	targetCompatibility = JavaVersion.VERSION_25
}

tasks.jar {
	val projectName = project.name
	inputs.property("projectName", projectName)

	from("LICENSE") {
		rename { "${it}_$projectName" }
	}
}

// configure the maven publication
publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
		}
	}

	// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
	repositories {
		// Add repositories to publish to here.
		// Notice: This block does NOT have the same function as the block in the top level.
		// The repositories here will be used for publishing your artifact, not for
		// retrieving dependencies.
	}
}