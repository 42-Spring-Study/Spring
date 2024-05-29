package me.staek.shop.domain;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Product extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PRODUCT_ID")
    private Long id;

    private String name;

    /**
     * N:M 설정
     * 단방향 참조객체 매핑 설정
     */
//    @ManyToMany
//    @JoinTable(name = "PRODUCT_MEMBER")
//    private List<Member> members = new ArrayList<>();

//    public void addMember(Member member) {
//        member.getProducts().add(this);
//        this.members.add(member);
//    }

    /**
     * 1쪽 참조객체
     * - N:M 매핑테이블의 product를 참조
     */
    @OneToMany(mappedBy = "product")
    private List<ProductMember> productMembers = new ArrayList<>();

    public List<ProductMember> getProductMembers() {
        return productMembers;
    }
}
