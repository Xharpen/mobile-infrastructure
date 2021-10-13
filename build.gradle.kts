buildscript {

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.30")
        classpath("com.android.tools.build:gradle:7.0.0")
    }
}

group = "com.xhlab.mobile"
version = "0.4.1"
