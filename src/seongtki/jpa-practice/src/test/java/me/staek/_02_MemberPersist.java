package me.staek;


import jakarta.persistence.*;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Test;

import java.util.List;

public class _02_MemberPersist {



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

            Member member = new Member("member");
            em.persist(member);
            em.flush();
            Long id = member.getId();
            Member finded = em.find(Member.class, id);
            Assertions.assertThat(finded.getName()).isEqualTo("member");
            tx.commit();
        } catch (PersistenceException e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }



    /**
     * update 대상 객체가 detach 되면 영속성컨텍스트에서 분리되어 flush할 지연쿼리가 없어 갱신되지 않는다.
     */
    @Test
    void member_detach() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Member member = new Member("member_detach");
            em.persist(member);
            em.flush();

            member.setName("update_member");
            Long id = member.getId();
            em.detach(member);
            em.flush();

            Member finded = em.find(Member.class, id);
            System.out.println(finded.getName());
            Assertions.assertThat(finded.getName()).isNotEqualTo("update_member");
            tx.commit();
        } catch (PersistenceException e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }

    /**
     * 영속성 컨텍스트는 같은 조건에 대해 동등한(itentity) 객체를 반환한다.
     */
    @Test
    void member_identity() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Member member = new Member("member");
            em.persist(member);
            em.flush();

            Long id = member.getId();

            Member finded1 = em.find(Member.class, id);
            Member finded2 = em.find(Member.class, id);
            Assertions.assertThat(finded1).isEqualTo(finded2);
            tx.commit();
        } catch (PersistenceException e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }


    /**
     * EntityManager - clear, close는 1차캐시 모든 데이터를 삭제한다.
     */
    @Test
    void member_clear() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Member member = new Member("member");
            Member member2 = new Member("member");
            Member member3 = new Member("member");
            em.persist(member);
            em.persist(member2);
            em.persist(member3);
            em.clear();
//            em.close();
            em.flush();

            List<Member> list = em.createQuery("select m from Member as m ", Member.class).getResultList();
            System.out.println(list.size());

            Assertions.assertThat(list.size()).isZero();
            tx.commit();
        } catch (PersistenceException e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }

    /**
     * batch_size 유무에 따른 insert 시간 테스트
     */
    @Test
    void member_insert_10000() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        long start = System.currentTimeMillis();
        try {

            tx = em.getTransaction();
            tx.begin();

            for ( int i = 0; i < 10000; i++ ) {
                Member member = new Member("test");
                em.persist( member );
            }

            tx.commit();
        } catch (RuntimeException e) {
            if ( tx != null && tx.isActive()) tx.rollback();
            throw e;
        } finally {
            if (em != null) {
                em.close();
            }

            long end = System.currentTimeMillis();
            System.out.println("걸린시간 ::: " + (end - start));
        }
    }
}
