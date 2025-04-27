package account.user.model;

import account.auth.model.Group;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Builder
@ToString(exclude = "password")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String lastname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // hashed password

    @Builder.Default
    @Column(nullable = false)
    private boolean nonLocked = true;

    @Builder.Default
    @Column(nullable = false)
    private int failedAttempts = 0;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Group> roles = new HashSet<>();
}
