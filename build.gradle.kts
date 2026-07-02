plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.jpa") version "2.0.21"
    kotlin("plugin.allopen") version "2.0.21"
    application
}

group = "com.example"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
    implementation("org.hibernate.orm:hibernate-core:7.4.3.Final")
    runtimeOnly("com.h2database:h2:2.3.232")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.16")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

sourceSets {
    main {
        kotlin.srcDirs("src")
        resources.srcDirs("resources")
    }
    test {
        kotlin.srcDirs("test")
    }
    create("jpaTest") {
        kotlin.srcDirs("testJpa")
        resources.srcDirs("resources")
        compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
        runtimeClasspath += output + compileClasspath
    }
}

application {
    mainClass.set("MainKt")
}

tasks.register<JavaExec>("runJpa") {
    group = "application"
    description = "Run the JPA/H2 example scenario."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("JpaMainKt")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<Test>("jpaTest") {
    group = "verification"
    description = "Runs JPA/H2 integration tests."
    testClassesDirs = sourceSets["jpaTest"].output.classesDirs
    classpath = sourceSets["jpaTest"].runtimeClasspath
    useJUnitPlatform()
    shouldRunAfter(tasks.test)
}

tasks.check {
    dependsOn(tasks.test)
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}
