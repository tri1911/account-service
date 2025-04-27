package account.auth.repository;

import account.auth.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findByName(@NonNull Group.RoleType name);

    boolean existsByName(@NonNull Group.RoleType name);
}