# 🔴 주문 만들기

## 🟠 주문 도메인 정의

```java
@Entity
@Table(name = "orders")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class OrderEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private String orderName;
    private String orderContact;
    private String orderAddress;
    private String orderAddressDetail;
    private String orderZipcode;
    private String recipientName;
    private String recipientContact;
    private String recipientAddress;
    private String recipientAddressDetail;
    private String recipientZipcode;
    private int orderAmount;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderProductEntity> orderProductList;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDeliveryEntity> orderDeliveryEntityList;
}

```

```java
@Entity
@Table(name = "order_product")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class OrderProductEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderProductId;
    private String orderProductName;
    private String orderProductOptionName;
    private int quantity;
    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "product_option_id")
    private ProductOptionEntity productOption;
}
```

```java
@Entity
@Table(name = "order_delivery")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class OrderDeliveryEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderDeliveryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @Enumerated(EnumType.STRING)
    private OrderDeliveryStatus orderDeliveryStatus;

    private String invoice;
}

```

```java
public enum OrderStatus {
    TEMP,
    COMPLETE,
}
```

```java
public enum OrderDeliveryStatus {
    DELIVERY,
    COMPLETE,
}
```

주문과 관련한 Entity와 Enum을 정의했다.

## 🟠 주문 서비스 로직

### 🟢 주문 요청과 반환

```java
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostOrderRequest {
    // 주문 상품을 dto로 리스트로 받아야 함.
    @NotNull(message = "주문 상품 리스트를 입력해주세요.")
    @Size(min = 1, max = 10, message = "1~10개의 주문 상품를 입력해세요.")
    @JsonProperty("order_product_list")
    @Schema(description = "주문 상품 리스트", example = "주문 상품 리스트")
    private List<PostOrderDto> orderProductList;

    @NotNull(message = "주문 상품 개수를 입력해주세요.")
    @JsonProperty("quantity")
    @Schema(description = "주문자 이름", example = "주문자")
    private String orderName;

    @NotNull
    @JsonProperty("order_contact")
    @Schema(description = "주문자 연락처", example = "010-1234-5678")
    private String orderContact;

    @NotNull
    @JsonProperty("order_address")
    @Schema(description = "주문자 주소", example = "경기도 성남시 분당구 판교역로 231")
    private String orderAddress;

    @NotNull
    @JsonProperty("order_address_detail")
    @Schema(description = "주문자 상세 주소", example = "H스퀘어 S동 5층")
    private String orderAddressDetail;

    @NotNull
    @JsonProperty("order_zipcode")
    @Schema(description = "주문자 우편주소", example = "12345")
    private String orderZipcode;

    @NotNull
    @JsonProperty("recipient_name")
    @Schema(description = "받는사람 이름", example = "받는이")
    private String recipientName;

    @NotNull
    @JsonProperty("recipient_contact")
    @Schema(description = "받는사람 연락처", example = "010-1234-5678")
    private String recipientContact;

    @NotNull
    @JsonProperty("recipient_address")
    @Schema(description = "받는사람 주소", example = "경기도 성남시 분당구 판교역로 231")
    private String recipientAddress;

    @NotNull
    @JsonProperty("recipient_address_detail")
    @Schema(description = "받는사람 상세 주소", example = "H스퀘어 S동 6층")
    private String recipientAddressDetail;

    @NotNull
    @JsonProperty("recipient_zipcode")
    @Schema(description = "받는사람 우편주소", example = "12345")
    private String recipientZipcode;
}
```

```java
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostOrderDto {
    // todo 주문 상품을 dto로 리스트로 받아야 함.
    @NotNull(message = "주문 상품 옵션 id를 입력해주세요.")
    @JsonProperty("product_option_id")
    @Schema(description = "상품 옵션 번호", example = "1")
    private Long productOptionId;

    @NotNull(message = "주문 상품 개수를 입력해주세요.")
    @JsonProperty("quantity")
    @Schema(description = "주문 상품 개수", example = "2")
    private int quantity;
}
```

요청에 대한 정의고

