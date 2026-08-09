plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.datasource.proxy)
    implementation(libs.hibernate.core)
    testImplementation(libs.mokito)
    api(libs.junit.jupiter)

    testRuntimeOnly(libs.junit.test.launcher)
}

//tasks.test(_ -> )

tasks.named<Test>("test") {
    useJUnitPlatform()
}
