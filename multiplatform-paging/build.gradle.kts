plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

val artifactName = "mobile-infrastructure-paging"
val artifactGroup = rootProject.group
val artifactVersion = rootProject.version

group = artifactGroup
version = artifactVersion

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

    macosX64 {
        binaries {
            framework {
                baseName = artifactName
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")
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
                implementation("androidx.paging:paging-runtime:3.0.1")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
            }
        }
        val iosMain by getting
        val iosTest by getting
        val macosX64Main by getting
        val nativeDarwinMain by creating {
            dependsOn(commonMain)
            iosMain.dependsOn(this)
            macosX64Main.dependsOn(this)
        }
    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).all {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

publishing {
    publications.withType<MavenPublication>().forEach {
        it.pom {
            name.set("Xharpen Mobile infrastructure paging")
            description.set("Paging library for mobile infrastructure")
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
