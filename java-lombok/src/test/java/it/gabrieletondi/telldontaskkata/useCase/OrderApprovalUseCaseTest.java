package it.gabrieletondi.telldontaskkata.useCase;

import it.gabrieletondi.telldontaskkata.domain.Order;
import it.gabrieletondi.telldontaskkata.domain.OrderStatus;
import it.gabrieletondi.telldontaskkata.doubles.TestOrderRepository;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class OrderApprovalUseCaseTest {
    private final TestOrderRepository orderRepository = new TestOrderRepository();
    private final OrderApprovalUseCase useCase = new OrderApprovalUseCase(orderRepository);

    @Test
    public void approvesExistingOrder() {
        Order initialOrder = new Order();
        initialOrder.setStatus(OrderStatus.CREATED);
        initialOrder.setId(1);
        orderRepository.addOrder(initialOrder);

        OrderApprovalRequest request = new OrderApprovalRequest();
        request.setOrderId(1);
        request.setApproved(true);
        useCase.run(request);

        final Order savedOrder = orderRepository.getSavedOrder();
        assertThat(savedOrder.getStatus(), is(OrderStatus.APPROVED));
    }

    @Test
    public void rejectsExistingOrder() {
        Order initialOrder = new Order();
        initialOrder.setStatus(OrderStatus.CREATED);
        initialOrder.setId(1);
        orderRepository.addOrder(initialOrder);

        OrderApprovalRequest request = new OrderApprovalRequest();
        request.setOrderId(1);
        request.setApproved(false);
        useCase.run(request);

        final Order savedOrder = orderRepository.getSavedOrder();
        assertThat(savedOrder.getStatus(), is(OrderStatus.REJECTED));
    }

    @Test(expected = CannotApproveRejectedOrder.class)
    public void cannotApproveRejectedOrder() {
        Order initialOrder = new Order();
        initialOrder.setStatus(OrderStatus.REJECTED);
        initialOrder.setId(1);
        orderRepository.addOrder(initialOrder);

        OrderApprovalRequest request = new OrderApprovalRequest();
        request.setOrderId(1);
        request.setApproved(true);
        useCase.run(request);

        assertThat(orderRepository.getSavedOrder(), is(nullValue()));
    }

    @Test(expected = CannotRejectApprovedOrder.class)
    public void cannotRejectApprovedOrder() {
        Order initialOrder = new Order();
        initialOrder.setStatus(OrderStatus.APPROVED);
        initialOrder.setId(1);
        orderRepository.addOrder(initialOrder);

        OrderApprovalRequest request = new OrderApprovalRequest();
        request.setOrderId(1);
        request.setApproved(false);
        useCase.run(request);

        assertThat(orderRepository.getSavedOrder(), is(nullValue()));
    }

    @Test(expected = OrderAlreadyShipped.class)
    public void shippedOrdersCannotBeApproved() {
        Order initialOrder = new Order();
        initialOrder.setStatus(OrderStatus.SHIPPED);
        initialOrder.setId(1);
        orderRepository.addOrder(initialOrder);

        OrderApprovalRequest request = new OrderApprovalRequest();
        request.setOrderId(1);
        request.setApproved(true);

        useCase.run(request);

        assertThat(orderRepository.getSavedOrder(), is(nullValue()));
    }

    @Test(expected = OrderAlreadyShipped.class)
    public void shippedOrdersCannotBeRejected() {
        Order initialOrder = new Order();
        initialOrder.setStatus(OrderStatus.SHIPPED);
        initialOrder.setId(1);
        orderRepository.addOrder(initialOrder);

        OrderApprovalRequest request = new OrderApprovalRequest();
        request.setOrderId(1);
        request.setApproved(false);
        useCase.run(request);

        assertThat(orderRepository.getSavedOrder(), is(nullValue()));
    }
}
