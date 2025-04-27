package account.payment.exception;

public class InvalidPaymentPeriodException extends RuntimeException {
    public InvalidPaymentPeriodException(String message) {
        super(message);
    }
}
