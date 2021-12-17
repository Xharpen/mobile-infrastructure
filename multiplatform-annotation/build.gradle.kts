plugins {
    kotlin("multiplatform")
    id("java-library")
    id("maven-publish")
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

            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }

            developers {
                developer {
                    id.set("Sparkweb")
                    name.set("Sparkweb")
                    organization.set("Sparkweb")
                    organizationUrl.set("https://sparkweb.kr")
                }
            }

            scm {
                url.set("https://github.com/Xharpen/mobile-infrastructure")
            }

            withXml {
                fun groovy.util.Node.addDependency(dependency: Dependency, scope: String) {
                    appendNode("dependency").apply {
                        appendNode("groupId", dependency.group)
                        appendNode("artifactId", dependency.name.toLowerCase())
                        appendNode("version", dependency.version)
                        appendNode("scope", scope)
                    }
                }
            }
        }
    }

    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("PUBLISH_USERNAME")
                password = System.getenv("PUBLISH_TOKEN")
            }
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
