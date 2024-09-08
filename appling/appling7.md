# 🔴 Domain

## 🟠 jacoco 수정

```kotlin
tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			enabled = true
			element = "CLASS"

			// 라인 커버리지를 최소한 80%
			limit {
				counter = "LINE"
				value = "COVEREDRATIO"
				minimum = "1.00".toBigDecimal()
			}

			// 브랜치 커버리지를 최소한 90%
			limit {
				counter = "BRANCH"
				value = "COVEREDRATIO"
				minimum = "1.00".toBigDecimal()
			}

			// 빈 줄을 제외한 코드의 라인수를 최대 200라인으로 제한합니다.
			limit {
				counter = "LINE"
				value = "TOTALCOUNT"
				maximum = "200".toBigDecimal()
			}

			excludes = listOf(
				"*.ApplingApplication*"
				, "*.global.*"
			)
		}
	}
}
```
기존에 `*.domain.*` 부분을 제거하고 jacoco에서 테스트 되도록 수정했다.

## 🟠 doamin 테스트를 하는 이유

domain 테스트를 진행하는 이유는 domain에 서비스 로직들이 있기 때문이다. 서비스 로직을 서비스에 두지 않고 도메인으로 뺀 이유는 또 뭘까?

도메인에 서비스 로직을 넣어두면 장점으로는
1. 도메인을 가지고 로직을 짤때 재활용성이 매우 높다.
2. 도메인에는 db나 api 통신이 없기 때문에 도메인에 있는 로직의 경우 도메인 자체로 테스트가 가능하다.
3. 통합 테스트나 인수 테스트가 필요 없이 유닛 테스트로만 테스트가 가능하여 굉장히 빠르고 가볍다.

이런 장점들이 있기 때문에 domain에 로직을 두는 것이 좋다고 생각하는 편이고 그렇게 짜려고 한다.

### 🟢 테스트 하려는 도메인

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

이전에 작성했던 ProductEntity의 update()를 테스트 하려고 한다.

### 🟢 Test

```java
class ProductEntityTest {

    @Test
    @DisplayName("상품 업데이트 성공")
    void update() {
        //given
        ProductEntity productEntity = ProductEntity.builder()
                .productId(1L)
                .productName("아리수")
                .productWeight(5)
                .productType("11과")
                .productPrice(100_000)
                .productStock(100)
                .productStatus(ProductStatus.ON_SALE)
                .build();

        PutProductRequest putProductRequest = PutProductRequest.builder()
                .productId(1L)
                .productName("아리수")
                .productWeight(5)
                .productType("11과")
                .productPrice(100_000)
                .productStock(100)
                .productStatus(ProductStatus.SOLD_OUT)
                .build();

        //when
        productEntity.update(putProductRequest);

        //then
        Assertions.assertThat(productEntity.getProductStatus()).isEqualTo(ProductStatus.SOLD_OUT);
    }
}
```

해당 로직에 따로 실패하는 경우는 처리하지 않아 성공하는 테스트만 작성했다.

원래 테스트 코드는 실패 케이스가 많을 수록 좋지만 이번 글은 테스트 코드에 관한 글 보다는 도메인을 통해 유닛 테스트를 진행하는 것을 학습하려는 내용이므로 간단하게 작성하자!