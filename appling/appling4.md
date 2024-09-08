# 🔴 jacoco

테스트코드의 커버리지를 강제할 수 있는 jacoco 설정을 진행하려고 한다.

## 🟠 설정

```
plugins {
    ...
    id("jacoco")
}

dependencies {
    ...
//    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    runtimeOnly("com.h2database:h2")
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

jacoco {
    toolVersion = "0.8.12" // JaCoCo의 버전을 명시합니다.
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // 테스트가 완료된 후 리포트를 생성하도록 설정

    reports {
        xml.required.set(true) // XML 리포트 생성을 활성화
        csv.required.set(false) // CSV 리포트 생성을 비활성화
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml")) // HTML 리포트의 출력 위치 설정
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            enabled = true
            element = "CLASS"

            // 라인 커버리지를 최소한 80%
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }

            // 브랜치 커버리지를 최소한 90%
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.90".toBigDecimal()
            }

            // 빈 줄을 제외한 코드의 라인수를 최대 200라인으로 제한합니다.
            limit {
                counter = "LINE"
                value = "TOTALCOUNT"
                maximum = "200".toBigDecimal()
            }

            excludes = listOf(
                "*.ApplingApplication*"
                , "*.global.*"
                , "*.Hello*"
            )
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification) // `check` 태스크가 실행될 때 커버리지 검증을 포함하도록 설정
}
```

jacoco 설정을 위해 다음과 같이 정의했다. 기존에 docker compose support는 삭제를 했는데 그 이유는 build와 test를 진행할때 자동으로 docker가 같이 실행되어야하는 설정이 생각보다 복잡해져서 기존에 h2 db를 사용하는 방법으로 우선 변경했다. 나중에 해당 방법이 가능한것인지 확인하여 진행해야될 것 같다.

여기서 주의해야할 점은 excludes 정의시 패키지+파일명으로 정의해야된다는 점이다.

```
spring:
  application:
    name: appling
  profiles:
    active: ${server.profile:local}

---
spring:
  config:
    activate:
      on-profile: local
  h2:
    console:
      enabled: true
      settings:
        web-allow-others: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: create-drop  #절대 수정 금지
      format_sql: true
    show-sql: true
    defer-datasource-initialization: true
    properties:
      hibernate:
        default_batch_size: 100
  datasource:
    hikari:
      driver-class-name: org.h2.Driver
      jdbc-url: jdbc:h2:mem:appling;mode=mysql
      #      jdbc-url: jdbc:h2:tcp://localhost/~/appling
      username: sa
      password:
```

application.yml파일에서도 추가로 h2 설정을 해주었다.