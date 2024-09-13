# 🔴 상품 상세 (with. 코드리뷰, n+1 문제 해결)

이제 어느정도 세팅이 된거 같으니 일반적으로 기능을 만드는 과정을 해보려고 한다! 상품 상세에 대해 작성해보자

## 🟠 Service 추가

### 🟢 서비스 및 도메인 정의

```java
public interface ProductService {
    ...
    /**
     * 상품 상세
     * @param productId
     * @return
     */
    ProductDetailResponse getProductDetail(Long productId);
}
```

우선 서비스를 정의해보자. getProductDetail을 정의하려고 하면 `ProductDetailResponse` 반환하고자 하는 class가 존재하지 않는다. 반환 class를 먼저 작성해야 한다.

```java
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
@Schema(description = "상품 반환 데이터")
public record ProductDetailResponse(
    @Schema(description = "상품번호", example = "1")
    Long productId,
    @Schema(description = "상품명", example = "아리수")
    String productName,
    @Schema(description = "상품 타입", example = "OPTION")
    ProductType productType,
    @Schema(description = "상품 상태", example = "ON_SALE")
    ProductStatus productStatus,
    @Schema(description = "상품 옵션")
    List<ProductOptionVo> productOption

) {}
```

```java
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
@Schema(description = "상품 옵션 반환 데이터")
public record ProductOptionVo (
    @Schema(description = "상품 옵션 번호", example = "1")
    Long productOptionId,
    @Schema(description = "상품 옵션명", example = "5kg 11-12과")
    String productOptionName,
    @Schema(description = "상품 정렬 순서", example = "1")
    int productOptionSort,
    @Schema(description = "상품 옵션 가격", example = "100000")
    int productOptionPrice,
    @Schema(description = "상품 옵션 재고", example = "100")
    int productOptionStock,
    @Schema(description = "상품 옵션 상태", example = "ON_SALE")
    ProductOptionStatus productOptionStatus,
    @Schema(description = "상품 옵션 설명", example = "5kg 11-12과입니다.")
    String productOptionDescription
){}
```

반환을 위한 vo 객체를 먼저 정의한다. service를 작성하기 위해서 domain이 먼저 정의되어야 한다. 자연스럽게 도메인을 정의하고 서비스를 정의하게 된다.

이제 서비스를 정의해보자.

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductCustomRepository productCustomRepository;
    private final ProductOptionRepository productOptionRepository;
    
    ...

    @Override
    public ProductDetailResponse getProductDetail(Long productId) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 상품입니다."));
        return ProductDetailResponse.from(productEntity);
    }
}
```

코드가 이렇게 나올거 같다. n+1이 발생할거 같지만 우선 해보자!

![](https://velog.velcdn.com/images/ililil9482/post/8652d4bc-e0e4-4acc-939b-34acc99c5ad7/image.png)

실제로 select 할때 product와 productOption을 조회하기 위해 2번의 쿼리가 나간다.

테스트를 위해 테스트 코드를 작성해보자!

```java
@SpringBootTest
class ProductServiceImplTest {
    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @AfterEach
    void cleanUp() {
        productRepository.deleteAll();
        productOptionRepository.deleteAll();
    }

    ...

