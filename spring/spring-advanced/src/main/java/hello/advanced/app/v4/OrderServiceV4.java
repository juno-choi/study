package hello.advanced.app.v4;

import org.springframework.stereotype.Service;

import hello.advanced.trace.logtrace.LogTrace;
import hello.advanced.trace.template.AbstractTemplate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceV4 {
    private final OrderRepositoryV4 orderRepository;
    private final LogTrace trace;

    public void orderItem(String itemId) {

        AbstractTemplate<Void> template = new AbstractTemplate<>(trace) {

            @Override
            protected Void call() {
                orderRepository.save(itemId);
                return null; // void로 정의하면 null이라도 반환해주어야한다. 언어적 한계 ㅜ
            }
        };
        template.execute("OrderService.orderItem()");
    }
}
