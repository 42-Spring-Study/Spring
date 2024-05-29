package me.staek;


import jakarta.persistence.*;
import me.staek.shop.domain.Member;
import me.staek.shop.domain.Team;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class _03_MemberReference {



    /**
     * EntityManager - flush - 지연쿼리를 모두 요청하여 db와 영속성컨텍스트를 싱크한다
     *
     */
    @Test
    void member_flush() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Team team1 = new Team();
            team1.setName("team1");
            Team team2 = new Team();
            team2.setName("team2");

            Member member1 = new Member();
            Member member2 = new Member();
            team1.addMember(member1);
//            team2.addMember(member2);
            team1.addMember(member2);

            /**
             * cascade가 설정되어 있다면 부모만 persist하면 전파되어 자식도 등록된다.
             */
            em.persist(member1);
            em.persist(member2);
            em.persist(team1);
//            em.persist(team2);

            em.flush();
            em.clear();


            /**
             * 프록시 & 타겟객체 동등성 테스트
             * - 같은 조건의 Entity에 대해 먼저 getReference()(프록시)로 조회했다면, 이어서 find()(타겟객체)로 호출해도 프록시로 조회되고
             * - find()로 조회하여 타겟객체로 조회된다면, 이어서 getReference()(프록시)를 호출해도 타겟객체로 조회된디ㅏ.
             */
            Member reference1 = em.getReference(Member.class, 1L);
            Member find1 = em.find(Member.class, 1L);
            Member reference2 = em.getReference(Member.class, 1L);
//            System.out.println(reference1.getClass());
            System.out.println(find1.getClass());
            System.out.println(reference2.getClass());
//            Member member1 = em.find(Member.class, 1L);

            Team team = find1.getTeam();
            System.out.println(team.getId());
            System.out.println(team.getName());
            System.out.println(team.getClass());
            System.out.println(reference2.getTeam().getClass());

            em.clear();

            /**
             * fetch join을 이용해 team객체는 접근할 때 쿼리를 사용한다.
             * - team은 지연로딩이 설정되어 있어야 한다.
             */
//            List<Member> list = em.createQuery("select m from Member m join fetch m.team", Member.class).getResultList();
//            for (Member m : list) {
//                System.out.println(m.getTeam().getName());
//            }

            /**
             * 1대다 연관관계 설정에서
             * 1쪽에서 연관관계 매핑을 하게되면, member를 조회할 때 지연로딩을 설정해도 쿼리를 조인해서 즉시로딩한다.ㅠㅜ
             */
            System.out.println("=====================");
//            Team team3 = em.find(Team.class, 1L);
//            System.out.println(team3.getMembers().get(0).getClass());
//            Member member = em.find(Member.class, 1L);
//            System.out.println(member.getTeam().getClass());
            System.out.println("=====================");

            /**
             * 1쪽에 의해 M쪽이 삭제되었을 때 아래 옵션이 존재하면 M쪽만 삭제가 가능하다.
             * cascade = CascadeType.ALL, orphanRemoval = true
             */
//            team3.getMembers().remove(0);
//            Member member = em.find(Member.class, 1L);

            /**
             * 1쪽이 삭제되었을 때 M쪽이 모든 부모를 잃게 된다.
             * 기본적으로 부모가 먼저삭제될 수 없고
             * 1쪽에  orphanRemoval = true 혹은 cascade = CascadeType.REMOVE
             * 가 설정되어 있을 때 1쪽과 함께 M쪽이 모두 삭제된다.
             */
//            em.remove(team3);



//            List<Member> list = em.createQuery("select m from Member m", Member.class).getResultList();
//            for (Member m : list) {
//                System.out.println(m.getTeam().getName());
//            }

//            List<Team> teamList = em.createQuery("select m from Team m", Team.class).getResultList();
//            for (Team t : teamList) {
//                System.out.println("================================================");
//                List<Member> members = t.getMembers();
//                for (Member m : members) {
//                    System.out.println("???================================================");
//                    System.out.println(m.getName());
//                }
//            }

            tx.commit();
        } catch (PersistenceException e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }


}
