# 🔴 Product Option 추가 

상품을 만들때 옵션을 고려하지 않으려고 했는데 옵션을 추가해달라는 요구사항이 들어와서 구조를 변경해보려고 한다.

## 🟠 Product 데이터 정리 및 Option 데이터 추가

### 🟢 Product 데이터 정리
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

    @Enumerated(EnumType.STRING)
    private ProductType productType;
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOptionEntity> productOptionList;

    public void update(PutProductRequest putProductRequest) {
        this.productName = putProductRequest.getProductName();
        this.productType = ProductType.OPTION;
        this.productStatus = putProductRequest.getProductStatus();
    }
}

```

먼저 `Product` 데이터를 정리했다. 해당 부분을 정리하며 Dto, Vo도 수정이 되었는데 해당 부분은 테스트 코드를 실행하면 모두 나오기 때문에 따로 적어두진 않겠다.

그리고 여기에 연관관계가 추가되어 `productOptionList` 값이 추가되었다.

### 🟢 Product Option 추가

```java
@Entity
@Table(name = "product_option")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class ProductOptionEntity extends CommonEntity {
    @Id
    private Long optionId;
    private int optionSort;
    private String optionName;
    private int optionPrice;
    private int optionStock;
    @Enumerated(EnumType.STRING)
    private OptionStatus optionStatus;
    private String optionDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;
}
```

ProductOption은 다음과 같이 추가되었다.

## 🟠 상품 등록 리팩토링

### 🟢 Request수정

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

    @NotNull(message = "상품 타입을 입력해 주세요. ex) OPTION")
    @JsonProperty("product_type")
    @Schema(description = "상품 타입", example = "OPTION")
    private ProductType productType;
    @NotNull(message = "상품 옵션을 입력해주세요.")
    @JsonProperty("product_option")
    @Schema(description = "상품 옵션")
    private List<PostProductOptionDto> productOption;

}
```
등록시 `productOption` 값을 추가하여 상품 옵션도 입력 받도록 수정하고

```java
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "상품명 옵션")
public class PostProductOptionDto {
    @JsonProperty("option_name")
    @NotNull(message = "옵션명을 입력해주세요.")
    @Schema(description = "옵션명", example = "11-12과")
    private String optionName;
    @JsonProperty("option_sort")
    @Schema(description = "옵션 정렬 순서 (미입력시 가장 뒤로 자동 처리)", example = "1")
    private int optionSort;
    @JsonProperty("option_price")
    @NotNull(message = "옵션 가격을 입력해주세요.")
    @Schema(description = "옵션 가격", example = "100000")
    private int optionPrice;
    @JsonProperty("option_stock")
    @NotNull(message = "옵션 재고를 입력해주세요.")
    @Schema(description = "옵션 재고", example = "100")
    private int optionStock;
    @JsonProperty("option_status")
    @NotNull(message = "옵션 상태를 입력해주세요.")
    @Schema(description = "옵션 상태", example = "ON_SALE")
    private OptionStatus optionStatus;
    @JsonProperty("option_description")
    @NotNull(message = "옵션 설명을 입력해주세요.")
    @Schema(description = "옵션 설명", example = "아리수 11-12과 입니다.")
    private String optionDescription;
}
```
입력 받을 옵션 dto도 정의했다.

### 🟢 Service 수정

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductCustomRepository productCustomRepository;
    private final ProductOptionRepository productOptionRepository;

    ...

    @Transactional
    @Override
    public PostProductResponse createProduct(PostProductRequest postProductRequest) {
        ProductEntity saveProduct = productRepository.save(ProductEntity.from(postProductRequest));
        // saveProduct에 option list 추가하기
        List<ProductOptionEntity> productOptionList = postProductRequest.getProductOption().stream()
                .map(f -> ProductOptionEntity.from(f, saveProduct))
                .collect(Collectors.toList());
        productOptionRepository.saveAll(productOptionList);

        return PostProductResponse.createFrom(saveProduct);
    }

}
```

상품을 등록하는 부분에서 옵션도 함께 등록되도록 수정한다. 그 외에도 소스가 좀 수정되었는데 자세한 내용은 깃 커밋으로 확인하면 좋을것 같다. 테스트 코드와 Entity쪽도 모두 수정이 일어나 이 글에 다 적기는 너무 많더라...ㅜㅜ

## 🟠 상품 수정 리팩토링

수정할때도 옵션 내용이 변경되어야 하기 때문에 리팩토링해야한다. 근데 생각해보니 옵션쪽은 좀 복잡해질거 같다...ㅜ

### 🟢 Request 수정

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
    @NotNull(message = "상품 타입을 입력해 주세요. ex) OPTION")
    @JsonProperty("product_type")
    @Schema(description = "상품 타입", example = "OPTION")
    private ProductType productType;
    @NotNull(message = "상품 상태를 입력해 주세요.")
    @JsonProperty("product_status")
    @Schema(description = "상품 상태", example = "SOLD_OUT")
    private ProductStatus productStatus;
    @NotNull(message = "상품 옵션은 1개 이상 등록되어야 합니다.")
    @JsonProperty("product_option")
    @Schema(description = "상품 옵션")
    private List<PutProductOptionDto> productOption;
}
```

