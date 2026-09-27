package com.gdghongik.commerce.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true) //cascade는 주문을 저장하면 orderitem 항목 전부도 저장하겠다
    private List<OrderItem> orderItems = new ArrayList<>();

    private Order(OrderStatus status) {
        this.status = status;
    }

    public static Order place(OrderItem item) { //주문을 생성하는 길이 이것 하나 뿐, new Order은 불가능하기 때문
        Order order = new Order(OrderStatus.CREATED);
        order.addItem(item);
        return order;
    }

    public void addItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("주문 항목이 있어야 주문할 수 있습니다.");
        }

        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("주문 확정 이후에는 항목을 변경할 수 없습니다.");
        }
        this.orderItems.add(item);
        item.assignTo(this);
    }

    public void cancel() {
        if (this.status == OrderStatus.SHIPPED || this.status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("배송이 시작된 주문은 취소할 수 없습니다.");
        }
        this.status = OrderStatus.CANCELED;
    }

    public void cancelItem(OrderItem item) {
        if (this.status == OrderStatus.SHIPPED || this.status == OrderStatus.DELIVERED) {
            throw new IllegalStateException("배송이 시작된 주문은 취소할 수 없습니다.");
        }

        if (!this.orderItems.contains(item)) {
            throw new IllegalArgumentException("주문에 존재하지 않는 항목입니다.");
        }
        // 남아있는 항목이 1개일 때 삭제를 시도하면 예외를 발생시켜 항목이 0개가 되는 것을 방지
        if (this.orderItems.size() <= 1) {
            throw new IllegalStateException("주문 항목이 1개일 때는 부분 취소할 수 없습니다. 주문 전체 취소를 이용해주세요.");
        }

        this.orderItems.remove(item);
    }

    public Money totalAmount() {
        return orderItems.stream()
                .map(OrderItem::subtotal)
                .reduce(Money.ZERO, Money::add);
    }

    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(orderItems);
        //List.copyOf()도 테스트 통과하고, 작동도 하지만, 쓸일이 없을지도 모르는 데이터를 db에서 전부 읽어와야한다는 단점이 존재
    }

    public void ship() {
        this.status = OrderStatus.SHIPPED;
    }

    public void deliver() {
        this.status = OrderStatus.DELIVERED;
    }
}
