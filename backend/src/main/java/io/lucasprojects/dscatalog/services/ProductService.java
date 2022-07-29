package io.lucasprojects.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.lucasprojects.dscatalog.dto.ProductDTO;
import io.lucasprojects.dscatalog.entities.Product;
import io.lucasprojects.dscatalog.repositories.ProductRepository;
import io.lucasprojects.dscatalog.services.exceptions.DatabaseException;
import io.lucasprojects.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

    @Autowired
    private ProductRepository ProductRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {
        Page<Product> list = ProductRepository.findAll(pageRequest);
        Page<ProductDTO> listDTO = list.map(Product -> new ProductDTO(Product));

        return listDTO;
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id){
        Optional<Product> obj = ProductRepository.findById(id);
        Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found or not exist."));
        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto){
        Product entity = new Product();
        //entity.setName(dto.getName());

        entity = ProductRepository.save(entity);

        return new ProductDTO(entity);

    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto){
        try {

            Product entity = ProductRepository.getReferenceById(id);
            //entity.setName(dto.getName());
            entity = ProductRepository.save(entity);
            return new ProductDTO(entity); 

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("ID not found: " + id);
        }
        
    }

    public void delete(Long id){
        try {
            ProductRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("ID not found" + id);
        } catch (DataIntegrityViolationException e){
            throw new DatabaseException("Integrity violation");
        }
        
    }

}
