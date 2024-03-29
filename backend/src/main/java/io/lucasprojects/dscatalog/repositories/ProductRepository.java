package io.lucasprojects.dscatalog.repositories;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.lucasprojects.dscatalog.entities.Category;
import io.lucasprojects.dscatalog.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT DISTINCT obj FROM Product obj INNER JOIN obj.categories cats WHERE "
            + " (COALESCE(:categories) IS NULL OR cats IN :categories) AND "
            + " (LOWER(obj.name) LIKE LOWER(CONCAT('%',:name,'%')))")
    public Page<Product> find(List<Category> categories, String name, Pageable pageable);

    @Query("SELECT obj FROM Product obj JOIN FETCH obj.categories WHERE obj IN :products")
    public List<Product> findProductsWithCategories(List<Product> products);
}
