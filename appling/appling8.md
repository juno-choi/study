# 🔴 Querydsl

리스트를 불러오려고 했는데 페이징 처리가 필요해졌다. 페이징 처리를 할때 Querydsl을 적용하여 진행하면 좋을거 같아. Querydsl을 적용시키려고 한다.

## 🟠 설정

### 🟢 gradle

```kt
dependencies {
	...
	// ✅ querydsl을 설치합니다. ":jakarta"를 꼭 설정합니다
	implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
	annotationProcessor("com.querydsl:querydsl-apt:5.0.0:jakarta")
	// java.lang.NoClassDefFoundError(jakarta.persistence.Entity) 발생 대응
	annotationProcessor("jakarta.persistence:jakarta.persistence-api") 
}
```

gradle 설정이 꽤 애를 먹었다. kotlin으로 설정을 진행한 경우가 많진 않아서였다. 공식 문서에서도 maven 설정만 안내할 뿐 kotlin이 없어 한참을 찾은것 같다.

`설정 참고글` [Spring boot 3 버전에서 kotlin gradle에서 queryDSL 설정하기](https://v3.leedo.me/devs/118)

`설정 후 에러 참고글` [java.lang.NoClassDefFoundError](https://velog.io/@gundorit/Spring-java.lang.NoClassDefFoundError-javaxpersistenceEntity)

설정은 다음과 같이 의존성만 추가해주면 끝나는것 같다. 이전에는 더 많은 설정이 필요했는데 querydsl이 많이 편해졌다.

### 🟢 Java 설정

```java
@Configuration
public class QuerydslConfig {
    @PersistenceContext
    private EntityManager em;

    @Bean
    public JPAQueryFactory querydsl() {
        return new JPAQueryFactory(em);
    }
}
```

그리고 다음과 같이 Querydsl을 java에서 주입해주는 코드를 작성하면 끝난다.

정상 실행을 테스트 해보려면 build를 진행하고

```
build > generated > annotationProcessor > java > main
```

쪽에 Q class가 생성되었는지 확인하면 된다.

![](https://velog.velcdn.com/images/ililil9482/post/1462111c-345a-4574-b322-9295bebe2460/image.png)


### 🟢 Jacoco exclude 추가

바로 빌드를 눌렀더니 바로 터졌다. 그 이유는 jacoco 설정 때문이였는데

```kt
tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
            ...

			excludes = listOf(
				"*.ApplingApplication*"
				, "*.global.*"
				, "*.Q*"
			)
		}
	}
}
```
기존에 exclude에 Q class를 제외시키도록 설정만 추가해주었다.
Querydsl 적용 끝!