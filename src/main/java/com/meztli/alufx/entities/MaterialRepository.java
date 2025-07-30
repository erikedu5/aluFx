package com.meztli.alufx.entities;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class MaterialRepository {

    public List<Material> findAll() {
        EntityManager em = JpaUtil.getFactory().createEntityManager();
        try {
            return em.createQuery("from Material", Material.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void save(Material material) {
        EntityManager em = JpaUtil.getFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(material);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
