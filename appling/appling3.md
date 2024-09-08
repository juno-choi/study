# 🔴 프로젝트 기초 설계

간단한 프로젝트라도 기본 설정을 먼저 한 후에 진행하려고 한다.

## 🟠 Swagger

Spring에서 문서는 Swagger와 Restdoc이 있는데 예전에는 테스트코드가 강제되는 Restdoc을 선호했으나 요즘에는 Swagger를 좀더 선호하는 편이다.

그 이유는 Restdoc은 spring에서만 사용하고 있는 반면 Swagger는 다른 api에서 제공이 되고 있어 api에 접근하는 앱, 프론트 개발자 분들이 따로 익히거나 알 필요성이 없이 바로 적응이 가능하기 때문에 더 선호한다.

  
또한 테스트 코드의 강제는 꼭 restdoc이 아니더라도 jacoco와 같은 설정으로 강제할 수 있기 때문에 큰 문제가 되지 않는다고 생각한다.

  
Restdoc으로 Swagger 파일을 생성하여 굳이 Restdoc을 사용할수 있는 방법도 있으나 그냥 간단하게 swagger를 사용하는 편이 더 낫다고 생각한다.

하지만 단점이라고 할수 있는 Controller에 어노테이션이 붙는건 어쩔 수 없다. 그래도 Controller에는 반환에 대한 정보가 담겨야 한다고 생각하기 때문에 어느정도 문서를 위한 어노테이션도 붙는건 괜찮다고 생각한다.

### 🟢 gradle 의존성 추가

```
dependencies {
    ...
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-api:2.5.0")
}
```

다음과 같이 의존성을 추가하면 끝이다.

### 🟢 Swagger 설정 파일 추가

```
@Configuration
@OpenAPIDefinition(info = @Info(title = "Appling API", version = "1.0.0", description = "Appling API Documentation"))
public class SwaggerConfig {
}
```

설정 파일도 어노테이션으로 설정이 가능하여 크게 설정할 것이 없다.

### 🟢 Controller에 적용

```
@RestController
@RequestMapping("/api")
@Tag(name = "Auth API", description = "Appling API Documentation")
public class HelloController {

    @Operation(summary = "hello", description = "hello api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
    })
    @GetMapping("/hello")
    public ResponseEntity<Map<String, Object>> index() {
        Map<String, Object> map = new HashMap<>();
        map.put("message", "Hello, World!");
        return ResponseEntity.ok(map);
    }
}
```

Contorller에 적용도 크게 어렵진 않다. 처음에는 붙는 어노테이션이 많은데 해당 부분은 간소화하는 어노테이션을 새롭게 생성해서 해도 좋을것 같다.

![](https://velog.velcdn.com/images/ililil9482/post/2d570c67-806e-4db4-b9db-d00fb3ac49d5/image.png)


[http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui/index.html#/)로 이동하면 api를 확인해볼 수 있다.