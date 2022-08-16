package io.lucasprojects.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.lucasprojects.dscatalog.dto.RoleDTO;
import io.lucasprojects.dscatalog.dto.UserDTO;
import io.lucasprojects.dscatalog.dto.UserInsertDTO;
import io.lucasprojects.dscatalog.entities.Role;
import io.lucasprojects.dscatalog.entities.User;
import io.lucasprojects.dscatalog.repositories.RoleRepository;
import io.lucasprojects.dscatalog.repositories.UserRepository;
import io.lucasprojects.dscatalog.services.exceptions.DatabaseException;
import io.lucasprojects.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class UserService {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository UserRepository;

    @Autowired
    private RoleRepository RoleRepository;

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(Pageable pageable) {
        Page<User> list = UserRepository.findAll(pageable);
        Page<UserDTO> listDTO = list.map(User -> new UserDTO(User));

        return listDTO;
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id){
        Optional<User> obj = UserRepository.findById(id);
        User entity = obj.orElseThrow(() -> new ResourceNotFoundException("Entity not found or not exist."));
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto){
        User entity = new User();

        copyDtoToEntity(dto, entity);
        //entity.setName(dto.getName());
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));

        entity = UserRepository.save(entity);

        return new UserDTO(entity);

    }

    @Transactional
    public UserDTO update(Long id, UserDTO dto){
        try {

            User entity = UserRepository.getReferenceById(id);

            copyDtoToEntity(dto, entity);

            //entity.setName(dto.getName());
            entity = UserRepository.save(entity);
            return new UserDTO(entity); 

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("ID not found: " + id);
        }
        
    }

    public void delete(Long id){
        try {
            UserRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("ID not found" + id);
        } catch (DataIntegrityViolationException e){
            throw new DatabaseException("Integrity violation");
        }
        
    }

    private void copyDtoToEntity(UserDTO dto, User entity) {

        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());

        entity.getRoles().clear();
        for(RoleDTO roleDTO : dto.getRoles()){
            Role role = RoleRepository.getReferenceById(roleDTO.getId());
            entity.getRoles().add(role);
        }

    }

}
