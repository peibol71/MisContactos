package com.appsguays.miscontactos;


public class Contacto {
    private String name;
    private String phone;
    private String id;

    // constructor
    public Contacto(String contactId, String contactPhone, String contactName) {
        this.id = contactId;
        this.phone = contactPhone;
        this.name = contactName;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }
}
