import org.gradle.kotlin.dsl.`java-gradle-plugin`

plugins {
    `java-gradle-plugin`
    //`java-library`
}



repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

/*tasks.named<Test>("test") {
    useJUnitPlatform()
}*/

subprojects.forEach { p ->
    p.repositories {
        mavenCentral()
    }
}

configurations.all {
    repositories {
        mavenCentral()
    }

}
/*
childProjects.forEach { (_, p) ->
    p.repositories {
        mavenCentral()
    }
}*/
