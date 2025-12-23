package org.chiveson.wms.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Product {
    private final long id;
    private String name;
    private int sku;
    private Status status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    //Конструктор
    public Product (long id, String name, Status status, int sku) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.status = status.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public Long getId () {
        return id;
    }
    public String getName () {
        return name;
    }
    public Integer getSku () {
        return sku;
    }
    public Status getStatus() {
        return status;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setName (String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }
    public void setSku (int sku) {
        this.sku = sku;
        this.updatedAt = LocalDateTime.now();
    }
    public void setStatus(Status status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Product product = (Product) object;
        return id == product.id && sku == product.sku;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sku);
    }

    @Override
    public String toString() {
        return "Товар №" + id + " Наименование: " +
                 name + "\n" + "Артикул: " + sku + "\n"
                + "Создано: " + status + "\n" //+
               // "Остаток: " + quantity
                + createdAt + "\n" + "Обновлено: " + updatedAt;
    }
}
