package account.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequest(@NotBlank String name, @NotBlank String lastname,
                            @Email @NotBlank @Pattern(regexp = ".*@acme\\.com$", message = "Email must be from @acme.com domain") String email,
                            @NotBlank @Size(min = 12, message = "Password length must be 12 chars minimum!") String password) {
}
