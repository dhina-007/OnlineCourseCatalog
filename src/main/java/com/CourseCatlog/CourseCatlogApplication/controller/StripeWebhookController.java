package com.CourseCatlog.CourseCatlogApplication.controller;

import com.CourseCatlog.CourseCatlogApplication.config.properties.StripeProperties;
import com.CourseCatlog.CourseCatlogApplication.service.OrderService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhooks/stripe")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirements(value = {})
public class StripeWebhookController {

    private final StripeProperties stripeProperties;
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> handle(@RequestBody String payload,
                                         @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader) {
        if (!StringUtils.hasText(stripeProperties.getWebhookSecret())) {
            log.warn("Stripe webhook secret not configured; ignoring webhook");
            return ResponseEntity.ok("ignored");
        }
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, stripeProperties.getWebhookSecret());
            if ("payment_intent.succeeded".equals(event.getType())) {
                event.getDataObjectDeserializer().getObject().ifPresent(obj -> {
                    if (obj instanceof PaymentIntent intent) {
                        orderService.confirmStripePayment(intent.getId());
                    }
                });
            }
            return ResponseEntity.ok("ok");
        } catch (SignatureVerificationException e) {
            log.warn("Invalid Stripe signature");
            return ResponseEntity.badRequest().body("invalid signature");
        }
    }
}
