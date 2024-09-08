# 🔴 Controller 예외처리

## 🟠 Controller 유효성 검사 예외처리

```java
@ApiController
@RequiredArgsConstructor
@Tag(name = "Product API", description = "Product API Documentation")
public class ProductController {
    private final ProductService productService;

    @PostMapping("/product")
    @Operation(summary = "상품 등록", description = "상품 등록 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "정상", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
    })
    public ResponseEntity<ResponseData<PostProductResponse>> product(@RequestBody @Validated PostProductRequest productRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.from(ResponseDataCode.SUCCESS, errors));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.from(ResponseDataCode.SUCCESS, productService.createProduct(productRequest)));
    }
}
```
현재는 다음과 같이 Map을 통해 처리가 되고 있고 유효성 검사가 들어가는 부분은 모두 해당 코드가 반복적으로 처리되어야 한다. 해당 부분을 공통으로 처리해보자.

### 🟢 Controller Advice 처리

```java
@ApiController
@RequiredArgsConstructor
@Tag(name = "Product API", description = "Product API Documentation")
public class ProductController {
    private final ProductService productService;

    @PostMapping("/product")
    @Operation(summary = "상품 등록", description = "상품 등록 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "정상", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
    })
    public ResponseEntity<ResponseData<PostProductResponse>> product(@RequestBody @Validated PostProductRequest productRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.from(ResponseDataCode.SUCCESS, productService.createProduct(productRequest)));
    }
}

```
BindingResult 제거하였다. 그러면 잘못된 요청시

![](https://velog.velcdn.com/images/ililil9482/post/7d9ee2be-181e-4b27-9b81-d45f1cfb0f07/image.png)

다음과 같이 `MethodArgumentNotValidException`가 발생한다.

이제 해당 에러를 컨트롤해보자.

```java
@Builder
public record ResponseError(
    String detail,
    String message
) {
    public static ResponseError from(ObjectError f) {
        return ResponseError.builder()
                .detail(fieldConvertToSnakeCase(((FieldError) f).getField()))
                .message(f.getDefaultMessage())
                .build();
    }

    private static String fieldConvertToSnakeCase(String field) {
        return field.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
}
```

우선 에러를 반환할때 필요한 데이터를 정의한다.

```java
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> methodArgumentNotValidException(MethodArgumentNotValidException e) {

        List<ResponseError> errors = e.getBindingResult().getAllErrors().stream()
                .map(ResponseError::from)
                .collect(Collectors.toList());
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        ProblemDetail problemDetail = createProblemDetailFrom(httpStatus, "잘못된 입력입니다.", errors);
        return ResponseEntity.status(httpStatus).body(problemDetail);
    }

    private static ProblemDetail createProblemDetailFrom(HttpStatus httpStatus, String detail, List<ResponseError> errors) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, detail);
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }
}
```

그 후 `RestControllerAdvice`에 MethodArgumentNotValidException 핸들링을 처리해주면 입력되는 데이터의 유효성 검사를 자동으로 처리할 수 있다.

### ✅ 문제 해결

이번 작업을 하면서 @RestControllerAdvice를 적용했을 때 Swagger가 500 에러가 발생하는 문제가 있었다. 해당 문제는 Spring Boot 3.4.0 M2 버전에서 발생한 에러로 아직 해당 문제가 해결되지 않은것으로 보인다. 우선은 문제 해결을 위해 3.3.3 으로 버전을 낮춰서 사용하여 문제를 해결하였다.