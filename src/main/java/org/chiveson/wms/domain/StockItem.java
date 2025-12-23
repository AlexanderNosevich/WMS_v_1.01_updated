package org.chiveson.wms.domain;

public class StockItem {
    private final long productId;
    private final long locationId;
    private int quantity;

    public StockItem(long productId, long locationId, int quantity) {
        this.productId = productId;
        this.locationId = locationId;
        this.quantity = quantity;
    }

    public long getProductId() {
        return productId;
    }



    public long getLocationId() {
        return locationId;
    }


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Сток" +
                "Остаток" + quantity +
                ", locationId " + locationId +
                ", productId=" + productId
                ;
    }
}
