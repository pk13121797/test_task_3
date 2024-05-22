package by.pavvel.model.reg;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;
import java.util.Objects;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "{register.name.blank}")
    @Size(min=4, max=50,message = "{register.name.size}")
    private String name;

    @NotBlank(message = "{register.email.blank}")
    @Email(message = "{register.email}")
    private String email;

    @NotBlank(message = "{register.password.blank}")
    @Size(min=7, max=50,message = "{register.password.size}")
    private String password;

    public RegisterRequest() {
    }

    public RegisterRequest(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegisterRequest that)) return false;
        return Objects.equals(name, that.name) && Objects.equals(email, that.email) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, password);
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
