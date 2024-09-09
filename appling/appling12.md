# 🔴 Service에서 예외 발생 부분 처리하기

Controller에서 반환하는 데이터를 처리하면서 Service에서 Exception이 발생하는 경우도 처리해야 된다.

## 🟠 Service에서 Exception 발생하는 경우

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductCustomRepository productCustomRepository;

    ...

    @Transactional
    @Override
    public PutProductResponse putProduct(PutProductRequest putProductRequest) {
        ProductEntity productEntity = productRepository.findById(putProductRequest.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 상품입니다."));

        productEntity.update(putProductRequest);
        ProductEntity updateProductEntity = productRepository.save(productEntity);
        return PutProductResponse.from(updateProductEntity);
    }

}

```

`putProduct` method 부분을 확인해보면 proudct_id가 유효하지 않은 경우 `IllegalArgumentException`이 터지고 있다. 개발자가 일부러 예외로 터트린 부분으로 해당 부분도 처리해줘야 한다.

```java
@SpringBootTest
@AutoConfigureMockMvc
@Transactional(readOnly = true)
class ProductControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    ...
    
    @Test
    @DisplayName("[PUT] /api/v1/product 유효하지 않은 상품은 실패")
    void putProductFail() throws Exception{
        //given
        final Long NOT_EXISTS_PRODUCT_ID = 100L;
        PutProductRequest putProductRequest = PutProductRequest.builder()
                .productId(NOT_EXISTS_PRODUCT_ID)
                .productType("12과")
                .productPrice(100000)
                .productStock(10)
                .productWeight(10)
                .productName("수정 상품")
                .productStatus(ProductStatus.ON_SALE)
                .build();
        //when
        ResultActions perform = mockMvc.perform(put("/api/v1/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(putProductRequest)));
        //then
        perform.andExpect(status().is4xxClientError());
        perform.andReturn().getResponse()
            .getContentAsString(StandardCharsets.UTF_8).contains("유효하지 않은");
    }
}
```

해당 테스트를 진행시키면 400 에러가 아닌 500으로 터져버린다. 해당 부분을 400으로 떨어질 수 있게 변경해보자.

### 🟢 advice 설정

```java
@RestControllerAdvice(basePackages = "com.simol.appling")
public class GlobalAdvice {

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> illegalArgumentException(IllegalArgumentException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        List<ResponseError> errors = List.of(ResponseError.from("", e.getMessage()));
        ProblemDetail problemDetail = createProblemDetailFrom(httpStatus, httpStatus.getReasonPhrase(), errors);
        return ResponseEntity.status(httpStatus).body(problemDetail);
    }

    private static ProblemDetail createProblemDetailFrom(HttpStatus httpStatus, String detail, List<ResponseError> errors) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, detail);
        problemDetail.setType(URI.create("/swagger-ui/index.html"));
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }
}
```

`IllegalArgumentException` 핸들링할 수 있도록 advice를 설정해준다.

그리고 위에 작성해둔 테스트 코드를 실행하면 정상적으로 실행된다.

api를 직접 실행했을 때는

```json
{
  "type": "/swagger-ui/index.html",
  "title": "Bad Request",
  "status": 400,
  "detail": "Bad Request",
  "instance": "/api/v1/product",
  "errors": [
    {
      "detail": "",
      "message": "유효하지 않은 상품입니다."
    }
  ]
}
```

다음과 같이 내가 의도한 대로 반환되고 있다.