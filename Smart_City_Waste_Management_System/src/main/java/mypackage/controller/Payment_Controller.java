
package mypackage.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mypackage.model.Payment;
import mypackage.service.Payment_Service;

@RestController
@RequestMapping("/api/payments")
public class Payment_Controller {

    @Autowired
    private Payment_Service paymentService;

    // Insert new payment
    @PostMapping
    public Payment savePayment(@RequestBody Payment payment) {
        return paymentService.savePayment(payment);
    }

    // Get all payments
    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    // Get payment by ID
    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPaymentById(
            @PathVariable Long paymentId) {

        return paymentService.getPaymentById(paymentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update payment by ID
    @PutMapping("/{paymentId}")
    public ResponseEntity<Payment> updatePayment(
            @PathVariable Long paymentId,
            @RequestBody Payment payment) {

        return paymentService.getPaymentById(paymentId)
                .map(existingPayment -> {
                    payment.setPaymentId(paymentId);
                    return ResponseEntity.ok(
                            paymentService.savePayment(payment));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete payment by ID
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> deletePayment(
            @PathVariable Long paymentId) {

        return paymentService.getPaymentById(paymentId)
                .map(existingPayment -> {
                    paymentService.deletePayment(paymentId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
