package io.lucasprojects.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.lucasprojects.dscatalog.repositories.ProductRepository;
import io.lucasprojects.dscatalog.services.exceptions.DatabaseException;
import io.lucasprojects.dscatalog.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
    
    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 4L;

        Mockito.doNothing().when(productRepository).deleteById(existingId);

        Mockito.doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(nonExistingId);

        Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);

    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId(){

        Assertions.assertThrows(DatabaseException.class, () -> {
            productService.delete(dependentId);
        });

        Mockito.verify(productRepository, Mockito.times(1)).deleteById(dependentId);

    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists(){

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nonExistingId);
        });

        Mockito.verify(productRepository, Mockito.times(1)).deleteById(nonExistingId);

    }

    @Test
    public void deleteShouldDoNothingWhenIdExists(){
        
        Assertions.assertDoesNotThrow(() -> {
            productService.delete(existingId);
        });

        Mockito.verify(productRepository, Mockito.times(1)).deleteById(existingId);

    }

}
