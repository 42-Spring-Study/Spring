package me.staek.shop.domain;

import jakarta.persistence.*;

/**
 * PRODUCT (N) : MEMBER (M)
 */
@Entity
@Table(name = "PRODUCT_MEMBER")
public class ProductMember extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PRODUCT_MEMBER_ID")
    private Long id;

    /**
     * M쪽 참조객체 매핑
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    /**
     * M쪽 참조객체 매핑
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    public void changeProduct(Product product) {
        product.getProductMembers().add(this);
        this.product = product;
    }

    public void changeMember(Member member) {
        member.getProductMembers().add(this);
        this.member = member;
    }

    public Product getProduct() {
        return product;
    }

    public Member getMembers() {
        return member;
    }

    private int column1;
    private int column2;
}