등록과 동일하게 수정도 Dto를 추가해주자

```java
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "상품명 옵션 (수정)")
public class PutProductOptionDto {
    @JsonProperty("product_option_id")
    @Schema(description = "옵션 id (비어있을 경우 새롭게 추가)", example = "1")
    private Long productOptionId;
    @JsonProperty("product_option_name")
    @NotNull(message = "옵션명을 입력해주세요.")
    @Schema(description = "옵션명", example = "11-12과")
    private String productOptionName;
    @JsonProperty("product_option_sort")
    @Schema(description = "옵션 정렬 순서 (미입력시 가장 뒤로 자동 처리)", example = "1")
    private int productOptionSort;
    @JsonProperty("product_option_price")
    @NotNull(message = "옵션 가격을 입력해주세요.")
    @Schema(description = "옵션 가격", example = "100000")
    private int productOptionPrice;
    @JsonProperty("product_option_stock")
    @NotNull(message = "옵션 재고를 입력해주세요.")
    @Schema(description = "옵션 재고", example = "100")
    private int productOptionStock;
    @JsonProperty("product_option_status")
    @NotNull(message = "옵션 상태를 입력해주세요.")
    @Schema(description = "옵션 상태", example = "ON_SALE")
    private ProductOptionStatus productOptionStatus;
    @JsonProperty("product_option_description")
    @NotNull(message = "옵션 설명을 입력해주세요.")
    @Schema(description = "옵션 설명", example = "아리수 11-12과 입니다.")
    private String productOptionDescription;
}
```

그리고 ProductEntity의 update() 메서드를 수정해준다.

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

    @Enumerated(EnumType.STRING)
    private ProductType productType;
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOptionEntity> productOptionList;

    ...

    public void update(PutProductRequest putProductRequest) {
        this.productName = putProductRequest.getProductName();
        this.productType = ProductType.OPTION;
        this.productStatus = putProductRequest.getProductStatus();

        // product option 데이터 처리
        List<ProductOptionEntity> newProductOptionList = putProductRequest.getProductOption().stream()
                .map(f -> ProductOptionEntity.from(f, this))
                .collect(Collectors.toList());
        newProductOptionList.stream().forEach(f -> f.updateCreateAt(this.productOptionList));
        this.productOptionList.clear();
        this.productOptionList.addAll(newProductOptionList);
    }
}
```

여기서 처리된 부분은 option을 처리할때 새로 수정,삭제,등록에 대해 간단하게 처리하고 싶었다. 그 방법 중 첫번째로

```java
public class ProductEntity extends CommonEntity {
    // 잘못된 케이스 예시
    ...
    
