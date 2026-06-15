package com.meztli.alufx.repository;

import com.meztli.alufx.entities.JpaUtil;
import com.meztli.alufx.entities.Pedido;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class PedidoRepository {

    public List<Pedido> findAll() {
        EntityManager em = JpaUtil.getFactory().createEntityManager();
        try {
            return em.createQuery("from Pedido p order by p.fecha desc", Pedido.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Pedido findById(int id) {
        EntityManager em = JpaUtil.getFactory().createEntityManager();
        try {
            return em.find(Pedido.class, id);
        } finally {
            em.close();
        }
    }

    public void save(Pedido pedido) {
        EntityManager em = JpaUtil.getFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(pedido);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
