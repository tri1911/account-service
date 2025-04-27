package account.auth.exception.custom;

public class UserExistException extends RuntimeException {

    public UserExistException() {
        super("User exist!");
    }
}
