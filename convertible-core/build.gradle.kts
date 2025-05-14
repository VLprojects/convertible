plugins {
	kotlin("jvm")
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("com.squareup:kotlinpoet:2.1.0")
	implementation("com.squareup:kotlinpoet-ksp:2.1.0")
	implementation("com.google.devtools.ksp:symbol-processing-api:2.1.10-1.0.29")
	testImplementation(kotlin("test"))
}

tasks.test {
	useJUnitPlatform()
}

kotlin {
	jvmToolchain(17)
}
