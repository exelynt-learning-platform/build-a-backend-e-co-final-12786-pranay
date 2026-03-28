package com.service.backend_service.service.impl;

import com.service.backend_service.model.Product;
import com.service.backend_service.service.StockValidationService;
import org.springframework.stereotype.Service;

@Service
public class StockValidationServiceImpl implements StockValidationService {

    @Override
    public boolean hasSufficientStock(Product product, Integer requestedQuantity) {
        return product != null
                && product.getStockQuantity() != null
                && requestedQuantity != null
                && requestedQuantity > 0
                && requestedQuantity <= product.getStockQuantity();
    }

    @Override
    public boolean canIncreaseQuantity(Product product, Integer existingQuantity, Integer requestedQuantityDelta) {
        if (product == null
                || product.getStockQuantity() == null
                || requestedQuantityDelta == null
                || requestedQuantityDelta <= 0) {
            return false;
        }
        int currentQuantity = existingQuantity == null ? 0 : existingQuantity;
        return currentQuantity + requestedQuantityDelta <= product.getStockQuantity();
    }
}
