package account.payment.repository;

import account.payment.model.Payment;
import account.user.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByEmployeeOrderByPeriodDesc(AppUser employee);

    Optional<Payment> findByEmployeeAndPeriod(AppUser employee, String period);

    boolean existsByEmployeeAndPeriod(AppUser employee, String period);
}