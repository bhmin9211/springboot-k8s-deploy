package com.example.demo.payment.entity;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public final class PaymentStatusTransitions {

    private static final Map<PaymentStatus, Set<PaymentStatus>> RULES = new EnumMap<>(PaymentStatus.class);

    static {
        RULES.put(PaymentStatus.REQUESTED, EnumSet.of(PaymentStatus.APPROVED, PaymentStatus.REJECTED));
        RULES.put(PaymentStatus.APPROVED, EnumSet.of(PaymentStatus.PROCESSING));
        RULES.put(PaymentStatus.PROCESSING, EnumSet.of(PaymentStatus.SUCCESS, PaymentStatus.FAILED));
        RULES.put(PaymentStatus.FAILED, EnumSet.of(PaymentStatus.RETRYING));
        RULES.put(PaymentStatus.RETRYING, EnumSet.of(PaymentStatus.SUCCESS, PaymentStatus.FAILED));
        RULES.put(PaymentStatus.SUCCESS, EnumSet.of(PaymentStatus.SETTLED));
        RULES.put(PaymentStatus.REJECTED, EnumSet.noneOf(PaymentStatus.class));
        RULES.put(PaymentStatus.SETTLED, EnumSet.noneOf(PaymentStatus.class));
    }

    private PaymentStatusTransitions() {
    }

    public static boolean canTransit(PaymentStatus from, PaymentStatus to) {
        return RULES.getOrDefault(from, Set.of()).contains(to);
    }
}
