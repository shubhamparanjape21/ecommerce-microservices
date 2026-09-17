package scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.japes.paymentservice.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//Periodically catches payments the webhook never confirmed. This is the
//self-heal path: the webhook stays the primary, fast path for marking
//payments SUCCESS/FAILED, but if it's lost (endpoint down, tunnel down,
//Razorpay delivery failure) this job asks Razorpay directly instead of
//leaving the payment - and the customer's order - stuck in PENDING forever.

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentReconciliationScheduler {
	private final PaymentService paymentService;
	 
	@Scheduled(fixedDelayString = "${payment.reconciliation.fixed-delay-ms:60000}")
	public void run() {
		try {
			paymentService.reconcilePendingPayments();
		} catch (Exception ex) {
			// A scheduled method that throws stops future executions in some
			// configurations - never let one bad run kill the job permanently.
			log.error("Payment reconciliation run failed unexpectedly", ex);
		}
	}
}
