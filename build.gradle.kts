plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
    id("jacoco")
}

group = "com.trippy"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

jacoco {
    toolVersion = "0.8.12"
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.security:spring-security-crypto")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("org.flywaydb:flyway-core:11.1.0")
    implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.azam.ulidj:ulidj:1.0.1")
    implementation("com.nimbusds:nimbus-jose-jwt:10.5")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    implementation("ch.qos.logback:logback-classic:1.5.20")

    testImplementation("org.mock-server:mockserver-netty-no-dependencies:5.15.0")
    testImplementation("org.mock-server:mockserver-client-java-no-dependencies:5.15.0") {
        exclude("org.sl4j", "slf4j-jdk14")
    }
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.testcontainers:junit-jupiter:1.21.3")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("io.mockk:mockk:1.13.14")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

val excludePackages: List<String> by extra {
    listOf(
        "com/kaiqkt/authentication/Application*",
        "com/kaiqkt/authentication/application/config/*",
        "com/kaiqkt/authentication/application/web/requests/*",
        "com/kaiqkt/authentication/application/web/responses/*",
        "com/kaiqkt/authentication/domain/models/*",
        "com/kaiqkt/authentication/domain/dtos/*"
    )
}

@Suppress("UNCHECKED_CAST")
fun ignorePackagesForReport(jacocoBase: JacocoReportBase) {
    jacocoBase.classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            exclude(jacocoBase.project.extra.get("excludePackages") as List<String>)
        }
    )
}

tasks.withType<JacocoReport> {
    reports {
        html.required.set(true)
    }
    ignorePackagesForReport(this)
}


tasks.withType<JacocoCoverageVerification> {
    violationRules {
        rule {
            limit {
                minimum = "1.0".toBigDecimal()
                counter = "LINE"
            }
            limit {
                minimum = "1.0".toBigDecimal()
                counter = "BRANCH"
            }
        }
    }
    ignorePackagesForReport(this)
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport, tasks.jacocoTestCoverageVerification)
}

