package me.staek.shop.domain;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "TEAM_ID")
    private Long id;


    /**
     * 1쪽 연관관계 매핑
     */
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members = new ArrayList<>();

    /**
     * 1쪽 연관관계주인 매핑
     */
//    @OneToMany
//    @JoinColumn(name = "TEAM_ID")
//    private List<Member> members = new ArrayList<>();


    public List<Member> getMembers() {
        return members;
    }

    public Long getId() {
        return id;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }


    /**
     * 연관관계 편의 메서드
     */
    public void addMember(Member member) {
        member.setTeam(this);
        this.members.add(member);
    }
}
