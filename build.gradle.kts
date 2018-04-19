import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.file.Files.delete

group = "cn.ac.iie.syncdata"
version = "1.0"

buildscript {
    var kotlinVersion: String by extra
    kotlinVersion = "1.2.21"

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(kotlinModule("gradle-plugin", kotlinVersion))
    }
}

plugins {
    java
    kotlin("jvm") version "1.2.10"
}

val kotlinVersion: String by extra

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlinModule("stdlib-jdk8", kotlinVersion))

    compile("io.javalin:javalin:1.4.1")
    compile("com.squareup.okhttp3:okhttp:3.10.0")
    compile("com.alibaba", "druid", "1.1.8")

    compile("com.google.code.gson:gson:2.8.2")
    compile("com.lmax:disruptor:3.3.7")
    compile("redis.clients:jedis:2.9.0")

    compile("org.apache.logging.log4j:log4j-slf4j-impl:2.10.0")
    compile("org.apache.logging.log4j:log4j-core:2.10.0")

    compile(fileTree("libs") {
        include("*.jar")
    })
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

var outLibs = "outLibs"

tasks {
    "copyJar"(Copy::class) {
        delete(outLibs)
        from(configurations.runtime)
        into(outLibs)
    }
}
