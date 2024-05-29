package me.staek.shop.domain;

import jakarta.persistence.*;

@Entity
public class Locker extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "LOCKER_ID")
    private Long id;

    private String name;

    /**
     * 주테이블 Member (1) : 타겟테이블 Locker (1)
     * - 주 테이블의 locker를 참조하는 읽기전용 참조객체
     */
    @OneToOne(mappedBy = "locker", fetch = FetchType.LAZY)
    private Member member;

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void addMember(Member member) {
        member.setLocker(this);
        this.member = member;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
