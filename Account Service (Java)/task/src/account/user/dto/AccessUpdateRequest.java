package account.user.dto;

public record AccessUpdateRequest(String user, Operation operation) {
    public enum Operation {
        LOCK, UNLOCK
    }
}
