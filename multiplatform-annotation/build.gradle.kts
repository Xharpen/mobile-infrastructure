plugins {
    kotlin("multiplatform")
    id("java-library")
}

val artifactName = "mobile-infrastructure-annotation"
val artifactGroup = rootProject.group
val artifactVersion = rootProject.version

group = artifactGroup
version = artifactVersion

kotlin {
    jvm()
    ios()
    macosX64()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    publications.withType<MavenPublication>().forEach {
        it.pom {
            name.set("Xharpen Mobile infrastructure annotation")
            description.set("Annotations for mobile infrastructure")
            url.set("https://github.com/Xharpen/mobile-infrastructure")
        }
    }
}

afterEvaluate {
    project.publishing.publications.withType<MavenPublication>().all {
        groupId = group.toString()
        version = version
        artifactId = when {
            name.contains("metadata") -> "$artifactName-common"
            name.contains("kotlinMultiplatform") -> artifactName
            else -> "$artifactName-$name"
        }.toLowerCase()
    }
}