```java
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
@Schema(description = "주문 등록 반환 데이터")
public record PostOrderResponse (
        @Schema(description = "주문번호", example = "1")
        Long orderId
){
        public static PostOrderResponse from(OrderEntity orderEntity) {
            return PostOrderResponse.builder()
                .orderId(orderEntity.getOrderId())
                .build();
        }
}

```

반환에 대한 정의를 했다.

여기서 요청에 대한 정의를 하다가 보니 controller에서 반환 부분을 리팩토링하게 되었다.

기존에 사용하던 `ResponseData`를 수정하였는데

```java
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ResponseData<T>(
    String code,
    String message,
    T data
) {
    public static ResponseData from(ResponseDataCode responseDataCode, Object data) {
        return ResponseData.<Object>builder()
                .code(responseDataCode.code)
                .message(responseDataCode.message)
                .data(data)
                .build();
    }
}
```
Object로 받아서 사용하고 있기 때문에 타입에 대한 정의가 필요했고

```java
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ResponseData<T>(
    String code,
    String message,
    T data
) {
    public static <T> ResponseData<T> from(ResponseDataCode responseDataCode, T data) {
        return ResponseData.<T>builder()
                .code(responseDataCode.code)
                .message(responseDataCode.message)
                .data(data)
                .build();
    }
}
```

다음과 같이 리팩토링하여 사용하였다.

### 🟢 주문 생성 로직

```java
public interface OrderService {
    PostOrderResponse createOrder(PostOrderRequest postOrderRequest);
}
```

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductOptionRepository productOptionRepository;
    
    @Transactional
    @Override
    public PostOrderResponse createOrder(PostOrderRequest postOrderRequest) {
        // order 데이터 생성
        OrderEntity order = OrderEntity.from(postOrderRequest);

        // orderProduct 데이터 생성
        List<OrderProductEntity> orderProductEntityList = getOrderProductEntityList(postOrderRequest, order);
        // order product 추가 및 총 비용 계산
        order.calculatorTotalAmount(orderProductEntityList);
        order.updateOrderProductList(orderProductEntityList);

        OrderEntity saveOrderEntity = orderRepository.save(order);

        return PostOrderResponse.from(saveOrderEntity);
    }

    /**
     * orderProductEntityList 생성
     * @param postOrderRequest
     * @param order
     * @return
     */
    private List<OrderProductEntity> getOrderProductEntityList(PostOrderRequest postOrderRequest, OrderEntity order) {
        List<OrderProductEntity> orderProductEntityList = new ArrayList<>();
        List<PostOrderDto> orderProductList = postOrderRequest.getOrderProductList();
        for (PostOrderDto dto : orderProductList) {
            ProductOptionEntity productOptionEntity = productOptionRepository.findById(dto.getProductOptionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

            OrderProductEntity orderProductEntity = OrderProductEntity.from(dto, order, productOptionEntity);
            orderProductEntityList.add(orderProductEntity);
        }
        return orderProductEntityList;
    }
}
```

서비스 로직을 다음과 같이 정의했고 주문 리스트에 상품을 한개씩 조회하고 있는데 해당 부분은 리팩토링이 필요해보인다.


![](https://velog.velcdn.com/images/ililil9482/post/00be0fd7-8fac-4c12-b5a3-8a89231771fa/image.png)

이미 상품 옵션과 상품을 조회하면서 n+1 문제가 발생하고 있다. 여기에 여러 옵션을 주문한다면

`주문옵션수 * n+1` 만큼 문제가 발생한다.

이를 해결하기 위해서는 상품 옵션을 조회하는 부분을 리팩토링 해야한다.

### 🟢 주문 생성 로직 리팩토링

```java
public interface ProductOptionCustomRepository {
    List<ProductOptionEntity> findAllByProductOptionId(List<Long> idList);
}
```

```java
@Repository
@RequiredArgsConstructor
public class ProductOptionCustomRepositoryImpl implements ProductOptionCustomRepository {
    private final JPAQueryFactory querydsl;