    @Test
    @DisplayName("상품 번호가 유효하지 않아 상품 상세를 불러오는데 실패한다.")
    void getProductDetailFailByProductId() {
        //given
        Long productId = 0L;
        //when
        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> productService.getProductDetail(productId))
                .withMessageContaining("유효하지 않은");  
    }

    @Test
    @DisplayName("상품 상세를 불러오는데 성공한다.")
    void getProductDetail() {
        //given
        PostProductOptionDto option = PostProductOptionDto.builder()
                .productOptionName("11-12과")
                .productOptionPrice(100000)
                .productOptionStatus(ProductOptionStatus.ON_SALE)
                .productOptionStock(100)
                .productOptionDescription("아리수 11-12과 입니다.")
                .productOptionSort(1)
                .build();

        PostProductRequest productRequest = PostProductRequest.builder()
                .productName("아리수")
                .productType(ProductType.OPTION)
                .productOption(List.of(option))
                .build();

        ProductEntity saveProduct = productRepository.save(ProductEntity.from(productRequest));
        ProductOptionEntity saveProductOption = productOptionRepository.save(ProductOptionEntity.from(option, saveProduct));
        saveProduct.getProductOptionList().add(saveProductOption);
        productRepository.save(saveProduct);
        //when
        ProductDetailResponse productDetail = productService.getProductDetail(saveProduct.getProductId());
        //then
        Assertions.assertThat(productDetail.productName()).isEqualTo(saveProduct.getProductName());
        Assertions.assertThat(productDetail.productOption()).hasSizeGreaterThan(0);
    }
}
```

테스트 코드를 작성하면서 실패 케이스를 하나씩 작성하면서 코드를 더 단단하게 만들어 가는 것은 항상 즐겁다. 실패 케이스를 최대한 고려하면 좋지만 현재 코드는 크게 어려울 것이 없기 때문에 다음과 같이 작성했다. 그리고 마지막으로 성공하는 케이스를 작성했다.

이렇게 성공 케이스까지 모두 작성하면 테스트가 종료된다. 이제 이 코드는 개발자가 예상하는 에러를 뱉을 수 있고 개발자가 원하는 데이터를 반환하는 서비스 로직이 되었다.

![](https://velog.velcdn.com/images/ililil9482/post/820192ff-4933-4335-b555-e72e2268cb20/image.png)

그리고 전체 테스트를 실행하여 사이드 이펙트가 없을지 확인해보자. 테스트까지 통과하면 서비스 로직은 모두 끝났다!

## 🟠 Controller 추가

### 🟢 Controller 작성

```java
@ApiController
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product API Documentation")
public class ProductController {
    private final ProductService productService;

    ...

    @GetMapping("/product/{product_id}")
    @Operation(summary = "상품 상세", description = "상품 상세 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProductListResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public ResponseEntity<ResponseData<ProductDetailResponse>> getProductDetail(@Schema(description = "상품 번호", defaultValue = "1", nullable = true) @PathVariable(name = "product_id") Long productId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseData.from(ResponseDataCode.SUCCESS, productService.getProductDetail(productId)));
    }
}
```

Controller를 작성하고 테스트를 해보자

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

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        productOptionRepository.deleteAll();
    }

    ...

    @Test
    @DisplayName("[GET] /api/v1/product/{productId} 유효하지 않은 상품은 실패한다.")
    void getProductDetailFailByProductId() throws Exception{
        //given
        final Long NOT_EXISTS_PRODUCT_ID = 0L;
        //when
        ResultActions perform = mockMvc.perform(get("/api/v1/product/{productId}", NOT_EXISTS_PRODUCT_ID));
        //then
        perform.andExpect(status().is4xxClientError());
        perform.andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8).contains("유효하지 않은");
    }


    @Test
    @DisplayName("[GET] /api/v1/product/{productId}")
    void getProductDetail() throws Exception{
        //given
        PostProductRequest productRequest = PostProductRequest.builder()
                .productName("등록 상품")
                .productType(ProductType.OPTION)
                .build();
        ProductEntity saveProduct1 = productRepository.save(ProductEntity.from(productRequest));
        PostProductOptionDto option = PostProductOptionDto.builder()
                .productOptionName("11-12과")
                .productOptionPrice(100000)
                .productOptionStatus(ProductOptionStatus.ON_SALE)
                .productOptionStock(100)
                .productOptionDescription("아리수 11-12과 입니다.")
                .productOptionSort(1)
                .build();
        ProductOptionEntity saveProductOption = productOptionRepository.save(ProductOptionEntity.from(option, saveProduct1));
        saveProduct1.getProductOptionList().add(saveProductOption);
        productRepository.save(saveProduct1);
        //when
        ResultActions perform = mockMvc.perform(get("/api/v1/product/{productId}", saveProduct1.getProductId()));
        //then
        perform.andExpect(status().isOk());
    }
}
```

sevice test code를 작성할 때와 동일하게 실패 케이스부터 작성하여 성공 케이스를 작성한다. 여기서 성공케이스에서 http의 상태값만 체크한 이유는 이미 service를 테스트할때 서비스 로직을 테스트 했기 때문에 컨트롤러에서는 반환하는 값만 체크하려고 한것이기 때문이다.

