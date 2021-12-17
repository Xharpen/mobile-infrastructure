plugins {
    kotlin("multiplatform")
    id("java-library")
}

val artifactName = "mobile-infrastructure-processor"
val artifactGroup = rootProject.group
val artifactVersion = rootProject.version

group = artifactGroup
version = artifactVersion

description = "Annotation processor for mobile infrastructure"

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
