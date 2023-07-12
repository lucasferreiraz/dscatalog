package io.lucasprojects.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import io.lucasprojects.dscatalog.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
