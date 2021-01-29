plugins {
    id("com.android.library")
    kotlin("multiplatform") version "1.4.20"
    id("kotlin-android-extensions")
    id("maven-publish")
}

val artifactName = "infrastructure"
val artifactGroup = "com.xhlab.mobile"
val artifactVersion = "0.1.4"

group = artifactGroup
version = artifactVersion

repositories {
    google()
    mavenCentral()
    jcenter()
}

kotlin {
    android {
        publishLibraryVariants("release", "debug")
    }
    ios {
        binaries {
            framework {
                baseName = artifactName
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.0-alpha02")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.2")
            }
        }
        val iosMain by getting
        val iosTest by getting
    }
}

android {
    compileSdkVersion(30)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
    }
}

publishing {
    publications.withType<MavenPublication>().forEach {
        it.pom {
            name.set("Xharpen Mobile infrastructure")
            description.set("Infrastructure classes for base architectures")
            url.set("https://github.com/Xharpen/mobile-infrastructure")

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
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Xharpen/mobile-infrastructure")
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