buildscript {

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
        classpath("com.android.tools.build:gradle:7.0.0")
    }
}

group = "kr.sparkweb"
version = "0.7.0"

subprojects {
    apply {
        plugin("maven-publish")
        plugin("signing")
    }

    // Empty javadoc
    val javadocJar = tasks.register("javadocJar", Jar::class.java) {
        archiveClassifier.set("javadoc")
    }

    configure<PublishingExtension> {
        publications.withType<MavenPublication>().forEach {
            it.artifact(javadocJar)

            it.pom {
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

        configure<SigningExtension> {
            useInMemoryPgpKeys(
                System.getenv("GPG_PRIVATE_KEY"),
                System.getenv("GPG_PRIVATE_PASSWORD")
            )
            sign(publications)
        }
    }
}
