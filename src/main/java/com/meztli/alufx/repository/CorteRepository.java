package com.meztli.alufx.repository;

import com.meztli.alufx.entities.Corte;
import com.meztli.alufx.entities.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class CorteRepository {

    public List<Corte> findAll() {
        EntityManager em = JpaUtil.getFactory().createEntityManager();
        try {
            return em.createQuery("from Corte", Corte.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void save(Corte corte) {
        EntityManager em = JpaUtil.getFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(corte);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
