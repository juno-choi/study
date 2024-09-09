# 🔴 Product 상품 수정

## 🟠 도메인 정리

### 🟢 Request

```
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PutProductRequest {
    @JsonProperty("product_id")
    @NotNull(message = "상품 번호를 입력해 주세요.")
    private Long productId;
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
    @NotNull(message = "상품 상태를 입력해 주세요.")
    @JsonProperty("product_status")
    private ProductStatus productStatus;
}
```

수정용 PutProductRequest를 정의했다.

### 🟢 Response

```
@Builder
public record PutProductResponse(
    Long productId,
    String productName,
    int productWeight,
    String productType,
    ProductStatus productStatus,
    int productPrice,
    int productStock
) {
    public static PutProductResponse from(ProductEntity updateProductEntity) {
        return PutProductResponse.builder()
                .productId(updateProductEntity.getProductId())
                .productName(updateProductEntity.getProductName())
                .productWeight(updateProductEntity.getProductWeight())
                .productType(updateProductEntity.getProductType())
                .productStatus(updateProductEntity.getProductStatus())
                .productPrice(updateProductEntity.getProductPrice())
                .productStock(updateProductEntity.getProductStock())
                .build();
    }
}
```

수정용 response인 PutProductResponse도 정의했다.

## 🟠 서비스 정리

### 🟢 Service

```
public interface ProductService {
    ...

    PutProductResponse putProduct(PutProductRequest putProductRequest);
}
```

interface 에 추가로 작성하고

```
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

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

Service에도 추가로 로직을 작성해준다. 여기서 Entity에서 update를 해야되는 상황이 생기는데 해당 부분은 Entity에 정의해주었다.

### 🟢 Entity

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

    public void update(PutProductRequest putProductRequest) {
        this.productName = putProductRequest.getProductName();
        this.productWeight = putProductRequest.getProductWeight();
        this.productType = putProductRequest.getProductType();
        this.productPrice = putProductRequest.getProductPrice();
        this.productStock = putProductRequest.getProductStock();
        this.productStatus = putProductRequest.getProductStatus();
    }
}
```

Entity에 update 메서드를 추가해주었다.

### 🟢 Test

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

    @Test
    @DisplayName("상풍 번호가 유효하지 않으면 수정에 실패한다.")
    void putProductFail() {
        //given
        PutProductRequest putProductRequest = PutProductRequest.builder()
                .productId(0L)
                .productName("아리수")
                .productWeight(5)
                .productType("11과")
                .productPrice(100_000)
                .productStock(100)
                .productStatus(ProductStatus.ON_SALE)
                .build();
        //when
        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> productService.putProduct(putProductRequest))
                        .withMessageContaining("유효하지 않은");
    }

    @Test
    @DisplayName("상풍 수정에 성공한다.")
    void putProduct() {
        //given
        PostProductRequest productRequest = PostProductRequest.builder()
                .productName("아리수")
                .productWeight(5)
                .productType("11과")
                .productPrice(100_000)
                .productStock(100)
                .build();

        ProductEntity saveProduct = productRepository.save(productRequest.toProductEntity());
        PutProductRequest putProductRequest = PutProductRequest.builder()
                .productId(saveProduct.getProductId())
                .productName("아리수")
                .productWeight(5)
                .productType("11과")
                .productPrice(200_000)
                .productStock(100)
                .productStatus(ProductStatus.ON_SALE)
                .build();
        //when
        PutProductResponse putProductResponse = productService.putProduct(putProductRequest);
        //then
        ProductEntity productEntity = productRepository.findById(saveProduct.getProductId()).get();
        Assertions.assertThat(putProductResponse.productPrice()).isEqualTo(200_000);
        Assertions.assertThat(productEntity.getProductPrice()).isEqualTo(200_000);
    }
}
```

마지막으로 실패하는 케이스와 성공하는 케이스를 간단하게 테스트하였다.

![](https://velog.velcdn.com/images/ililil9482/post/48f6a617-203b-47c3-8e41-9e8902573f62/image.png)

### ✅ 프로젝트를 하다가 놓친 부분...;;

테스트 코드를 작성하다 보니 domain 쪽 메서드들을 테스트하지 않고 있다는걸 깨달았다. jacoco 설정을 바꾸고 domain쪽도 unit test code로 작성하여 테스트를 진행할 수 있도록 수정해야겠다.