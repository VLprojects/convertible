import com.vanniktech.maven.publish.SonatypeHost

plugins {
	kotlin("jvm")
	id("com.vanniktech.maven.publish") version "0.32.0"
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

mavenPublishing {
	publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
	signAllPublications()

	pom {
		name.set("convertible-jpa")
		description.set("Annotation-based JPA converter generator for Kotlin Value Objects")
		url.set("https://github.com/vlprojects/convertible")

		licenses {
			license {
				name.set("Apache License 2.0")
				url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
			}
		}

		developers {
			developer {
				id.set("bushin.d")
				name.set("Dmitriy Bushin")
				email.set("bushin.d@vlprojects.pro")
			}
		}

		scm {
			connection.set("scm:git:https://github.com/vlprojects/convertible.git")
			developerConnection.set("scm:git:ssh://github.com/vlprojects/convertible.git")
			url.set("https://github.com/vlprojects/convertible")
		}
	}
}

tasks.test {
	useJUnitPlatform()
}

kotlin {
	jvmToolchain(17)
}
