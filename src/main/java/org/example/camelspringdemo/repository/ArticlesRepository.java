package org.example.camelspringdemo.repository;

import org.example.camelspringdemo.model.ArticlesEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticlesRepository extends CrudRepository<ArticlesEntity, Long> {
}
