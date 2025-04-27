package account.payment.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PaymentDto(
        @NotBlank @Email String employee,
        @Pattern(regexp = "^(0[1-9]|1[0-2])-\\d{4}$",
                message = "Invalid period format. Use MM-YYYY, where MM is between 01 and 12")
        String period, // mm-YYYY format
        @Min(value = 0, message = "The salary must be non-negative") long salary
) {
}
