package account.auth.controller;

import account.auth.dto.response.SecurityEventResponse;
import account.auth.service.SecurityEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/security/events")
public class SecurityEventController {

    private final SecurityEventService securityEventService;

    @GetMapping("/")
    public List<SecurityEventResponse> getSecurityEvents() {
        return securityEventService.getSecurityEvents();
    }
}
