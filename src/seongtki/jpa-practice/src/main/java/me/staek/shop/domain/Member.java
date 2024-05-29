package me.staek.shop.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Member extends BaseEntity {


    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "MEMBER_ID")
    private Long id;
    private String name;
    private String city;
    private String zipcode;
    private String street;


    /**
     * M쪽 연관관계주인 매핑
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID")
    private Team team;


    /**
     * M쪽 연관관계 매핑
     * - insert, update 를 모두 False 해주어야 갱신,추가 권한이 없어져 문제가 없다.
     */
//    @ManyToOne
//    @JoinColumn(name = "TEAM_ID", insertable = false, updatable = false)
//    private Team team;

    /**
     * 1쪽 연관관계 매핑
     */
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();


    /**
     * 주테이블 Member (1) : 타겟테이블 Locker (1)
     * - 주 테이블에 연관관계 매핑
     * - Member 테이블에 외래키 생성.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCKER_ID")
    private Locker locker;


    /**
     * N:M 설정
     * 양방향 참조객체 설정
     */
//    @ManyToMany(mappedBy = "members")
//    private List<Product> products = new ArrayList<>();

//    public List<Product> getProducts() {
//        return products;
//    }

    /**
     * 1쪽 참조객체
     * - N:M 매핑테이블의 member를 참조
     */
    @OneToMany(mappedBy = "member")
    private List<ProductMember> productMembers = new ArrayList<>();

    public List<ProductMember> getProductMembers() {
        return productMembers;
    }

    public Locker getLocker() {
        return locker;
    }

    public void setLocker(Locker locker) {
        this.locker = locker;
    }

    public Long getId() {
        return id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

}
