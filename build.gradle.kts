/*
 * Copyright 2016 Scalified <http://www.scalified.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.SourcesJar
import io.github.simonhauck.release.version.api.Version

buildscript {
    repositories { gradlePluginPortal() }
    // palantir git-version needs Guava at runtime but doesn't declare it
    dependencies { classpath(libs.guava) }
}

plugins {
    `java-library`
    jacoco
    alias(libs.plugins.versions)
    alias(libs.plugins.release)
    alias(libs.plugins.git.version)
    alias(libs.plugins.jacocolog)
    alias(libs.plugins.maven.publish)
}

group = "ch.nvaria"

@Suppress("UNCHECKED_CAST")
val versionDetails = extra["versionDetails"] as groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails>
val details = versionDetails()
val versionFile = layout.settingsDirectory.file("version.properties").asFile
var projectVersion = Version.fromPropertiesFile(versionFile).toString()

// Feature branches publish as e.g. 0.3.0-feature-foo-SNAPSHOT, so they never clash with main
if (details.branchName != null && details.branchName.contains("/")) {
    val branchname = details.branchName.replace("/", "-")
    val parts = projectVersion.split("-")
    projectVersion = parts[0] + "-" + branchname + "-" + parts[1]
}

version = projectVersion

release {
    checkForPreReleaseVersions.set(false)
}

// Public home of the project. Gitea is the source of truth; GitHub is its read-only mirror.
val projectUrl = "https://github.com/nvaria-labs/tree"
val licenseName = "Apache-2.0"
val licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0"

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<JavaCompile>().configureEach {
    // Built with JDK 25, runs on Java 21+. --release also rejects JDK APIs newer than 21,
    // and Gradle derives the published module metadata (org.gradle.jvm.version) from it.
    options.release = 21
    options.encoding = Charsets.UTF_8.name()
    options.compilerArgs.add("-Xlint:unchecked")
    options.isDeprecation = true
}

tasks.withType<Javadoc>().configureEach {
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:all,-missing", "-quiet")
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}

mavenPublishing {
    coordinates(project.group.toString(), "tree", project.version.toString())
    configure(JavaLibrary(javadocJar = JavadocJar.Javadoc(), sourcesJar = SourcesJar.Sources()))

    // Upload, validate, then wait for a manual "Publish" click on central.sonatype.com.
    publishToMavenCentral(automaticRelease = false)

    // Central requires signatures. Only CI release builds have the key
    // (ORG_GRADLE_PROJECT_signingInMemoryKey), so local and snapshot builds stay unsigned.
    if (!providers.gradleProperty("signingInMemoryKey").orNull.isNullOrBlank()) {
        signAllPublications()
    }

    pom {
        name.set("Tree Data Structure Java Library")
        description.set("Tree Data Structure Java Library")
        url.set(projectUrl)
        inceptionYear.set("2016")

        licenses {
            license {
                name.set(licenseName)
                url.set(licenseUrl)
            }
        }
        developers {
            developer {
                id.set("nvaria")
                name.set("Nvaria GmbH")
                url.set(projectUrl)
            }
            developer {
                id.set("tm")
                name.set("Thomas Müllerleile")
                email.set("oss@nvaria.ch")
                organization.set("Nvaria GmbH")
            }
        }
        scm {
            url.set(projectUrl)
            connection.set("scm:git:$projectUrl.git")
            developerConnection.set("scm:git:$projectUrl.git")
        }
        issueManagement {
            system.set("GitHub")
            url.set("$projectUrl/issues")
        }
    }
}

publishing {
    // Internal nexus, in addition to Maven Central
    repositories {
        maven {
            name = "maven-snapshots"
            url = uri(System.getenv("NEXUS_MVN_SNAPSHOTS") ?: "")
            credentials {
                username = System.getenv("NEXUS_USER")
                password = System.getenv("NEXUS_PW")
            }
        }
        maven {
            name = "maven-release"
            url = uri(System.getenv("NEXUS_MVN_RELEASE") ?: "")
            credentials {
                username = System.getenv("NEXUS_USER")
                password = System.getenv("NEXUS_PW")
            }
        }
    }
}

// LICENSE and NOTICE travel with every published jar, as the Apache License asks.
// The javadoc jar comes from the publish plugin's own task type, which is not a Jar.
tasks.matching { it is Jar || it is com.vanniktech.maven.publish.tasks.JavadocJar }.configureEach {
    (this as AbstractCopyTask).from(layout.projectDirectory) {
        include("LICENSE", "NOTICE")
        into("META-INF")
    }
}
