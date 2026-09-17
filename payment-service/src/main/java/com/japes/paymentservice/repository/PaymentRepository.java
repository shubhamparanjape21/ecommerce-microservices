package com.japes.paymentservice.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.japes.paymentservice.entity.Payment;
import com.japes.paymentservice.enums.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	public Optional<Payment> findByPaymentReference(String paymentReference);
	public Optional<Payment> findByOrderNumber(String orderNumber);
	Page<Payment> findByPaymentStatus(PaymentStatus status, Pageable pageable);
	Optional<Payment> findByRazorpayOrderId(String razorpayOrderId);
	// Atomic, single round-trip update guarded by the WHERE clause -
	// avoids the read-then-write race window that a find + check + save has.
	// Only succeeds (returns 1) if the payment is still in expectedStatus at the
	// moment the UPDATE runs, so two webhook deliveries (or any two writers)
	// racing each other can never both "win" - the second one gets 0 rows
	// affected and knows to skip its side-effects (order-service notification etc.)
	@Modifying
	@Query("UPDATE Payment p SET p.paymentStatus = :newStatus, p.transactionId = :transactionId "
			+ "WHERE p.razorpayOrderId = :razorpayOrderId AND p.paymentStatus = :expectedStatus")
	int updateStatusIfCurrentStatus(@Param("razorpayOrderId") String razorpayOrderId,
			@Param("expectedStatus") PaymentStatus expectedStatus,
			@Param("newStatus") PaymentStatus newStatus,
			@Param("transactionId") String transactionId);
	
	// Used by the reconciliation job: payments that have a Razorpay order
	// (so checkout was actually opened) but have sat in PENDING past the
	// cutoff - i.e. the webhook should have arrived by now and didn't.
	List<Payment> findByPaymentStatusAndRazorpayOrderIdIsNotNullAndUpdatedAtBefore(
			PaymentStatus status, LocalDateTime cutoff);
}
