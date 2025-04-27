package account.auth.service;

import account.auth.dto.request.ChangePasswordRequest;
import account.auth.dto.response.PasswordChangeResponse;
import account.auth.dto.response.UserSummary;
import account.auth.dto.request.SignUpRequest;
import account.auth.exception.custom.AuthenticationRequiredException;
import account.auth.exception.custom.BreachPasswordException;
import account.auth.exception.custom.PasswordMatchException;
import account.auth.exception.custom.UserExistException;
import account.auth.model.AppUserAdapter;
import account.auth.model.Group;
import account.auth.model.SecurityAction;
import account.user.model.AppUser;
import account.user.repository.AppUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthService {

    private final Set<String> BREACHED_PASSWORDS = Set.of("PasswordForJanuary", "PasswordForFebruary",
            "PasswordForMarch", "PasswordForApril", "PasswordForMay", "PasswordForJune", "PasswordForJuly",
            "PasswordForAugust", "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");

    private final HttpServletRequest httpServletRequest;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final GroupService groupService;
    private final SecurityEventService securityEventService;

    @Transactional
    public UserSummary signUp(@NonNull SignUpRequest request) {

        if (appUserRepository.existsByEmailIgnoreCase(request.email())) {
            throw new UserExistException();
        }

        validatePasswordSecurity(request.password());

        // Check if this is the first user by counting existing users
        boolean isFirstUser = appUserRepository.count() == 0;
        log.debug("Registered user is the first user: {}", isFirstUser);

        var newUser = AppUser.builder()
                .email(request.email().toLowerCase())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .lastname(request.lastname())
                .roles(Set.of(groupService.getGroupByName(isFirstUser ? Group.RoleType.ADMINISTRATOR : Group.RoleType.USER)))
                .nonLocked(true)
                .build();

        AppUser savedUser = appUserRepository.save(newUser);

        // FIXME: Manually log the security event, figure out the better approach?
        securityEventService.logSecurityEvent(
                SecurityAction.CREATE_USER,
                "Anonymous",
                request.email().toLowerCase(),
                httpServletRequest.getRequestURI()
        );

        return UserSummary.from(savedUser);
    }

    @Transactional
    public PasswordChangeResponse changePassword(@NonNull ChangePasswordRequest request) {
        AppUser currentUser = getCurrentAuthenticatedUser();

        if (currentUser == null) {
            throw new AuthenticationRequiredException("Authentication required to change password");
        }

        String newPassword = request.new_password();
        validatePasswordSecurity(newPassword);
        validatePasswordDifference(newPassword, currentUser.getPassword());

        currentUser.setPassword(passwordEncoder.encode(newPassword));
        appUserRepository.save(currentUser);

        securityEventService.logSecurityEvent(
                SecurityAction.CHANGE_PASSWORD,
                currentUser.getEmail(),
                currentUser.getEmail(),
                httpServletRequest.getRequestURI()
        );

        return PasswordChangeResponse.success(currentUser.getEmail());
    }

    /**
     * Get the authenticated user from the security context.
     *
     * @return The authenticated user or null if no user is authenticated.
     */
    public AppUser getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return getUserFromAuthentication(authentication);
    }

    private AppUser getUserFromAuthentication(Authentication authentication) {
        if (authentication.getPrincipal() instanceof AppUserAdapter(AppUser user)) {
            return user;
        }
        return null;
    }

    private void validatePasswordDifference(String newPassword, String hashedOldPassword) {
        if (passwordEncoder.matches(newPassword, hashedOldPassword)) {
            throw new PasswordMatchException(PASSWORD_MUST_BE_DIFFERENT_MESSAGE);
        }
    }

    private void validatePasswordSecurity(String password) {
        if (BREACHED_PASSWORDS.contains(password)) {
            throw new BreachPasswordException();
        }
    }

    private static final String PASSWORD_MUST_BE_DIFFERENT_MESSAGE = "The passwords must be different!";
}
