
package mypackage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mypackage.model.Payment;
import mypackage.repository.Payment_Repository;

@Service
public class Payment_Service {

    @Autowired
    private Payment_Repository paymentRepository;

    // Insert or update payment
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    // Get all payments
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Get payment by ID
    public Optional<Payment> getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId);
    }

    // Delete payment by ID
    public void deletePayment(Long paymentId) {
        paymentRepository.deleteById(paymentId);
    }
}
