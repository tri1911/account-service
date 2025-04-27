package account.auth.dto.request;

import account.auth.model.Group;
import jakarta.validation.constraints.NotBlank;

public record UserRolesUpdateRequest(
        @NotBlank String user,
        Group.RoleType role,
        OperationType operation
) {
    public enum OperationType {
        GRANT, REMOVE;
    }
}
