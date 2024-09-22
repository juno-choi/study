# 🔴 주문 리스트 만들기

## 🟠 주문 도메인 정의

### 🟢 주문 요청 정의

```java
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetOrderListRequest {
    private int size;
    private int page;
    private Sort sort;
    private String orderContact;
}
```

orderContact를 통해 주문자를 구별하기 위해 연락처만 받았다.

### 🟢 주문 응답 정의

```java
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
@Schema(description = "주문 리스트")
public record OrderResponseList (
        @Schema(description = "페이지 마지막 여부", example = "true")
        Boolean last,
        @Schema(description = "페이지 비어있음 여부", example = "false")
        Boolean empty,
        @Schema(description = "총 페이지", example = "1")
        int totalPage,
        @Schema(description = "총 데이터 수", example = "1")
        Long totalElements,
        @Schema(description = "페이지에 존재하는 데이터 수", example = "1")
        int numberOfElement,
        @Schema(description = "리스트")
        List<OrderVo> orderList
) {
    public static OrderResponseList from(Page<OrderEntity> orderPage) {
        int totalPage = orderPage.getTotalPages();
        Long totalElements = orderPage.getTotalElements();
        int numberOfElement = orderPage.getNumberOfElements();
        Boolean last = orderPage.isLast();
        Boolean empty = orderPage.isEmpty();

        List<OrderVo> orderVoList = orderPage.stream()
                .map(OrderVo::from)
                .toList();

        return OrderResponseList.builder()
                .orderList(orderVoList)
                .totalPage(totalPage)
                .totalElements(totalElements)
                .numberOfElement(numberOfElement)
                .last(last)
                .empty(empty)
                .build();
    }
}
```

```java
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
@Schema(description = "주문")
public record OrderVo(
        @Schema(description = "주문번호", example = "1")
        Long orderId,
        @Schema(description = "주문상태", example = "COMPLETE")
        OrderStatus orderStatus,
        @Schema(description = "주문자", example = "주문자")
        String orderName,
        @Schema(description = "주문자 전화번호", example = "010-1234-5678")
        String orderContact,
        @Schema(description = "주문자 주소", example = "주소")
        String orderAddress,
        @Schema(description = "주문자 상세 주소", example = "상세 주소")
        String orderAddressDetail,
        @Schema(description = "주문자 우편번호", example = "12345")
        String orderZipcode,
        @Schema(description = "수령인", example = "수령인")
        String recipientName,
        @Schema(description = "수령인 전화번호", example = "010-1234-5678")
        String recipientContact,
        @Schema(description = "수령인 주소", example = "주소")
        String recipientAddress,
        @Schema(description = "수령인 상세 주소", example = "상세 주소")
        String recipientAddressDetail,
        @Schema(description = "수령인 우편번호", example = "12345")
        String recipientZipcode,
        @Schema(description = "주문금액", example = "100000")
        int orderAmount
) {
    public static OrderVo from(OrderEntity orderEntity) {
        return OrderVo.builder()
                .orderId(orderEntity.getOrderId())
                .orderStatus(orderEntity.getOrderStatus())
                .orderName(orderEntity.getOrderName())
                .orderContact(orderEntity.getOrderContact())
                .orderAddress(orderEntity.getOrderAddress())
                .orderAddressDetail(orderEntity.getOrderAddressDetail())
                .orderZipcode(orderEntity.getOrderZipcode())
                .recipientName(orderEntity.getRecipientName())
                .recipientContact(orderEntity.getRecipientContact())
                .recipientAddress(orderEntity.getRecipientAddress())
                .recipientAddressDetail(orderEntity.getRecipientAddressDetail())
                .recipientZipcode(orderEntity.getRecipientZipcode())
                .orderAmount(orderEntity.getOrderAmount())
                .build();
    }
}
```

반환 데이터를 정의하고

### 🟢 주문 조회 서비스 정의

```java
public interface OrderService {
    ...
    OrderResponseList getOrderList(GetOrderListRequest getOrderListRequest);
}
```

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductOptionCustomRepository productOptionCustomRepository;
    private final OrderCustomRepository orderCustomRepository;
    
    ...

    @Override
    public OrderResponseList getOrderList(GetOrderListRequest getOrderListRequest) {
        // todo 요청 받은 값으로 주문 리스트를 가져오기
        Page<OrderEntity> orderList = orderCustomRepository.getOrderList(getOrderListRequest);
        return OrderResponseList.from(orderList);
    }
}
```

### 🟢 주문 리스트 조회 Repository 정의

```java
public interface OrderCustomRepository {
    Page<OrderEntity> getOrderList(GetOrderListRequest getOrderListRequest);
}
```

```java
@Repository
@RequiredArgsConstructor
public class OrderCustomRepositoryImpl implements OrderCustomRepository {
    private final JPAQueryFactory querydsl;