    public void update(PutProductRequest putProductRequest) {
        this.productName = putProductRequest.getProductName();
        this.productType = ProductType.OPTION;
        this.productStatus = putProductRequest.getProductStatus();

        // product option 데이터 처리
        List<ProductOptionEntity> newProductOptionList = putProductRequest.getProductOption().stream()
                .map(f -> ProductOptionEntity.from(f, this))
                .collect(Collectors.toList());
        this.productOptionList.clear();
        this.productOptionList.addAll(newProductOptionList);
    }
}
```
list를 clear()하고 들어오는 optionList를 모두 등록해주는 방식으로 처리를 했었는데 여기서 문제가 기존에 있던 데이터가 수정될때 create_at 데이터가 null로 바껴버리는 것이다.

그 이유는 ProductOptionEntity.from()에서 새롭게 데이터를 반환할때 create_at 값이 null이고 자동으로 처리해둔 create_at 입력이 등록이 아닌 수정이기 때문에 @PrePersist가 작동하지 않기 때문에 null로 입력된걸로 보인다.

해당 문제 해결을 위해 list를 만들때 기존 데이터와 비교하여 기존의 create_at 값을 넣어주록 했다.

```java
@Entity
@Table(name = "product_option")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class ProductOptionEntity extends CommonEntity {
    ...

    public void updateCreateAt(List<ProductOptionEntity> productOptionList) {
        Long targetProductOptionId = this.productOptionId;
        if (targetProductOptionId == null) {
            return ;
        }

        Optional<ProductOptionEntity> findProductOption = productOptionList.stream()
                .filter(f -> f.getProductOptionId().equals(targetProductOptionId))
                .findFirst();

        if (! findProductOption.isPresent()) {
            throw new RuntimeException("해당 상품에 존재하지 않는 옵션 id가 입력되었습니다. proudct_option_id = %d".formatted(targetProductOptionId));
        }

        this.createdAt = findProductOption.get().createdAt;
    }
}
```

다음과 같이 도메인에 체크하는 로직을 추가하여 create_at 값이 이전 값으로 유지되도록 처리했다.

이 방법이 좋은 방법인지는 모르겠지만 우선 등록,수정,삭제시 하나의 로직으로 모두 처리가 가능하여 편하게 사용할 수 있다.

이제 테스트 코드를 수정을 다 하고 빌드를 눌러보면

![](https://velog.velcdn.com/images/ililil9482/post/17d810f0-ee8d-404e-8ea7-d7a9b092b1fb/image.png)


두둥 jacoco에서 터져버렸다. 새롭게 만든 update()를 테스트하지 않아서이다.

domain에 기능을 넣으면 좋은점은 unit test code가 자연스럽게 작성된다는 점인데 다음과 같이 테스트를 도메인의 기능별로 테스트할 수 있어 통합 테스트가 필요 없고 순수 자바로만 테스트가 가능하여 엄청 빠르고 가볍다.

### 🟢 unit Test 작성

```java
class ProductOptionEntityTest {

    @Test
    @DisplayName("product option id 값이 존재하지 않으면 실패한다.")
    void updateFailByProductOptionId() {
        //given
        ProductOptionEntity originProductOptionEntity = ProductOptionEntity.builder()
                .productOptionId(1L)
                .productOptionName("11-12과")
                .productOptionPrice(100000)
                .productOptionStock(100)
                .productOptionStatus(ProductOptionStatus.ON_SALE)
                .build();

        // 기존 productOptionId 값은 1인데 2로 요청된 경우임
        ProductOptionEntity requestProductOptionEntity = ProductOptionEntity.builder()
                .productOptionId(2L)
                .productOptionName("11-12과")
                .productOptionPrice(100000)
                .productOptionStock(100)
                .productOptionStatus(ProductOptionStatus.ON_SALE)
                .build();

        //when
        //then
        Assertions.assertThatExceptionOfType(RuntimeException.class)
                        .isThrownBy(() -> requestProductOptionEntity.updateCreateAt(List.of(originProductOptionEntity)))
                .withMessageContaining("해당 상품에 존재하지 않는 옵션 id가 입력되었습니다.");
    }

    @Test
    @DisplayName("product option 수정 성공")
    void updateSuccess() {
        //given
        ProductOptionEntity originProductOptionEntity = ProductOptionEntity.builder()
                .productOptionId(1L)
                .productOptionName("11-12과")
                .productOptionPrice(100000)
                .productOptionStock(100)
                .productOptionStatus(ProductOptionStatus.ON_SALE)
                .build();

        // 기존 productOptionId 값은 1인데 2로 요청된 경우임
        ProductOptionEntity requestProductOptionEntity = ProductOptionEntity.builder()
                .productOptionId(1L)
                .productOptionName("11-12과")
                .productOptionPrice(100000)
                .productOptionStock(100)
                .productOptionStatus(ProductOptionStatus.SOLD_OUT)
                .build();

        //when
        requestProductOptionEntity.updateCreateAt(List.of(originProductOptionEntity));
        //then
        Assertions.assertThat(requestProductOptionEntity.getProductOptionStatus()).isEqualTo(ProductOptionStatus.SOLD_OUT);
    }
}
```

다음과 같이 unit test code를 작성해준다. 이제 build를 하면

![](https://velog.velcdn.com/images/ililil9482/post/e53d4620-620a-418b-b95b-86933f8f3a15/image.png)

빌드를 성공하는걸 확인할 수 있다!