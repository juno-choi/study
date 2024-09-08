# 🔴 Controller 만들기

지금까지 Service만 작성하고 실제로 Controller는 작성하지 않았다. 이제 Controller를 적용해보자.

## 🟠 Controller 적용하기

### 🟢 기존 Controller 쓰기

```java
@RestController
@RequestMapping("/api/v1")
public class ProductController {
}
```

RestController를 적용하고 기본 값으로 `/api/v1`을 붙여주려고 한다. 근데 만드는 Controller마다 붙이는게 너무 귀찮을거 같아 Annotation으로 설정하려고 하다.

### 🟢 @ApiController 만들기

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@RestController
@RequestMapping("/api/v1")
public @interface ApiController {
}
```

`@ApiController`를 Annotation으로 정의하고

```java
@ApiController
public class ProductController {
}
```

기존에 Controller 적용 대신 다음과 같이 적용해준다.

## 🟠 Swagger 적용하기

### 🟢 Controller 처리

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

상품 Controller에 `@Tag()`를 추가해주고 각 Url에 대한 설명을 위해 `@Operation()` `@ApiResponse()`를 추가해준다.

그리고 Controller에 추가적으로 `@Validated`와 `BindingResult`를 추가로 적용하여 유효성 검사를 추가해준다.

```java
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostProductRequest {

    @NotNull(message = "상품명을 입력해 주세요.")
    @JsonProperty("product_name")
    @Schema(description = "상품명", example = "아리수")
    private String productName;
    @NotNull(message = "상품 무게를 입력해 주세요.")
    @JsonProperty("product_weight")
    @Schema(description = "상품 무게", example = "5")
    private int productWeight;
    @NotNull(message = "상품 타입을 입력해 주세요. ex) 사과는 11과")
    @JsonProperty("product_type")
    @Schema(description = "상품 타입", example = "11과")
    private String productType;
    @NotNull(message = "상품 가격을 입력해 주세요.")
    @JsonProperty("product_price")
    @Schema(description = "상품 가격", example = "100000")
    private int productPrice;
    @NotNull(message = "상품 수량을 입력해 주세요.")
    @JsonProperty("product_stock")
    @Schema(description = "상품 수량", example = "100")
    private int productStock;

    ...
}

```

Request에 Swagger에 대한 추가 설정을 한다.

![](https://velog.velcdn.com/images/ililil9482/post/d41e3b30-922e-4147-b486-adce8b1255fa/image.png)

Swagger 추가 설정을 통해 테스트 시 기본 값과 API의 요청 Schema에 대한 설명을 추가해두었다.

```java
@Entity
@Table(name = "product")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class ProductEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    private String productName;
    private int productWeight;
    private String productType;
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;
    private int productPrice;
    private int productStock;

    ...
}
```

추가로 Entity에 `@Enumerated(EnumType.STRING)`를 추가해두었다.

