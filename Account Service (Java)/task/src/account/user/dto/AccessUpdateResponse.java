package account.user.dto;

public record AccessUpdateResponse(String status) {
    public static AccessUpdateResponse success(String email, AccessUpdateRequest.Operation operation) {
        return new AccessUpdateResponse("User " + email +
                (operation == AccessUpdateRequest.Operation.LOCK ? " locked!" : " unlocked!"));
    }
}
