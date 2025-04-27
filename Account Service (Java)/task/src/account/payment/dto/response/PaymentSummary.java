package account.payment.dto.response;

import account.payment.model.Payment;
import account.user.model.AppUser;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public record PaymentSummary(
        String name, // user name
        String lastname, // user lastname
        String period, // name of month-YYYY (for example, January-2021)
        String salary // X dollar(s) Y cent(s) (for example, 1234 dollar(s) and 56 cent(s))
) {

    public static PaymentSummary from(Payment payment) {
        AppUser employee = payment.getEmployee();
        return new PaymentSummary(
                employee.getName(),
                employee.getLastname(),
                getFormattedPeriod(payment.getPeriod()),
                getFormattedSalary(payment.getSalary())
        );
    }

    /**
     * From '01-2021' to 'January-2021'
     */
    private static String getFormattedPeriod(String period) {
        String[] parts = period.split("-");
        int month = Integer.parseInt(parts[0]);
        String year = parts[1];

        String monthName = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        return monthName + "-" + year;
    }

    /**
     * From '123456' to '1234 dollar(s) and 56 cent(s)'
     */
    private static String getFormattedSalary(long salary) {
        long dollars = salary / 100;
        long cents = salary % 100;

        return dollars + " dollar(s) " + cents + " cent(s)";
    }
}
