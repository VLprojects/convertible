allprojects {
	group = project.property("group")!!
	version = project.property("version")!!

	repositories {
		mavenCentral()
	}
}
