package org.chiveson.wms.domain;

//Класс для ячейки/полки
public class Location {
    private final long id;
    private String code;

    //Конструктор
    public Location (long id, String code) {
        this.id = id;
        this.code = code;
    }

    public Long getId() {
        return id;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
