package com.appsguays.miscontactos;


public class Contacto {
    private String name;
    private String phone;
    private String id;
    private String email1;
    private boolean selected;

    // constructor
    Contacto(String contactId, String contactPhone, String contactName, String contactMail) {
        this.id = contactId;
        this.phone = contactPhone;
        this.name = contactName;
        this.email1 = contactMail;
        this.selected = false;
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

    public String getEmail1() { return  email1; }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
}
