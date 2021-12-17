plugins {
    kotlin("multiplatform")
    id("java-library")
    id("maven-publish")
}

val artifactName = "infrastructure-processor"
val artifactGroup = rootProject.group
val artifactVersion = rootProject.version

group = artifactGroup
version = artifactVersion

kotlin {
    jvm()

    sourceSets {
        all {
            languageSettings {
                optIn("com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview")
                optIn("com.squareup.kotlinpoet.javapoet.KotlinPoetJavaPoetPreview")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(project(":multiplatform-annotation"))

                implementation("com.squareup:javapoet:1.13.0")

                implementation("com.squareup:kotlinpoet:1.10.1")
                implementation("com.squareup:kotlinpoet-metadata:1.10.1")
                implementation("com.squareup:kotlinpoet-javapoet:1.10.1")

                implementation("com.google.dagger:dagger:2.40.1")
            }
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    publications.withType<MavenPublication>().forEach {
        it.pom {
            name.set("Xharpen Mobile infrastructure processor")
            description.set("Annotation processor for mobile infrastructure")
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
            url = uri("https://oss1.sonatype.org/service/local/staging/deploy/maven2/")
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
