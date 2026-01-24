plugins {
	id("org.springframework.boot") version "4.0.1"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("jvm") version "2.3.0"
	kotlin("plugin.spring") version "2.3.0"
	kotlin("kapt") version "2.3.0"
	kotlin("plugin.jpa") version "2.3.0"
}

group = "com.petcaresuite"
version = "0.2"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
	// Shared Library
	maven {
		url = file("${rootProject.projectDir}/../library-project/build/repos").toURI()
	}
}

extra["springCloudVersion"] = "2024.0.0"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.3")
	implementation("org.redisson:redisson-spring-boot-starter:3.17.0")
	implementation("org.mapstruct:mapstruct:1.6.3")
	implementation("jakarta.validation:jakarta.validation-api:3.1.1")
	implementation("com.google.guava:guava:33.5.0-jre")
	kapt ("org.mapstruct:mapstruct-processor:1.6.3")
	implementation("io.jsonwebtoken:jjwt-jackson:0.13.0")
	compileOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("org.springframework.boot:spring-boot-devtools")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

kapt {
	arguments {
		arg("mapstruct.defaultComponentModel", "spring")
		arg("mapstruct.unmappedTargetPolicy", "IGNORE")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// Pass system properties to the bootRun task
tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
	systemProperties = System.getProperties().mapKeys { it.key.toString() }
}
