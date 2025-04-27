package account.payment.exception;

public class EmployeePeriodDuplicateException extends RuntimeException {
    public EmployeePeriodDuplicateException(String email, String period) {
        super("The employee-period pair duplicate: " + email + " - " + period);
    }
}
