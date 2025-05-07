plugins {
	kotlin("jvm")
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(project(":convertible-core"))

	implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
	implementation("org.springframework:spring-context:6.2.6")

	implementation("com.squareup:kotlinpoet:2.1.0")

	testImplementation(kotlin("test"))

	testImplementation("dev.zacsweers.kctfork:core:0.7.0")
	testImplementation("dev.zacsweers.kctfork:ksp:0.7.0")

	testImplementation(platform("org.junit:junit-bom:5.10.2"))
	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")
}

tasks.test {
	useJUnitPlatform()
}

kotlin {
	jvmToolchain(17)
}
