package org.chiveson.wms.domain;

public enum Status {
    ACTIVE ("Активен"),
    ARCHIVED ("В архиве");

    private final String description;
    Status (String description) {
        this.description = description;
    }
    public String getDescription () {
        return description;
    }

    @Override
    public String toString() {
        return "Статус: " + description;
    }
}