마지막으로 swagger에서 내가 예상한 시나리오 대로 동작할지 확인해보자.

```json
{
  "type": "/swagger-ui/index.html",
  "title": "Bad Request",
  "status": 400,
  "detail": "Bad Request",
  "instance": "/api/v1/product/1",
  "errors": [
    {
      "detail": "",
      "message": "유효하지 않은 상품입니다."
    }
  ]
}
```
상품을 등록하지 않았을때 다음과 같이 반환되고 있다.

```json
{
  "code": "0000",
  "message": "success",
  "data": {
    "product_id": 1,
    "product_name": "아리수",
    "product_type": "OPTION",
    "product_status": "ON_SALE",
    "product_option": [
      {
        "product_option_id": 1,
        "product_option_name": "11-12과",
        "product_option_sort": 1,
        "product_option_price": 100000,
        "product_option_stock": 100,
        "product_option_status": "ON_SALE",
        "product_option_description": "아리수 11-12과 입니다."
      }
    ]
  }
}
```

상품을 등록 후 다음과 같이 정상적으로 반환이 된다. 기능 추가가 완료되었다.

정말 마지막으로 build까지 해보자! build를 통해 전체 테스트를 돌려보고 jacoco를 확인해볼 수 있기 때문에 build를 실행한다!

![](https://velog.velcdn.com/images/ililil9482/post/e991a55e-46fe-4a3b-be0f-88ab1ea5f320/image.png)

정말 끝났다!

## 🟠 PR 후 코드리뷰

이제 git에 pr을 올려서 마무리해보자!

![](https://velog.velcdn.com/images/ililil9482/post/eb93b557-f8bc-4186-b246-ccca852c12a8/image.png)

근데 아까 로직 중에 n+1 발생하는 문제로 인해 pr이 거부당하고 해당 부분을 해결해달라고 리뷰를 받았다... 이걸 다시 해결해보자!

### 🟢 querydsl로 n+1 해결

```java
public interface ProductCustomRepository {
    Page<ProductEntity> findAll(GetProductListRequest getProductListRequest);

    Optional<ProductEntity> findById(Long productId);
}
```

`ProductCustomRepository`에 `findById()`를 작성하고

```java
@Repository
@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements ProductCustomRepository {
    private final JPAQueryFactory querydsl;

    ...

    @Override
    public Optional<ProductEntity> findById(Long productId) {
        QProductEntity productEntity = QProductEntity.productEntity;
        QProductOptionEntity productOptionEntity = QProductOptionEntity.productOptionEntity;

        ProductEntity result = querydsl.selectFrom(productEntity)
                .join(productEntity.productOptionList, productOptionEntity)
                .fetchJoin()
                .where(productEntity.productId.eq(productId))
                .fetchOne();
        return Optional.ofNullable(result);
    }
}
```

구현체도 작성해준다. 참고로 `fetchOne()`에서는 join에 on 조건을 설정하지 못하도록 되어 있다. JPA 자체에서 그렇게 되도록 해놨다고 하는데 자세하게는 모르겠다. on 조건에 들어갈 filter 조건이 있다면 where로 작성하도록 가이드하고 있다.

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductCustomRepository productCustomRepository;
    private final ProductOptionRepository productOptionRepository;
    
    ...

    @Override
    public ProductDetailResponse getProductDetail(Long productId) {
        ProductEntity productEntity = productCustomRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 상품입니다."));
        return ProductDetailResponse.from(productEntity);
    }

}
```

그리고 실제 서비스에는 `productRepository` -> `productCustomRepository` 로 변경만 해주면 끝이다. 나머지는 테스트 코드를 실행시켜 검증하면 끝이다!

![](https://velog.velcdn.com/images/ililil9482/post/e1aef224-1a88-4f42-88a0-8e44dbc5bd3f/image.png)

모든 테스트를 통과했다. 기존에 내가 작성했던 서비스의 동작을 그대로 처리하고 있다는 증거다.

![](https://velog.velcdn.com/images/ililil9482/post/02bb15b8-0b02-4990-bc8a-08e3c5d00140/image.png)

n+1이 발생하던 select문도 join을 통해 1번만 쿼리가 발생하도록 처리되었다.