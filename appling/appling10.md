# 🔴 나머지 Controller 작업

## 🟠 put, get

### 🟢 put

```java
@ApiController
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product API Documentation")
public class ProductController {
    private final ProductService productService;

    ...

    @PutMapping("/product")
    @Operation(summary = "상품 수정", description = "상품 수정 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
    })
    public ResponseEntity<ResponseData<PostProductResponse>> putProduct(@RequestBody @Validated PutProductRequest putProductRequest) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseData.from(ResponseDataCode.SUCCESS, productService.putProduct(putProductRequest)));
    }

}
```

```java
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PutProductRequest {
    @JsonProperty("product_id")
    @NotNull(message = "상품 번호를 입력해 주세요.")
    @Schema(description = "상품 번호", example = "1")
    private Long productId;
    @NotNull(message = "상품명을 입력해 주세요.")
    @JsonProperty("product_name")
    @Schema(description = "상품명", example = "시나노 골드")
    private String productName;
    @NotNull(message = "상품 무게를 입력해 주세요.")
    @JsonProperty("product_weight")
    @Schema(description = "상품 무게", example = "10")
    private int productWeight;
    @NotNull(message = "상품 타입을 입력해 주세요. ex) 사과는 11과")
    @JsonProperty("product_type")
    @Schema(description = "상품 타입", example = "13과")
    private String productType;
    @NotNull(message = "상품 가격을 입력해 주세요.")
    @JsonProperty("product_price")
    @Schema(description = "상품 가격", example = "150000")
    private int productPrice;
    @NotNull(message = "상품 수량을 입력해 주세요.")
    @JsonProperty("product_stock")
    @Schema(description = "상품 수량", example = "0")
    private int productStock;
    @NotNull(message = "상품 상태를 입력해 주세요.")
    @JsonProperty("product_status")
    @Schema(description = "상품 상태", example = "SOLD_OUT")
    private ProductStatus productStatus;

}
```

### 🟢 get

```java
@ApiController
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product API Documentation")
public class ProductController {
    private final ProductService productService;

    ...

    @GetMapping("/product")
    @Operation(summary = "상품리스트", description = "상품리스트 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public ResponseEntity<ResponseData<ProductListResponse>> getProductList(
            @Schema(description = "페이지 크기", defaultValue = "10", nullable = true) @RequestParam(required = false, defaultValue = "10" ) int size,
            @Schema(description = "페이지 번호", defaultValue = "0", nullable = true) @RequestParam(required = false, defaultValue = "0") int page,
            @Schema(description = "페이지 정렬(proudct id 기준)", defaultValue = "DESC", nullable = true) @RequestParam(required = false, defaultValue = "DESC" ) Sort sort,
            @Schema(description = "검색어(아직 기능 개발 안함)", defaultValue = "", nullable = true) @RequestParam(required = false, defaultValue = "") String search) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseData.from(ResponseDataCode.SUCCESS, productService.getProductList(GetProductListRequest.from(size, page, sort, search))));
    }
}
```

## 🟠 Test Code 작성

build를 해보니 jacoco에서 coverage를 충족시키지 못해 터져버린다. controller에 test를 까먹었다. test code를 작성하자!

### 🟢 post

```java
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("/post/product")
    void postProduct() throws Exception {
        //given
        PostProductRequest productRequest = PostProductRequest.builder()
                .productName("등록 상품")
                .productWeight(5)
                .productPrice(10000)
                .productStock(10)
                .productType("11과")
                .build();


        //when
        ResultActions perform = mockMvc.perform(post("/api/v1/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)));

        //then
        perform.andExpect(status().isCreated());
    }
}
```

