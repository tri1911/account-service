package account.auth.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long groupId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleType name;

    public Group(RoleType name) {
        this.name = name;
    }

    public enum RoleType {
        ADMINISTRATOR,
        USER,
        ACCOUNTANT,
        AUDITOR,
    }

    @Override
    public String toString() {
        return name != null ? name.toString() : "null";
    }
}
