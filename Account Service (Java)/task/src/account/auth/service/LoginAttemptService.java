package account.auth.service;

import account.auth.model.SecurityAction;
import account.user.model.AppUser;
import account.user.repository.AppUserRepository;
import account.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@EnableCaching
public class LoginAttemptService {

    private final HttpServletRequest httpServletRequest;
    private final SecurityEventService securityEventService;
    private final UserService userService;
    private final AppUserRepository appUserRepository;

    private static final int MAX_ATTEMPT = 5;

    @Transactional
    public void loginFailed(final String email) {
        log.debug("{} failed to login", email);
        securityEventService.logSecurityEvent(
                SecurityAction.LOGIN_FAILED,
                email,
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRequestURI()
        );

        // FIXME: The real person will suffer?
        AppUser foundedUser = appUserRepository.findByEmailIgnoreCase(email).orElse(null);

        if (foundedUser == null) {
            log.debug("User {} not found in database!", email);
            return;
        } else if(userService.isAdminUser(foundedUser)) {
            log.debug("User {} is an ADMINISTRATOR!", email);
            return;
        }

        int currentFailAttempt = foundedUser.getFailedAttempts();
        foundedUser.setFailedAttempts(currentFailAttempt + 1);

        if (currentFailAttempt + 1 >= MAX_ATTEMPT) {
            log.debug("{} is locked due to multiple failed attempts!", email);
            foundedUser.setNonLocked(false);
            securityEventService.logSecurityEvent(
                    SecurityAction.BRUTE_FORCE,
                    email,
                    httpServletRequest.getRequestURI(),
                    httpServletRequest.getRequestURI()
            );
            // FIXME: Duplicate in userService.lockAccount() service method
            securityEventService.logSecurityEvent(
                    SecurityAction.LOCK_USER,
                    email,
                    "Lock user " + email.toLowerCase(),
                    httpServletRequest.getRequestURI()
            );
        }
        appUserRepository.save(foundedUser);
    }

    @Transactional
    public void loginSucceed(String email) {
        log.debug("{} login successfully!", email);
        AppUser foundedUser = userService.getUserByEmail(email);
        if (foundedUser.getFailedAttempts() > 0) {
            foundedUser.setFailedAttempts(0);
        }
        appUserRepository.save(foundedUser);
    }
}
