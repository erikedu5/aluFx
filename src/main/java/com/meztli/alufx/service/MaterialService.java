package com.meztli.alufx.service;

import com.meztli.alufx.entities.Material;
import com.meztli.alufx.entities.MaterialRepository;

import java.util.List;

/**
 * Service layer for {@literal Material} operations.
 */
public class MaterialService {

    private final MaterialRepository repository;

    public MaterialService() {
        this.repository = new MaterialRepository();
    }

    /**
     * Fetch all materials from the repository.
     *
     * @return list of materials
     */
    public List<Material> getAllMaterials() {
        return repository.findAll();
    }

    /**
     * Persist a new material entity.
     *
     * @param material the material to save
     */
    public void saveMaterial(Material material) {
        repository.save(material);
    }
}
