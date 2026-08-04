plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.ktlint) apply false
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

// Récupération sécurisée du jeton SonarCloud depuis local.properties
val localProperties = java.util.Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}
val sonarToken: String? = localProperties.getProperty("sonar.token")

sonarqube {
    properties {
        property("sonar.projectKey", "brunolambinbd-hue_ImageComparisonLib")
        property("sonar.organization", "brunolambinbd-hue")
        property("sonar.host.url", "https://sonarcloud.io")

        // Utilisation du jeton si présent dans local.properties
        sonarToken?.let { property("sonar.token", it) }

        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.scm.provider", "git")
        property("sonar.gradle.skipCompile", "false")
        property(
            "sonar.exclusions",
            "**/build/**,**/generated/**,**/.gradle/**,**/*.tflite",
        )

        // Inclusion du lockfile racine
        property("sonar.sources", "settings-gradle.lockfile")
    }
}

subprojects {
    dependencyLocking {
        lockAllConfigurations()
    }
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.name == "protobuf-javalite") {
                useVersion("3.25.1")
            }
        }
    }
    apply(plugin = "org.sonarqube")
    sonarqube {
        properties {
            val buildDir = project.layout.buildDirectory.get().asFile
            property("sonar.junit.reportPaths", "$buildDir/test-results/testDebugUnitTest")
            property("sonar.coverage.jacoco.xmlReportPaths", "$buildDir/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
            
            // Ajout explicite des sources et du lockfile
            property("sonar.sources", "src/main/java,gradle.lockfile")
        }
    }
}

apply(from = "sync-standards.gradle.kts")