    @Override
    public Page<OrderEntity> getOrderList(GetOrderListRequest getOrderListRequest) {
        String orderContact = getOrderListRequest.getOrderContact().replace("-", "");
        Pageable pageable = Pageable.ofSize(getOrderListRequest.getSize()).withPage(getOrderListRequest.getPage());

        Sort sort = getOrderListRequest.getSort();
        QOrderEntity order = QOrderEntity.orderEntity;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(order.orderContact.eq(orderContact));

        List<OrderEntity> fetch = querydsl.selectFrom(order)
                .where(builder)
                .orderBy(sort == Sort.ASC ? order.orderId.asc() : order.orderId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = querydsl.selectFrom(order)
                .where(builder)
                .fetch()
                .stream().count();

        return new PageImpl<>(fetch, pageable, total);
    }
}
```

### 🟢 주문 리스트 조회 Test Code 정의

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

    ...

    @Test
    @DisplayName("주문을 조회하는데 성공한다.")
    void getOrderList() {
        //given
        final String orderContact = "01012345678";

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
                .orderContact(orderContact)
                .orderAddress("경기도 성남시 분당구 판교역로 231")
                .orderAddressDetail("H스퀘어 S동 5층")
                .orderZipcode("12345")
                .recipientName("받는이")
                .recipientContact("010-1234-5678")
                .recipientAddress("경기도 성남시 분당구 판교역로 231")
                .recipientAddressDetail("H스퀘어 S동 6층")
                .recipientZipcode("12345")
                .build();

        for (int i=0; i<30; i++) {
            orderRepository.save(OrderEntity.from(postOrderRequest));
        }

        GetOrderListRequest getOrderListRequest = GetOrderListRequest.builder()
                .size(10)
                .page(0)
                .orderContact(orderContact)
                .build();
        //when
        OrderResponseList orderList = orderService.getOrderList(getOrderListRequest);
        //then
        Assertions.assertThat(orderList.orderList().size()).isGreaterThan(0);
    }

}
```

## 🟠 주문 리스트 조회 Controller 정의

### 🟢 Controller

```java
@ApiController
@RequiredArgsConstructor
@Tag(name = "Order", description = "Order API Documentation")
public class OrderController {
    private final OrderService orderService;
    
    ...

    @GetMapping("/order")
    @Operation(summary = "주문리스트", description = "주문리스트 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PostProductResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
    })
    public ResponseEntity<ResponseData<OrderResponseList>> getOrderList(@RequestParam(name = "size") int size, @RequestParam(name = "page") int page, @RequestParam(name = "sort") Sort sort, @RequestParam(name = "orderContact") String orderContact) {
        OrderResponseList orderResponseList = orderService.getOrderList(GetOrderListRequest.from(size, page, sort, orderContact));
        return ResponseEntity.ok(ResponseData.from(ResponseDataCode.SUCCESS, orderResponseList));
    }
}
```

### 🟢 Test Code

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

    ...
    
    @Test
    @DisplayName("[GET] /api/v1/order")
    void getOrderList() throws Exception {
        //given
        final String orderContact = "01012345678";

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
                .orderContact(orderContact)
                .orderAddress("경기도 성남시 분당구 판교역로 231")
                .orderAddressDetail("H스퀘어 S동 5층")
                .orderZipcode("12345")
                .recipientName("받는이")
                .recipientContact("010-1234-5678")
                .recipientAddress("경기도 성남시 분당구 판교역로 231")
                .recipientAddressDetail("H스퀘어 S동 6층")
                .recipientZipcode("12345")
                .build();

        for (int i=0; i<30; i++) {
            orderRepository.save(OrderEntity.from(postOrderRequest));
        }

        //when
        ResultActions perform = mockMvc.perform(
                get("/api/v1/order")
                        .param("size", "10")
                        .param("page", "0")
                        .param("sort", "DESC")
                        .param("orderContact", orderContact));
        //then
        perform.andExpect(status().isOk());
        Assertions.assertThat(perform.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8)).contains("0000");
    }

}
```