### 🟢 put

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
    @DisplayName("[PUT] /api/v1/product")
    void putProduct() throws Exception {
        //given
        PostProductRequest productRequest = PostProductRequest.builder()
                .productName("등록 상품")
                .productWeight(5)
                .productPrice(10000)
                .productStock(10)
                .productType("11과")
                .build();
        ProductEntity saveProduct = productRepository.save(productRequest.toProductEntity());

        PutProductRequest putProductRequest = PutProductRequest.builder()
                .productId(saveProduct.getProductId())
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
        perform.andExpect(status().isOk());
    }

}
```

### 🟢 get

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
    @DisplayName("[GET] /api/v1/product")
    void getProduct() throws Exception {
        //given
        PostProductRequest productRequest = PostProductRequest.builder()
                .productName("등록 상품")
                .productWeight(5)
                .productPrice(10000)
                .productStock(10)
                .productType("11과")
                .build();
        ProductEntity saveProduct1 = productRepository.save(productRequest.toProductEntity());
        ProductEntity saveProduct2 = productRepository.save(productRequest.toProductEntity());

        //when
        ResultActions perform = mockMvc.perform(get("/api/v1/product").param("size", "10").param("page", "0").param("sort", "DESC"));

        //then
        perform.andExpect(status().isOk());
    }

}
```

## 🟠 branch coverage

controller를 모두 작성했는데 build에서 또 터지더라 이유를 보니 ProductCustomRepository에서 커버리지가 50프로로 나왔다. 로직을 보니 중간에 조건에 따라 분기되는 곳이 있는데 한쪽 로직만 테스트를 했던 것이다.

해당 부분도 테스트 코드를 추가해주자

### 🟢 로직

```java
@Repository
@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements ProductCustomRepository{
    private final JPAQueryFactory querydsl;

    @Override
    public Page<ProductEntity> findAll(GetProductListRequest getProductListRequest) {
        Pageable pageable = Pageable.ofSize(getProductListRequest.getSize()).withPage(getProductListRequest.getPage());

        Sort sort = getProductListRequest.getSort();

        QProductEntity product = QProductEntity.productEntity;

        List<ProductEntity> fetch = querydsl.selectFrom(product)
                .orderBy(sort == Sort.ASC ? product.productId.asc() : product.productId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = querydsl.selectFrom(product).fetch().stream().count();
        return new PageImpl<>(fetch, pageable, total);
    }
}
```

여기서 orderBy()를 결정할때 분기처리 되어 있는데 해당 부분의 DESC만 테스트를 하고 있었다.

### 🟢 Test

```java
@SpringBootTest
@Transactional(readOnly = true)
class ProductCustomRepositoryImplTest {
    @Autowired
    private ProductCustomRepository productCustomRepository;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("findAll asc 성공")
    void findAllASC() {
        //given
        PostProductRequest productRequest = PostProductRequest.builder()
                .productName("등록 상품")
                .productWeight(5)
                .productPrice(10000)
                .productStock(10)
                .productType("11과")
                .build();

        ProductEntity saveProduct1 = productRepository.save(productRequest.toProductEntity());
        ProductEntity saveProduct2 = productRepository.save(productRequest.toProductEntity());

        //when
        Page<ProductEntity> productPage = productCustomRepository.findAll(GetProductListRequest.from(10, 0, Sort.ASC, ""));

        //then
        Assertions.assertThat(productPage.getContent().get(0).getProductId()).isEqualTo(saveProduct1.getProductId());
    }

    @Test
    @DisplayName("findAll desc 성공")
    void findAllDESC() {
        //given
        PostProductRequest productRequest = PostProductRequest.builder()
                .productName("등록 상품")
                .productWeight(5)
                .productPrice(10000)
                .productStock(10)
                .productType("11과")
                .build();

        ProductEntity saveProduct1 = productRepository.save(productRequest.toProductEntity());
        ProductEntity saveProduct2 = productRepository.save(productRequest.toProductEntity());

        //when
        Page<ProductEntity> productPage = productCustomRepository.findAll(GetProductListRequest.from(10, 0, Sort.DESC, ""));

        //then
        Assertions.assertThat(productPage.getContent().get(0).getProductId()).isEqualTo(saveProduct2.getProductId());
    }

}
```

다른 곳에서 DESC를 테스트하고 있긴 하지만 해당 테스트 코드가 수정되면 결국 여기서 또 터지기 때문에 2개의 케이스로 테스트를 진행했다.

이제 정상적으로 Build가 되었다!