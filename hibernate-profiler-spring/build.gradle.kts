
plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    // project dependency
    implementation(project(":hibernate-profiler-core"))

    // apis dependencies
    api(libs.spring.boot.autoconfigure)
    api(libs.spring.context)
    api(libs.datasource.proxy)
    api(libs.hibernate.core)

    // test dependencies
    testImplementation(libs.spring.boot.tests)
    testImplementation(libs.assertj)
    testRuntimeOnly(libs.junit.test.launcher)

}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
