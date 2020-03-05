package org.example.camelspringdemo.repository;

import org.example.camelspringdemo.model.CatalogEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogRepository extends CrudRepository<CatalogEntity, Long> {
}
