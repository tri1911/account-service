package account.auth.exception.custom;

public class InvalidRoleOperationException extends RuntimeException {
    public InvalidRoleOperationException(String message) {
        super(message);
    }
}
