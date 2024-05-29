package me.staek;


import jakarta.persistence.*;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Test;

public class _01_MemberCRUD {

    @Test
    void member_insert() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            TestMember testMember = new TestMember();
            testMember.setName("name");

            em.persist(testMember);
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
     * not null 조건인 name 없이 쿼리 할 때 예외 테스트
     */
    @Test
    void member_insert_exception() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            TestMember testMember = new TestMember();

            Assertions.assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
                @Override
                public void call() throws Throwable {
                    em.persist(testMember);
                }
            }).isInstanceOf(PropertyValueException.class);
            tx.commit();
        } catch (PersistenceException e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }


    @Test
    void member_insert_and_update() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            TestMember testMember = new TestMember();
            testMember.setName("name");
            em.persist(testMember);

            em.flush();

            /**
             * 1차캐시에 저장된 상태에서 (Detach 아닌 엔티티정보) 변경한다.
             */
            testMember.setName("new name");
            /**
             * 지연쿼리 요청
             */
            em.flush();
            Long id = testMember.getId();
            /**
             * 1차캐시 삭제
             */
            em.clear();
            /**
             * DB로부터 id에대한 정보 조회
             */
            TestMember finded = em.find(TestMember.class, id);
            Assertions.assertThat(finded.getName()).isEqualTo("new name");
            tx.commit();
        } catch (PersistenceException e) {
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }

    @Test
    void member_insert_and_delete() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            TestMember testMember = new TestMember();
            testMember.setName("name");
            em.persist(testMember);

            em.flush();

            /**
             * 1차캐시에 저장된 상태에서 (Detach 아닌 엔티티정보) 변경한다.
             */
            Long id = testMember.getId();
            em.remove(testMember);
            /**
             * 지연쿼리 요청
             */
            em.flush();
            /**
             * 1차캐시 삭제
             */
            em.clear();
            /**
             * DB로부터 id에대한 정보 조회
             */
            TestMember finded = em.find(TestMember.class, id);
            Assertions.assertThat(finded).isNull();
            tx.commit();
        } catch (PersistenceException e) {
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }
}
