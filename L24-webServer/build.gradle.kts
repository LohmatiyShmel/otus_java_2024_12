plugins {
    id ("org.springframework.boot")
}

dependencies {
    implementation("ch.qos.logback:logback-classic")
    implementation("com.google.code.gson:gson")

    implementation ("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation ("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation ("org.springframework.boot:spring-boot-starter-web:3.5.3")
    implementation ("org.flywaydb:flyway-core")
    implementation ("org.postgresql:postgresql")

    runtimeOnly("org.flywaydb:flyway-database-postgresql")
    compileOnly ("org.projectlombok:lombok")
    annotationProcessor ("org.projectlombok:lombok")

    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
}