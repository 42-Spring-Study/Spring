package me.staek.shop.domain;

import jakarta.persistence.*;

@Entity
public class Delivery extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "DELIVERY_ID")
    private Long id;


    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    private String zipcode;
    private String street;
    private String city;
    private DeliveryStatus deliveryStatus;
}