    @Override
    public List<ProductOptionEntity> findAllByProductOptionId(List<Long> idList) {
        QProductOptionEntity productOption = QProductOptionEntity.productOptionEntity;
        QProductEntity product = QProductEntity.productEntity;

        List<ProductOptionEntity> fetch = querydsl.selectFrom(productOption)
                .join(productOption.product, product)
                .fetchJoin()
                .where(productOption.productOptionId.in(idList))
                .fetch();
        return fetch;
    }
}
```

상품 조회를 할때 product option id 값을 받아와서 한번에 조회하도록 하고 product를 바로 join하여 n+1 문제를 해결하려 한다.

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductOptionCustomRepository productOptionCustomRepository;
    
    @Transactional
    @Override
    public PostOrderResponse createOrder(PostOrderRequest postOrderRequest) {
        // order 데이터 생성
        OrderEntity order = OrderEntity.from(postOrderRequest);

        // orderProduct 데이터 생성
        List<OrderProductEntity> orderProductEntityList = getOrderProductEntityList(postOrderRequest, order);
        // order product 추가 및 총 비용 계산
        order.calculatorTotalAmount(orderProductEntityList);
        order.updateOrderProductList(orderProductEntityList);

        OrderEntity saveOrderEntity = orderRepository.save(order);

        return PostOrderResponse.from(saveOrderEntity);
    }

    /**
     * orderProductEntityList 생성
     * @param postOrderRequest
     * @param order
     * @return
     */
    private List<OrderProductEntity> getOrderProductEntityList(PostOrderRequest postOrderRequest, OrderEntity order) {
        List<OrderProductEntity> orderProductEntityList = new ArrayList<>();
        List<PostOrderDto> orderProductList = postOrderRequest.getOrderProductList();

        List<Long> productOptionIdList = orderProductList.stream().mapToLong(PostOrderDto::getProductOptionId).boxed().toList();

        List<ProductOptionEntity> productOptionEntityList = productOptionCustomRepository.findAllByProductOptionId(productOptionIdList);

        checkOrderValidation(productOptionIdList, productOptionEntityList);

        // orderProductList, productOptionEntityList 모두 optionId 기준으로 정렬하기
        Comparator<PostOrderDto> orderDtoComparator = Comparator.comparing(o -> o.getProductOptionId());
        orderProductList = orderProductList.stream()
                .sorted(orderDtoComparator)
                .collect(Collectors.toList());

        Comparator<ProductOptionEntity> productOptionEntityComparator = Comparator.comparing(o -> o.getProductOptionId());
        productOptionEntityList = productOptionEntityList.stream()
                .sorted(productOptionEntityComparator)
                .collect(Collectors.toList());

        for (int i=0; i<orderProductList.size(); i++) {
            PostOrderDto dto = orderProductList.get(i);
            ProductOptionEntity productOption = productOptionEntityList.get(i);
            OrderProductEntity orderProductEntity = OrderProductEntity.from(dto, order, productOption);
            orderProductEntityList.add(orderProductEntity);
        }

        return orderProductEntityList;
    }

    private void checkOrderValidation(List<Long> productOptionIdList, List<ProductOptionEntity> productOptionEntityList) {
        if (productOptionIdList.size() != productOptionEntityList.size()) {
            throw new IllegalArgumentException("유효하지 않은 상품이 포함되어 있습니다.");
        }
    }
}
```

