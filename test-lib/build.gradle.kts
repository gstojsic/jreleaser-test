import org.jreleaser.model.Active

plugins {
    id("java")
    id("maven-publish")
    id("org.jreleaser") version "1.4.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_19
    targetCompatibility = JavaVersion.VERSION_19
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks.javadoc {
    options {
        (this as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
    }
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("jreleaser-test") {
            from(components["java"])

            pom {
                name.set(project.name)
                description.set("testing jreleaser features")
                url.set("https://github.com/gstojsic/jreleaser-test.git")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                scm {
                    connection.set("scm:git:git@github.com:gstojsic/jreleaser-test.git")
                    url.set("https://github.com/gstojsic/jreleaser-test.git")
                }
                developers {
                    developer {
                        id.set("gstojsic")
                        name.set("Goran Stojšić")
                        email.set("goran.stojsic@gmail.com")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            setUrl(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

jreleaser {
    environment {
        setVariables("jreleaser.toml")
    }

    gitRootSearch.set(true)
    dryrun.set(true)

    release {
        github {
            username.set("gstojsic")
            repoOwner.set("gstojsic")
            commitAuthor {
                name.set("goran")
            }
        }
    }

    signing {
        active.set(Active.ALWAYS)
        armored.set(true)
    }

    deploy {
        maven {
            nexus2 {
                create("maven-central") {
                    active.set(Active.ALWAYS)
                    //url.set("https://s01.oss.sonatype.org/service/local") //original
                    url.set("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                    snapshotUrl.set("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                    closeRepository.set(true)
                    releaseRepository.set(true)
                    stagingRepository("build/staging-deploy")
                }
            }
        }
    }
}