package com.service.backend_service.service.impl;

import com.service.backend_service.service.PriceCalculationService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Service;

@Service
public class PriceCalculationServiceImpl implements PriceCalculationService {

    @Override
    public BigDecimal calculateTotalPrice(Integer quantity, Double unitPrice) {
        return BigDecimal.valueOf(unitPrice)
                .multiply(BigDecimal.valueOf(quantity.longValue()))
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean matchesExpectedTotal(Double providedTotalPrice, BigDecimal calculatedTotalPrice) {
        if (providedTotalPrice == null) {
            return false;
        }
        BigDecimal providedTotal = BigDecimal.valueOf(providedTotalPrice).setScale(2, RoundingMode.HALF_UP);
        return providedTotal.compareTo(calculatedTotalPrice) == 0;
    }
}
