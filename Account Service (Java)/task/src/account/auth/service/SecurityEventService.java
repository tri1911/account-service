package account.auth.service;

import account.auth.dto.response.SecurityEventResponse;
import account.auth.model.SecurityAction;
import account.auth.model.SecurityEvent;
import account.auth.repository.SecurityEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SecurityEventService {

    private final SecurityEventRepository securityEventRepository;

    public List<SecurityEventResponse> getSecurityEvents() {
        return securityEventRepository.findAll(Sort.by(Sort.Direction.ASC, "eventId")).stream()
                .map(SecurityEventResponse::from)
                .toList();
    }

    @Transactional
    public void logSecurityEvent(SecurityAction action, String subject, String object, String path) {
        securityEventRepository.save(SecurityEvent.builder()
                .date(LocalDateTime.now())
                .action(action)
                .subject(subject)
                .object(object)
                .path(path)
                .build());
    }
}
