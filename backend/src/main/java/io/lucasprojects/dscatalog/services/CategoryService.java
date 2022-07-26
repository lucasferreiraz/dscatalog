package io.lucasprojects.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.lucasprojects.dscatalog.dto.CategoryDTO;
import io.lucasprojects.dscatalog.entities.Category;
import io.lucasprojects.dscatalog.repositories.CategoryRepository;
import io.lucasprojects.dscatalog.services.exceptions.EntityNotFoundException;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll() {
        List<Category> list = categoryRepository.findAll();
        List<CategoryDTO> listDTO = list.stream()
                                        .map(category -> new CategoryDTO(category))
                                        .collect(Collectors.toList());

        return listDTO;
    }

    public CategoryDTO findById(Long id){
        Optional<Category> obj = categoryRepository.findById(id);
        Category entity = obj.orElseThrow(() -> new EntityNotFoundException("Entity not found or not exist."));
        return new CategoryDTO(entity);
    }

}
