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

import io.lucasprojects.dscatalog.dto.CategoryDTO;
import io.lucasprojects.dscatalog.entities.Category;
import io.lucasprojects.dscatalog.repositories.CategoryRepository;
import io.lucasprojects.dscatalog.services.exceptions.DatabaseException;
import io.lucasprojects.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAllPaged(PageRequest pageRequest) {
        Page<Category> list = categoryRepository.findAll(pageRequest);
        Page<CategoryDTO> listDTO = list.map(category -> new CategoryDTO(category));

        return listDTO;
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id){
        Optional<Category> obj = categoryRepository.findById(id);
        Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found or not exist."));
        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto){
        Category entity = new Category();
        entity.setName(dto.getName());

        entity = categoryRepository.save(entity);

        return new CategoryDTO(entity);

    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto){
        try {

            Category entity = categoryRepository.getReferenceById(id);
            entity.setName(dto.getName());
            entity = categoryRepository.save(entity);
            return new CategoryDTO(entity); 

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("ID not found: " + id);
        }
        
    }

    public void delete(Long id){
        try {
            categoryRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("ID not found" + id);
        } catch (DataIntegrityViolationException e){
            throw new DatabaseException("Integrity violation");
        }
        
    }

}
