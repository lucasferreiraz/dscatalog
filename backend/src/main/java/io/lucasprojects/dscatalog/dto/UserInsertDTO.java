package io.lucasprojects.dscatalog.dto;

public class UserInsertDTO extends UserDTO{

    private static final long serialVersionUID = 1L;

    private String password;

    //DTO específico para trafegar a senha do usuário
    public UserInsertDTO() { 
        super();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
