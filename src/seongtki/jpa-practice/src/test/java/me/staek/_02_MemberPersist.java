package me.staek;


import jakarta.persistence.*;
import org.assertj.core.api.Assertions;
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

            TestMember testMember = new TestMember("member");
            em.persist(testMember);
            em.flush();
            Long id = testMember.getId();
            TestMember finded = em.find(TestMember.class, id);
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

            TestMember testMember = new TestMember("member_detach");
            em.persist(testMember);
            em.flush();

            testMember.setName("update_member");
            Long id = testMember.getId();
            em.detach(testMember);
            em.flush();

            TestMember finded = em.find(TestMember.class, id);
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

            TestMember testMember = new TestMember("member");
            em.persist(testMember);
            em.flush();

            Long id = testMember.getId();

            TestMember finded1 = em.find(TestMember.class, id);
            TestMember finded2 = em.find(TestMember.class, id);
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

            TestMember testMember = new TestMember("member");
            TestMember testMember2 = new TestMember("member");
            TestMember testMember3 = new TestMember("member");
            em.persist(testMember);
            em.persist(testMember2);
            em.persist(testMember3);
            em.clear();
//            em.close();
            em.flush();

            List<TestMember> list = em.createQuery("select m from TestMember as m ", TestMember.class).getResultList();
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
                TestMember testMember = new TestMember("test" + i);
                em.persist(testMember);
            }

            tx.commit();
            List list = em.createQuery("select m from TestMember m").getResultList();
            Assertions.assertThat(10000).isEqualTo(list.size());
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