![](https://velog.velcdn.com/images/ililil9482/post/0e94c1fb-cbd7-4188-b3ca-60c3d3589af0/image.png)

다음과 같이 n+1 문제를 해결하고 여러번 조회가 일어나지 않게되었다.

### 🟢 주문 Test Code

```java
@SpringBootTest
class OrderServiceImplTest {
    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @AfterEach
    void cleanUp() {
        orderRepository.deleteAll();
        productOptionRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("존재하지 않는 상품을 주문시 주문에 실패한다.")
    void createOrderFailByNonExistProduct() {
        //given
        PostOrderDto postOrderDto = PostOrderDto.builder()
                .productOptionId(1L)
                .quantity(1)
                .build();

        PostOrderRequest postOrderRequest = PostOrderRequest.builder()
                .orderProductList(List.of(postOrderDto))
                .build();

        //when
        //then
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                        .isThrownBy(() -> orderService.createOrder(postOrderRequest))
                        .withMessageContaining("유효하지 않은 상품");
    }

    @Test
    @DisplayName("주문에 성공한다.")
    void createOrder() {
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

        ProductEntity productEntity = ProductEntity.from(productRequest);
        ProductOptionEntity productOptionEntity = ProductOptionEntity.from(option, productEntity);
        productEntity.getProductOptionList().add(productOptionEntity);
        ProductEntity saveProduct = productRepository.save(productEntity);

        PostOrderDto postOrderDto = PostOrderDto.builder()
                .productOptionId(saveProduct.getProductOptionList().get(0).getProductOptionId())
                .quantity(1)
                .build();

        PostOrderRequest postOrderRequest = PostOrderRequest.builder()
                .orderProductList(List.of(postOrderDto))
                .orderName("주문자")
                .orderContact("010-1234-5678")
                .orderAddress("경기도 성남시 분당구 판교역로 231")
                .orderAddressDetail("H스퀘어 S동 5층")
                .orderZipcode("12345")
                .recipientName("받는이")
                .recipientContact("010-1234-5678")
                .recipientAddress("경기도 성남시 분당구 판교역로 231")
                .recipientAddressDetail("H스퀘어 S동 6층")
                .recipientZipcode("12345")
                .build();

        //when
        PostOrderResponse order = orderService.createOrder(postOrderRequest);
        //then
        OrderEntity orderEntity = orderRepository.findById(order.orderId()).get();

        Assertions.assertThat(orderEntity.getOrderName()).isEqualTo(postOrderRequest.getOrderName());
    }
}
```

테스트 코드는 리팩토링 전에 작성하여 리팩토링할때 테스트 코드가 잘 작동하는지 확인하였다.

## 🟠 주문 controller

### 🟢 주문 controller 추가
```java
@ApiController
@RequiredArgsConstructor
@Tag(name = "Order", description = "Order API Documentation")
public class OrderController {
    private final OrderService orderService;
    @PostMapping("/order")
    @Operation(summary = "주문 등록", description = "주문 등록 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "정상", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PostProductResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
    })
    public ResponseEntity<ResponseData<PostOrderResponse>> order(@RequestBody @Validated PostOrderRequest postOrderRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.from(ResponseDataCode.CREATE, orderService.createOrder(postOrderRequest)));
    }
}
```

### 🟢 주문 등록 Test Code

```java
@SpringBootTest
@AutoConfigureMockMvc
@Transactional(readOnly = true)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void cleanUp() {
        orderRepository.deleteAll();
        productOptionRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("[POST] /api/v1/order")
    void postOrder() throws Exception {
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

        ProductEntity productEntity = ProductEntity.from(productRequest);
        ProductOptionEntity productOptionEntity = ProductOptionEntity.from(option, productEntity);
        productEntity.getProductOptionList().add(productOptionEntity);
        ProductEntity saveProduct = productRepository.save(productEntity);

        PostOrderDto postOrderDto = PostOrderDto.builder()
                .productOptionId(saveProduct.getProductOptionList().get(0).getProductOptionId())
                .quantity(1)
                .build();

        PostOrderRequest postOrderRequest = PostOrderRequest.builder()
                .orderProductList(List.of(postOrderDto))
                .orderName("주문자")
                .orderContact("010-1234-5678")
                .orderAddress("경기도 성남시 분당구 판교역로 231")
                .orderAddressDetail("H스퀘어 S동 5층")
                .orderZipcode("12345")
                .recipientName("받는이")
                .recipientContact("010-1234-5678")
                .recipientAddress("경기도 성남시 분당구 판교역로 231")
                .recipientAddressDetail("H스퀘어 S동 6층")
                .recipientZipcode("12345")
                .build();
        //when
        ResultActions perform = mockMvc.perform(post("/api/v1/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postOrderRequest)));

        //then
        perform.andExpect(status().isCreated());
    }

}
```