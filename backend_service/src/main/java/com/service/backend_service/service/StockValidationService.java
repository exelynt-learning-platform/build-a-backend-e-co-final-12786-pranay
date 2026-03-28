package com.service.backend_service.service;

import com.service.backend_service.model.Product;

public interface StockValidationService {

    boolean hasSufficientStock(Product product, Integer requestedQuantity);
}
