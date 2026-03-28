package com.service.backend_service.service;

import java.math.BigDecimal;

public interface PriceCalculationService {

    BigDecimal calculateTotalPrice(Integer quantity, Double unitPrice);

    boolean matchesExpectedTotal(Double providedTotalPrice, BigDecimal calculatedTotalPrice);
}
