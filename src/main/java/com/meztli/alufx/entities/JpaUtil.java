package com.meztli.alufx.entities;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaUtil {
    private static final EntityManagerFactory FACTORY =
            Persistence.createEntityManagerFactory("aluFxPU");

    public static EntityManagerFactory getFactory() {
        return FACTORY;
    }
}
