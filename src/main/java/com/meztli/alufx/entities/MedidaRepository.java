package com.meztli.alufx.entities;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class MedidaRepository {

    public List<Medida> findByMaterialAndTipoProducto(int materialId, String tipoProducto) {
        EntityManager em = JpaUtil.getFactory().createEntityManager();
        try {
            return em.createQuery("from Medida m where m.material.id = :mId and m.tipoProducto = :tp", Medida.class)
                    .setParameter("mId", materialId)
                    .setParameter("tp", tipoProducto)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void save(Medida medida) {
        EntityManager em = JpaUtil.getFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(medida);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void updateMedida(Double medidaValor, int corteId, int materialId) {
        EntityManager em = JpaUtil.getFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createQuery("update Medida m set m.medida = :medida where m.corte.id = :cId and m.material.id = :mId")
                    .setParameter("medida", medidaValor)
                    .setParameter("cId", corteId)
                    .setParameter("mId", materialId)
                    .executeUpdate();
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
