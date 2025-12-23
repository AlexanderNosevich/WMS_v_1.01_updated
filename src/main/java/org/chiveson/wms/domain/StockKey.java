package org.chiveson.wms.domain;

public record StockKey(long productId, long locationId) {
    public StockKey {
        if (productId <= 0) throw new IllegalArgumentException("ID продукта не может быть 0 или меньше");
        if (locationId <= 0) throw new IllegalArgumentException("ID ячейки не может быть 0 или меньше");
    }
}
