package com.gdghongik.commerce.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private Long productId;

    private String productName;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "price"))
    private Money price;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "quantity"))
    private Quantity quantity;

    private OrderItem(Long productId, String productName, Money price, Quantity quantity) {
        if (productId == null || productName == null || price == null || quantity == null) {
            throw new IllegalArgumentException("주문 항목에 빠진 값이 있습니다.");
        }
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public static OrderItem create(Long productId, String productName, Money price, Quantity quantity) {
        return new OrderItem(productId, productName, price, quantity);
    } //정적 팩토리 메서드

    public Money subtotal() {
        return price.multiply(quantity);
    }

    void assignTo(Order order) {
        this.order = order;
    } //package private(누구나 만들 수 있는데, 아무 주문에나 갖다 붙힐 순 없다), private보다는 크고, protected보다 작은 범위에서 사용
}
