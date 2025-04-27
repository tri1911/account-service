package account.auth.event;

import account.auth.model.SecurityAction;
import account.auth.service.AuthService;
import account.auth.service.LoginAttemptService;
import account.auth.service.SecurityEventService;
import account.user.model.AppUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SecurityEventListener {

    private final HttpServletRequest httpServletRequest;
    private final LoginAttemptService loginAttemptService;
    private final SecurityEventService securityEventService;
    private final AuthService authService;

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
        String email = extractEmailFromAuthEvent(event);
        loginAttemptService.loginFailed(email);
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        String email = extractEmailFromAuthEvent(event);
        loginAttemptService.loginSucceed(email);
    }

    @EventListener
    public void onAccessDenied(AuthorizationDeniedEvent<?> event) {
        log.debug("Listening the AuthorizationDeniedEvent: {}", event.toString());
        AppUser authenticatedUser = authService.getCurrentAuthenticatedUser();
        if (authenticatedUser != null) {
            securityEventService.logSecurityEvent(
                    SecurityAction.ACCESS_DENIED,
                    authenticatedUser.getEmail(),
                    httpServletRequest.getRequestURI(),
                    httpServletRequest.getRequestURI()
            );
        }
    }

    private String extractEmailFromAuthEvent(AbstractAuthenticationEvent event) {
        var authentication = event.getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof String email) {
                return email;
            } else if (principal instanceof UserDetails userDetails) {
                return userDetails.getUsername();
            }
        }
        return "Anonymous";
    }
}
