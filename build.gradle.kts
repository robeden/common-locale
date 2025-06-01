import java.nio.charset.StandardCharsets

plugins {
    `java-library`
    `maven-publish`
    signing
}

group = "com.logicartisan"


version = project.property("VERSION") as String


dependencies {
    api("org.slf4j:slf4j-api:1.7.21")
    api("com.google.code.findbugs:jsr305:3.0.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}


repositories {
    mavenCentral()
}

java {
    withJavadocJar()
    withSourcesJar()
}

val testJavaVersion = System.getProperty("test.java.version", "21").toInt()
tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
    }

    val javaToolchains = project.extensions.getByType<JavaToolchainService>()
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(testJavaVersion))
    })
}

tasks.withType<JavaCompile> {
    options.encoding = StandardCharsets.UTF_8.toString()
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
}


tasks.named<Javadoc>("javadoc") {
    // Disable warnings when methods aren't commented.
    // See https://github.com/gradle/gradle/issues/15209 for why this crazy cast is happening.
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:missing", "-quiet")
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.name
            from(components["java"])
            pom {
                name = project.name
                description = "Localization framework for Java with compile-time verification."
                url = "https://github.com/robeden/common-locale/"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "robeden"
                        name = "Rob Eden"
                        email = "rob@robeden.com"
                    }
                }
                scm {
                    url = "https://github.com/robeden/common-locale/"
                    connection = "scm:git:git://github.com/robeden/common-locale.git"
                    developerConnection = "scm:git:ssh://git@github.com/robeden/common-locale.git"
                }
            }
        }
    }
    repositories {
        maven {
            name = "OSSRH"
            url = if (version.toString().endsWith("SNAPSHOT"))
                uri("https://oss.sonatype.org/content/repositories/snapshots/")
            else uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")

            credentials {
                username = findProperty("ossrhUsername")?.toString() ?: System.getenv("OSSRH_USERNAME")
                password = findProperty("ossrhPassword")?.toString() ?: System.getenv("OSSRH_PASSWORD")
            }
        }
    }
}

signing {
    val signingInMemoryKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingInMemoryKey, signingPassword)
    sign(publishing.publications["mavenJava"])
}