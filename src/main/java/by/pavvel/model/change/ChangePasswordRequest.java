package by.pavvel.model.change;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;
import java.util.Objects;

@Getter
@Setter
public class ChangePasswordRequest {

    @NotBlank(message = "{change.newPassword.blank}")
    @Size(min = 7,max = 50,message = "{change.newPassword.size}")
    private String newPassword;

    @NotBlank(message = "{change.confirmPassword.blank}")
    @Size(min = 7,max = 50,message = "{change.confirmPassword.size}")
    private String confirmPassword;

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String newPassword, String confirmPassword) {
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChangePasswordRequest that = (ChangePasswordRequest) o;
        return Objects.equals(newPassword, that.newPassword) && Objects.equals(confirmPassword, that.confirmPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(newPassword, confirmPassword);
    }

    @Override
    public String toString() {
        return "ChangePasswordRequest{" +
                "newPassword='" + newPassword + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                '}';
    }
}
