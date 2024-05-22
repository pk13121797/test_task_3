package by.pavvel.model.reg;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Getter
@Setter
@Entity(name = "ApplicationUser")
@Table(name = "application_user")
public class ApplicationUser implements UserDetails {

    @Id
    @SequenceGenerator(name = "user_sequence",sequenceName = "user_sequence",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "user_sequence")
    @Column(name = "id",updatable = false)
    private Long id;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "password",nullable = false)
    private String password;

    @Column(name = "email",nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role",nullable = false)
    private UserRole userRole;

    @Column(name = "locked",nullable = false)
    private Boolean locked = false;

    @Column(name = "enabled",nullable = false)
    private Boolean enabled = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_" + userRole.name());
        return Collections.singletonList(simpleGrantedAuthority);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public ApplicationUser() {
    }

    public ApplicationUser(String name, String password, String email, UserRole userRole) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.userRole = userRole;
    }

    public ApplicationUser(Long id, String name, String password, String email, UserRole userRole, Boolean locked, Boolean enabled) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.userRole = userRole;
        this.locked = locked;
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApplicationUser user)) return false;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name) &&
                Objects.equals(password, user.password) && Objects.equals(email, user.email) && userRole == user.userRole &&
                Objects.equals(locked, user.locked) && Objects.equals(enabled, user.enabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, password, email, userRole, locked, enabled);
    }

    @Override
    public String toString() {
        return "ApplicationUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", userRole=" + userRole +
                ", locked=" + locked +
                ", enabled=" + enabled +
                '}';
    }
}
