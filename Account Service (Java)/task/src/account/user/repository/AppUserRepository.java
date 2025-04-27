package account.user.repository;

import account.user.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

    Optional<AppUser> findByEmailIgnoreCase(@NonNull String email);

    boolean existsByEmailIgnoreCase(@NonNull String email);
}