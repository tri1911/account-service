package account.auth.exception.custom;

public class BreachPasswordException extends RuntimeException {

    public BreachPasswordException() {
        super("The password is in the hacker's database!");
    }
}
