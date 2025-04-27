package account.payment.service;

import account.auth.service.AuthService;
import account.common.exception.ResourceNotFound;
import account.payment.dto.response.PaymentSummary;
import account.payment.dto.request.PaymentDto;
import account.payment.dto.response.PayrollUpdateResponse;
import account.payment.dto.response.PayrollsUploadResponse;
import account.payment.exception.EmployeePeriodDuplicateException;
import account.payment.exception.InvalidPaymentPeriodException;
import account.payment.model.Payment;
import account.payment.repository.PaymentRepository;
import account.user.model.AppUser;
import account.user.service.UserService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class PaymentService {

    private final UserService userService;
    private final PaymentRepository paymentRepository;
    private final AuthService authService;

    public PaymentService(UserService userService, PaymentRepository paymentRepository, AuthService authService) {
        this.userService = userService;
        this.paymentRepository = paymentRepository;
        this.authService = authService;
    }

    /**
     * If data is missing, the service should return an empty JSON array or an empty JSON, respectively
     */
    @Transactional(readOnly = true)
    public Object getPayment(@Nullable String period) {
        AppUser authenticatedUser = authService.getCurrentAuthenticatedUser();
        if (period != null) {
            validatePeriodFormat(period);
            return getEmployeePaymentsByPeriod(authenticatedUser, period);
        } else {
            return getEmployeePayments(authenticatedUser);
        }
    }

    /**
     * Verify if the period format is valid. Should be 'mm-YYYY' (for example, 01-2021).
     *
     * @throws IllegalArgumentException if the period format is invalid.
     */
    private void validatePeriodFormat(@NotNull String period) {
        if (!period.matches("^[0-9]{2}-[0-9]{4}$")) {
            throw new InvalidPaymentPeriodException("Invalid period format. Use mm-YYYY (for example, 01-2021)");
        }

        // Additional validation for the month range (1-12)
        String[] parts = period.split("-");
        int month = Integer.parseInt(parts[0]);

        if (month < 1 || month > 12) {
            throw new InvalidPaymentPeriodException("Month should be between 1 and 12");
        }
    }

    private PaymentSummary getEmployeePaymentsByPeriod(@NotNull AppUser employee, @NotNull String period) {
        return paymentRepository.findByEmployeeAndPeriod(employee, period)
                .map(PaymentSummary::from)
                .orElse(null);
    }

    /**
     * Return information about the employee's salary for each period from the database as an array of objects in
     * descending order by date
     */
    private List<PaymentSummary> getEmployeePayments(@NotNull AppUser employee) {
        return paymentRepository.findByEmployeeOrderByPeriodDesc(employee).stream()
                .map(PaymentSummary::from)
                .toList();
    }

    @Transactional
    public PayrollsUploadResponse uploadPayrolls(List<PaymentDto> payments) {
        validatePayments(payments);
        savePayments(payments);
        return PayrollsUploadResponse.succeed();
    }

    /**
     * Check the payments, throw exceptions if needed:
     * <li>The employee-period pair must be unique</li>
     * <li>The employee must be among the users of our service</li>
     *
     * @param payments The payments to be checked.
     * @throws ResourceNotFound if the employee does not exist.
     * @throws EmployeePeriodDuplicateException if the employee-period pair already exists in the database.
     */
    private void validatePayments(List<PaymentDto> payments) {
        Map<String, Set<String>> employeePeriods = new HashMap<>();

        payments.forEach(p -> {
            String email = p.employee();
            String period = p.period();

            employeePeriods.computeIfAbsent(email, k -> new HashSet<>());

            if (!employeePeriods.get(email).add(period)) {
                throw new EmployeePeriodDuplicateException(email, period);
            }

            if (!userService.checkUserExistByEmail(email)) {
                throw new ResourceNotFound("User not found: " + email);
            }
        });
    }

    /**
     * Save the payments into the database.
     *
     * @param payments The payments to be saved.
     */
    private void savePayments(List<PaymentDto> payments) {
        List<Payment> paymentEntities = payments.stream()
                .map(p -> {
                    // FIXME: If Spring automatically cache fetched `User` by email?
                    AppUser employee = userService.getUserByEmail(p.employee());
                    // FIXME: What if the employee and period pair already exist in the database?
                    return new Payment(employee, p.period(), p.salary());
                })
                .toList();
        paymentRepository.saveAll(paymentEntities);
    }

    @Transactional
    public PayrollUpdateResponse updatePayment(PaymentDto newPayment) {
        AppUser employee = userService.getUserByEmail(newPayment.employee());
        Payment existingPayment = paymentRepository.findByEmployeeAndPeriod(employee, newPayment.period())
                .orElseThrow();
        existingPayment.setSalary(newPayment.salary());
        paymentRepository.save(existingPayment);
        return PayrollUpdateResponse.succeed();
    }
}
