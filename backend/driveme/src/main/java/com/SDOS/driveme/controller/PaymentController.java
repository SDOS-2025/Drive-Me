package com.SDOS.driveme.controller;
// package com.example.driveme.controller;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.example.driveme.model.Payment;
// import com.example.driveme.repository.PaymentRepository;

// import java.util.List;
// import java.util.Optional;

// @RestController
// @RequestMapping("/payments")
// public class PaymentController {
//     private final PaymentRepository paymentRepository;

//     public PaymentController(PaymentRepository paymentRepository) {
//         this.paymentRepository = paymentRepository;
//     }

//     // 1. Create a new payment
//     @PostMapping
//     public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
//         Payment savedPayment = paymentRepository.save(payment);
//         return ResponseEntity.status(HttpStatus.CREATED).body(savedPayment);
//     }

//     // 2. Get a payment by ID
//     @GetMapping("/{id}")
//     public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
//         Optional<Payment> payment = paymentRepository.findById(id);
//         return payment.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
//     }

//     // 3. Get all payments
//     @GetMapping
//     public List<Payment> getAllPayments() {
//         return paymentRepository.findAll();
//     }

//     // // 4. Get payments by booking ID
//     // @GetMapping("/booking/{bookingId}")
//     // public List<Payment> getPaymentsByBookingId(@PathVariable Long bookingId) {
//     //     return paymentRepository.findByBookingBookingId(bookingId);
//     // }

//     // 5. Update a payment
//     @PutMapping("/{id}")
//     public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @RequestBody Payment updatedPayment) {
//         return paymentRepository.findById(id)
//             .map(existingPayment -> {
//                 existingPayment.setStatus(updatedPayment.getStatus());
//                 existingPayment.setPaymentMethod(updatedPayment.getPaymentMethod());
//                 return ResponseEntity.ok(paymentRepository.save(existingPayment));
//             })
//             .orElse(ResponseEntity.notFound().build());
//     }

//     // 6. Delete a payment
//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
//         if (paymentRepository.existsById(id)) {
//             paymentRepository.deleteById(id);
//             return ResponseEntity.noContent().build();
//         } else {
//             return ResponseEntity.notFound().build();
//         }
//     }
// }