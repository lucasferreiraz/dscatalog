package io.lucasprojects.dscatalog.resources;

import static org.mockito.ArgumentMatchers.eq;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.lucasprojects.dscatalog.dto.ProductDTO;
import io.lucasprojects.dscatalog.services.ProductService;
import io.lucasprojects.dscatalog.services.exceptions.DatabaseException;
import io.lucasprojects.dscatalog.services.exceptions.ResourceNotFoundException;
import io.lucasprojects.dscatalog.tests.Factory;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private ProductDTO productDTO;
    private PageImpl<ProductDTO> page;

    @BeforeEach
    void setUp() throws Exception{
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 2L;
        productDTO = Factory.createProductDTO();
        page = new PageImpl<>(List.of(productDTO));

        //Find
        Mockito.when(productService.findAllPaged(ArgumentMatchers.any())).thenReturn(page);
        Mockito.when(productService.findById(existingId)).thenReturn(productDTO);
        Mockito.when(productService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        //Update
        Mockito.when(productService.update(eq(existingId), ArgumentMatchers.any())).thenReturn(productDTO);
        Mockito.when(productService.update(eq(nonExistingId), ArgumentMatchers.any()))
                    .thenThrow(ResourceNotFoundException.class);

        //Delete
        Mockito.doNothing().when(productService).delete(existingId);
        Mockito.doThrow(ResourceNotFoundException.class).when(productService).delete(nonExistingId);
        Mockito.doThrow(DatabaseException.class).when(productService).delete(dependentId);
        
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExist() throws Exception{
        String stringBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                                                    .put("/products/{id}", existingId)
                                                    .content(stringBody)
                                                    .contentType(MediaType.APPLICATION_JSON)
                                                    .accept(MediaType.APPLICATION_JSON));
        
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.description").exists());

    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception{
        String stringBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                                                    .put("/products/{id}", nonExistingId)
                                                    .content(stringBody)
                                                    .contentType(MediaType.APPLICATION_JSON)
                                                    .accept(MediaType.APPLICATION_JSON));
        
        result.andExpect(MockMvcResultMatchers.status().isNotFound());     
    }

    @Test
    public void findAllShouldReturnPage() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                            .get("/products")
                            .accept(MediaType.APPLICATION_JSON))
               .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() throws Exception {

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                                                    .get("/products/{id}", existingId)
                                                    .accept(MediaType.APPLICATION_JSON));
        
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.description").exists());

    }

    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception{
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                                                    .get("/products/{id}", nonExistingId)
                                                    .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotFound());                                           

    }
}
