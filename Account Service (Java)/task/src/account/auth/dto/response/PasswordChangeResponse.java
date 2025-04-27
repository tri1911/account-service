package account.auth.dto.response;

import lombok.Builder;

@Builder
public record PasswordChangeResponse(String email, String status) {
    public static PasswordChangeResponse success(String email) {
        return PasswordChangeResponse.builder()
                .email(email)
                .status("The password has been updated successfully")
                .build();
    }
}