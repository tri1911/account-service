package account.common.exception;

import lombok.Builder;

import java.util.Date;

// FIXME: timestamp = LocalDateTime
@Builder
public record ErrorResponse(
        Date timestamp, // When it happened
        int status, // HTTP status code (e.g., 404, 500)
        String error, // Reason phrase (e.g., "Not Found")
        String message, // Custom message
        String path // Requested URL
) {
}
