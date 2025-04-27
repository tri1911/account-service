package account.auth.dto.response;

import account.auth.model.SecurityAction;
import account.auth.model.SecurityEvent;

import java.time.LocalDateTime;

public record SecurityEventResponse(
        LocalDateTime date,
        SecurityAction action,
        String subject,
        String object,
        String path
) {
    public static SecurityEventResponse from(SecurityEvent event) {
        return new SecurityEventResponse(
                event.getDate(),
                event.getAction(),
                event.getSubject(),
                event.getObject(),
                event.getPath());
    }
}
