# 🔴 CORS 처리

## 🟠 크로스 도메인 이슈

- 크로스 도메인 이슈는 브라우저에서 다른 도메인으로 요청을 보낼 때 발생하는 보안 이슈
- 브라우저는 보안을 위해 동일한 출처 정책을 적용
- 출처가 다르면 브라우저는 요청을 보낼 수 없음
- 이를 해결하기 위해 CORS(Cross-Origin Resource Sharing) 헤더를 사용

### 🟢 CORS 헤더

- CORS 헤더는 브라우저에서 다른 도메인으로 요청을 보낼 때 사용되는 헤더
- 브라우저는 이 헤더를 통해 서버에서 허용하는 도메인을 확인하고, 허용되지 않는 도메인에서는 요청을 보낼 수 없음
- 이 헤더는 서버에서 설정하고, 브라우저는 이를 확인하여 요청을 보낼 수 있는지 여부를 결정

### 🟢 CORS 헤더의 종류

- Access-Control-Allow-Origin: 허용된 출처를 지정
- Access-Control-Allow-Methods: 허용된 메서드를 지정
- Access-Control-Allow-Headers: 허용된 헤더를 지정
- Access-Control-Allow-Credentials: 허용된 자격 증명을 지정

## 🟠 Spring에서 CORS 처리

Spring에서는 WebMvcConfigurer를 구현하여 CORS 설정을 추가할 수 있다.

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedHeaders("*")
                .allowedOrigins("*")
                .allowedMethods("*")
                ;
    }
}
```

다음과 같이 설정하면 모든 도메인에서 모든 메서드를 허용하는 CORS 설정을 추가할 수 있다.

front에서 api를 호출할때 cors 이슈가 발생하지 않도록 처리해두기 위해 위의 설정을 추가했다.

CORS에러를 처음에 애를 먹는 사람들이 대부분 swagger나 postman과 같이 api를 테스트할땐 정상적으로 작동하지만 앱이나 웹에서 호출할때 200으로 반환되지만 브라우저 측면에서 호출이 막혀버리기 때문에 처리하기 어려움을 겪는 사람들이 많다. 그럴때 위와 같이 설정해주면 된다.