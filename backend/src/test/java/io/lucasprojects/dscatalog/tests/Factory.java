package io.lucasprojects.dscatalog.tests;

import io.lucasprojects.dscatalog.dto.ProductDTO;
import io.lucasprojects.dscatalog.entities.Category;
import io.lucasprojects.dscatalog.entities.Product;

public class Factory {
    
    public static Product createProduct(){
        Product product = new Product(1L, "Phone", "Good Phone", 800.0 , "https://img.com/img.png");
        product.getCategories().add(new Category(2L, "Eletronics"));
        return product;
    }

    public static ProductDTO createProductDTO(){
        Product product = createProduct();

        return new ProductDTO(product, product.getCategories());
    }

}
