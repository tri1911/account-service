package account.payment.controller;

import account.payment.dto.request.PaymentDto;
import account.payment.dto.response.PayrollUpdateResponse;
import account.payment.dto.response.PayrollsUploadResponse;
import account.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/empl/payment")
    public ResponseEntity<?> getPayment(@RequestParam(required = false) String period) {
        return ResponseEntity.ok(paymentService.getPayment(period));
    }

    @PostMapping("/acct/payments")
    public ResponseEntity<PayrollsUploadResponse> uploadPayrolls(@Valid @RequestBody List<@Valid PaymentDto> payments) {
        return ResponseEntity.ok(paymentService.uploadPayrolls(payments));
    }

    @PutMapping("/acct/payments")
    public ResponseEntity<PayrollUpdateResponse> updatePaymentInfo(@Valid @RequestBody PaymentDto newPayment) {
        return ResponseEntity.ok(paymentService.updatePayment(newPayment));
    }
}
