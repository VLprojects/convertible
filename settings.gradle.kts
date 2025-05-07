pluginManagement {
	plugins {
		kotlin("jvm") version extra["kotlin.version"] as String
		id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
	}
	repositories {
		gradlePluginPortal()
		mavenCentral()
	}
}

rootProject.name = "convertible"

include("convertible-core")
include("convertible-jpa")
