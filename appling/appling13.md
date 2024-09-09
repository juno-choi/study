# 🔴 log 전역으로 처리하기

간단한 로그를 남기려고 하는데 Controller랑 Service마다 로그를 직접 찍지 않고 자동화처리 하고 싶다.

해당 부분을 Aspect(AOP)를 사용하여 처리해보자.

## 🟠 설정

```kotlin
dependencies {
	...
	implementation("org.springframework:spring-aop:6.0.11")
}
```

의존성 주입만 해주면 끝이다.

## 🟠 적용

### 🟢 Controller Log

```java
@Component
@Aspect
@Slf4j
public class GlobalControllerLog {
    @Around("execution(* com.simol.appling..*.controller..*.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        String type = pjp.getSignature().getDeclaringTypeName();
        String method = pjp.getSignature().getName();
        String requestURI = ((ServletRequestAttributes) requestAttributes).getRequest()
                .getRequestURI();

        log.info("[appling] [controller] requestUri=[{}] package = [{}], method = [{}]",
                requestURI, type, method);

        return pjp.proceed();
    }
}
```

`@Around("execution(* com.simol.appling..*.controller..*.*(..))")` 설정을 통해 해당 패키지의 controller에 포함된 경우에만 적용되도록 했다.

그리고 controller에서 요청된 url과 해당 controller의 package, method명을 로그로 찍도록 설정했다.

### 🟢 Service Log

```java
@Aspect
@Component
@Slf4j
public class GlobalServiceLog {
    @Around("execution(* com.simol.appling..*.service..*.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        log.info("[appling] [service] method = [{}]", pjp.getSignature().toShortString());

        return pjp.proceed();
    }
}
```

`@Around("execution(* com.simol.appling..*.service..*.*(..))")` 설정을 통해 해당 패키지의 service가 포함된 경우에만 적용되도록 했다.

그리고 service의 method명만 찍도록 했다.

![](https://velog.velcdn.com/images/ililil9482/post/01b194df-fa62-4f32-96d7-c15a14cb7ca2/image.png)

로그가 잘 찍히는 것을 확인했다. 로그를 저장하기 위해 LogBack을 설정해야되지만 아직은 필요하지 않아 나중에 설정하기로 하자!