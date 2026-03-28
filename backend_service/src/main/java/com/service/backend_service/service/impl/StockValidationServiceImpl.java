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
}
