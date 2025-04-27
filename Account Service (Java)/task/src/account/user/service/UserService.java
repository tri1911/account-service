package account.user.service;

import account.auth.dto.response.UserSummary;
import account.auth.dto.request.UserRolesUpdateRequest;
import account.auth.exception.custom.ForbiddenOperationException;
import account.auth.exception.custom.InvalidRoleOperationException;
import account.auth.model.Group;
import account.auth.model.SecurityAction;
import account.auth.service.AuthService;
import account.auth.service.GroupService;
import account.auth.service.SecurityEventService;
import account.common.exception.ResourceNotFound;
import account.user.dto.AccessUpdateRequest;
import account.user.model.AppUser;
import account.user.repository.AppUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class UserService {

    private final HttpServletRequest httpServletRequest;
    private final AppUserRepository userRepository;
    private final AuthService authService;
    private final GroupService groupService;
    private final SecurityEventService securityEventService;

    public boolean checkUserExistByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    /**
     * Get the user by email.
     *
     * @param email The email of the user. The email should be in lower case.
     * @return The user with the given email.
     * @throws ResourceNotFound if the user does not exist.
     */
    public AppUser getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFound("User not found!"));
    }

    @Transactional(readOnly = true)
    public List<UserSummary> getAllUsers() {
        List<AppUser> foundUsers = userRepository.findAll();
        return foundUsers.stream()
                .map(UserSummary::from)
                .toList();
    }

    public boolean isAdminUser(AppUser user) {
        return user.getRoles().stream()
                .anyMatch(r -> r.getName() == Group.RoleType.ADMINISTRATOR);
    }

    @Transactional
    public void deleteUserByEmail(@NonNull String email) {
        AppUser authenticatedUser = authService.getCurrentAuthenticatedUser();
        AppUser foundUser = getUserByEmail(email);

        if (isAdminUser(foundUser)) {
            throw new ForbiddenOperationException("Can't remove ADMINISTRATOR role!");
        }

        securityEventService.logSecurityEvent(
                SecurityAction.DELETE_USER,
                authenticatedUser.getEmail().toLowerCase(),
                foundUser.getEmail(),
                httpServletRequest.getRequestURI()
        );

        userRepository.delete(foundUser);
    }

    @Transactional
    public AppUser updateUserRoles(@NonNull UserRolesUpdateRequest request) {
        var operation = request.operation();
        var requestedRole = request.role();

        AppUser foundUser = getUserByEmail(request.user());
        boolean isAdminUser = isAdminUser(foundUser);
        Set<Group.RoleType> userRoles = foundUser.getRoles().stream()
                .map(Group::getName)
                .collect(Collectors.toSet());
        log.debug("Roles (before): {}", userRoles);

        if (operation == UserRolesUpdateRequest.OperationType.REMOVE) {
            if (isAdminUser) {
                throw new InvalidRoleOperationException("Can't remove ADMINISTRATOR role!");
            } else if (!userRoles.contains(requestedRole)) {
                throw new InvalidRoleOperationException("The user does not have a role!");
            } else if (foundUser.getRoles().size() == 1) {
                throw new InvalidRoleOperationException("The user must have at least one role!");
            }
        } else if (operation == UserRolesUpdateRequest.OperationType.GRANT) {
            if (isAdminUser || requestedRole == Group.RoleType.ADMINISTRATOR) {
                throw new InvalidRoleOperationException("The user cannot combine administrative and business roles!");
            }
        }

        Group foundRole = groupService.getGroupByName(requestedRole);
        return switch (request.operation()) {
            case GRANT -> assignUserRole(foundUser, foundRole);
            case REMOVE -> removeUserRole(foundUser, foundRole);
        };
    }

    private AppUser assignUserRole(AppUser foundUser, Group foundRole) {
        if (foundUser.getRoles().add(foundRole)) {
            AppUser authenticatedUser = authService.getCurrentAuthenticatedUser();
            securityEventService.logSecurityEvent(
                    SecurityAction.GRANT_ROLE,
                    authenticatedUser.getEmail(),
                    "Grant role " + foundRole.getName() + " to " + foundUser.getEmail().toLowerCase(),
                    httpServletRequest.getRequestURI()
            );
            log.debug("Roles (after granting role): {}", foundUser.getRoles());
            return userRepository.save(foundUser);
        } else {
            log.debug("Role {} already exists within user {}.", foundRole.getName(), foundUser.getEmail());
            throw new InvalidRoleOperationException("Role already exists");
        }
    }

    private AppUser removeUserRole(AppUser foundUser, Group foundRole) {
        if (foundUser.getRoles().remove(foundRole)) {
            AppUser authenticatedUser = authService.getCurrentAuthenticatedUser();
            securityEventService.logSecurityEvent(
                    SecurityAction.REMOVE_ROLE,
                    authenticatedUser.getEmail(),
                    "Remove role " + foundRole.getName() + " from " + foundUser.getEmail().toLowerCase(),
                    httpServletRequest.getRequestURI()
            );
            log.debug("Roles (after removing role): {}", foundUser.getRoles());
            return userRepository.save(foundUser);
        } else {
            log.debug("Role {} not found for user {}", foundRole.getName(), foundUser.getEmail());
            throw new InvalidRoleOperationException("The user does not have a role!");
        }
    }

    @Transactional
    public void updateUserAccess(@NonNull AccessUpdateRequest request) {
        switch (request.operation()) {
            case LOCK:
                lockUserAccount(request.user());
                break;
            case UNLOCK:
                unlockUserAccount(request.user());
                break;
        }
    }

    public void lockUserAccount(String email) {
        AppUser user = getUserByEmail(email);

        if (isAdminUser(user)) {
            throw new ForbiddenOperationException("Can't lock the ADMINISTRATOR!");
        }

        user.setNonLocked(false);
        userRepository.save(user);

        securityEventService.logSecurityEvent(
                SecurityAction.LOCK_USER,
                email,
                "Lock user " + email.toLowerCase(),
                httpServletRequest.getRequestURI()
        );
    }

    private void unlockUserAccount(String email) {
        AppUser foundUser = getUserByEmail(email);
        foundUser.setNonLocked(true);
        foundUser.setFailedAttempts(0);
        userRepository.save(foundUser);

        securityEventService.logSecurityEvent(
                SecurityAction.UNLOCK_USER,
                authService.getCurrentAuthenticatedUser().getEmail(),
                "Unlock user " + email.toLowerCase(),
                httpServletRequest.getRequestURI()
        );
    }
}
