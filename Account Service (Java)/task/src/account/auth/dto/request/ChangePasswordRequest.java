package account.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank @Size(min = 12, message = "Password length must be 12 chars minimum!") String new_password
) {
}
