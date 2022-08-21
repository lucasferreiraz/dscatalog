package io.lucasprojects.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import io.lucasprojects.dscatalog.dto.UserUpdateDTO;
import io.lucasprojects.dscatalog.entities.User;
import io.lucasprojects.dscatalog.repositories.UserRepository;
import io.lucasprojects.dscatalog.resources.exceptions.FieldMessage;

public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {

    @Autowired
    private HttpServletRequest request;
	
    @Autowired
    private UserRepository userRepository;

	@Override
	public void initialize(UserUpdateValid ann) {
	}

	@Override
	public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {

        @SuppressWarnings("unchecked")
        var uniVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Long userId = Long.parseLong(uniVars.get("id"));
		
		List<FieldMessage> list = new ArrayList<>();
		
		// Coloque aqui seus testes de validação, acrescentando objetos FieldMessage à lista
		
        User user = userRepository.findByEmail(dto.getEmail());

        if(user != null && userId != user.getId()){
            list.add(new FieldMessage("email", "This email DOES exist."));
        }

		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getFieldMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}
