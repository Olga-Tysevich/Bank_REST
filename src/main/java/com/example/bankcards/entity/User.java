package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.RoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.example.bankcards.util.Constants.*;

/**
 * This class represents a User entity with its attributes.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userIdSeq")
    @SequenceGenerator(name = "userIdSeq", sequenceName = "user_id_seq", allocationSize = 1)
    @NotNull(message = ID_CANNOT_BE_NULL)
    private Long id;

    @Column(nullable = false, length = 500)
    @NotBlank(message = NAME_CANNOT_BE_EMPTY)
    @Size(max = 500, message = NAME_CANNOT_BE_GZ_500)
    private String username;

    @Column(nullable = false, length = 500)
    @NotBlank(message = NAME_CANNOT_BE_EMPTY)
    @Size(max = 500, message = NAME_CANNOT_BE_GZ_500)
    private String name;

    @Column(nullable = false, length = 500)
    @NotBlank(message = NAME_CANNOT_BE_EMPTY)
    @Size(max = 500, message = NAME_CANNOT_BE_GZ_500)
    private String surname;

    @Column(name = "date_of_birth")
    @Past(message = DATE_OF_BIRTH_MUST_BE_IN_PAST)
    private LocalDate dateOfBirth;

    @Column(length = 500)
    @Size(min = 8, max = 500, message = INVALID_PASSWORD_LENGTH)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    @NotEmpty(message = THE_ROLE_SET_CANNOT_BE_EMPTY)
    private Set<Role> roleSet = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleSet;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public boolean isAdmin() {
        return roleSet.stream()
                .map(Role::getRole)
                .filter(RoleEnum.ROLE_ADMIN::equals)
                .count() == 1;
    }

}