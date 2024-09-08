# 🔴 Product 상품 등록

![](https://velog.velcdn.com/images/ililil9482/post/cb9b2d9d-4d1f-4490-aed1-6df977068f9b/image.png)

상품은 다음과 같이 domain을 작성하였었다. 이 설계에 맞춰서 상품을 작성해보자.

## 🟠 도메인 정리

### 🟢 Entity

```
@MappedSuperclass
public class CommonEntity {
    protected LocalDateTime createdAt;
    protected LocalDateTime modifiedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }
}
```

createdAt, modifiedAt의 경우 모든 Entity가 동일하게 가져갈 구조라 해당 값은 공통으로 처리되도록 CommonEntity class를 정의했다.

수정일도 자동으로 업데이트 하도록 할수 있는데 가끔 수정일을 건들지 않고 싶을때도 자동으로 처리되어 수동으로 업데이트 시키기 위해 해당 설정은 따로 적용하지 않았다.

```
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
    private ProductStatus productStatus;
    private int productPrice;
    private int productStock;
}
```

먼저 Entity를 정의했다. 컬럼별로 설정을 더 줄수 있지만 우선은 간단하게 했다.

### 🟢 Request

```
public enum ProductStatus {
    ON_SALE,
    SOLD_OUT,
    OFF_SALE,
}
```

상품의 상태를 enum으로 정의했다.

```
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostProductRequest {

    @NotNull(message = "상품명을 입력해 주세요.")
    @JsonProperty("product_name")
    private String productName;
    @NotNull(message = "상품 무게를 입력해 주세요.")
    @JsonProperty("product_weight")
    private int productWeight;
    @NotNull(message = "상품 타입을 입력해 주세요. ex) 사과는 11과")
    @JsonProperty("product_type")
    private String productType;
    @NotNull(message = "상품 가격을 입력해 주세요.")
    @JsonProperty("product_price")
    private int productPrice;
    @NotNull(message = "상품 수량을 입력해 주세요.")
    @JsonProperty("product_stock")
    private int productStock;

    public ProductEntity toProductEntity() {
        return ProductEntity.builder()
                .productName(productName)
                .productWeight(productWeight)
                .productType(productType)
                .productPrice(productPrice)
                .productStock(productStock)
                .productStatus(ProductStatus.ON_SALE)
                .build();
    }
}
```

상품 등록시 Client에서 요청할 Request를 모델링 해주었다.

### 🟢 Response

```
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public record PostProductResponse(
    Long productId
) {
    public static PostProductResponse createFrom(ProductEntity saveProduct) {
        return PostProductResponse.builder()
                .productId(saveProduct.getProductId())
                .build();
    }
}
```

상품 등록 후 반환할 Response도 추가해주었다.

### 🟢 Repository

```
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
```

repository도 추가해주자

## 🟠 서비스 로직 정리

### 🟢 Service

```
public interface ProductService {
    PostProductResponse createProduct(PostProductRequest postProductRequest);
}
```

service를 interface로 작성한다. 요즘엔 따로 interface를 작성하지 않아도 spring에서 자동으로 proxy 처리를 해주고 있지만 기능 정의서처럼 interface를 하나 더 두는것이 다른 개발자가 기능을 분석하고 읽기에 좋아 interface를 작성하는 편이다.

```
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    @Transactional
    @Override
    public PostProductResponse createProduct(PostProductRequest postProductRequest) {
        ProductEntity saveProduct = productRepository.save(postProductRequest.toProductEntity());
        return PostProductResponse.createFrom(saveProduct);
    }

}
```

이제 서비스를 정의했다. 서비스는 단순하게 request 객체를 가져와 request가 entity로 치환되고 entity를 그대로 저장한 후 response 객체로 만들어서 반환하는 방식이다.

### 🟢 Service Test

```
@SpringBootTest
class ProductServiceImplTest {
    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품 등록에 성공한다.")
    void createProduct() {
        //given
        PostProductRequest productRequest = PostProductRequest.builder()
                .productName("아리수")
                .productWeight(5)
                .productType("11과")
                .productPrice(100_000)
                .productStock(100)
                .build();
        //when
        PostProductResponse product = productService.createProduct(productRequest);
        //then
        Assertions.assertThat(product.productId()).isNotNull();
    }
}
```

서비스를 작성하며 함께 테스트 코드를 작성했다. Request를 받을 때 이미 대부분 처리를 해두어서 따로 실패하는 케이스는 작성하지 않았다.

![](https://velog.velcdn.com/images/ililil9482/post/dc3d027a-d665-475c-80d3-a1723aec823b/image.png)

테스트 코드가 성공하며 서비스 로직 정의가 끝났다!