import com.vanniktech.maven.publish.SonatypeHost

plugins {
	kotlin("jvm")
	id("com.vanniktech.maven.publish") version "0.32.0"
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

mavenPublishing {
	publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
	signAllPublications()

	pom {
		name.set("convertible-core")
		description.set("Annotation-based converter generator for Kotlin Value Objects")
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
