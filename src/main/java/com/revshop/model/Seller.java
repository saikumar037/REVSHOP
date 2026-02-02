package com.revshop.model;

public class Seller extends User {
    private String businessName;
    private String businessAddress;
    private String taxId;
    private String businessPhone;

    public Seller() {
        super();
        setUserType(UserType.SELLER);
    }

    public Seller(String email, String password, String firstName, String lastName,
                  String phone, String address, String businessName,
                  String businessAddress, String taxId, String businessPhone) {
        super(email, password, firstName, lastName, phone, address, UserType.SELLER);
        this.businessName = businessName;
        this.businessAddress = businessAddress;
        this.taxId = taxId;
        this.businessPhone = businessPhone;
    }

    // Getters and Setters
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getBusinessAddress() { return businessAddress; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public String getBusinessPhone() { return businessPhone; }
    public void setBusinessPhone(String businessPhone) { this.businessPhone = businessPhone; }

    @Override
    public String toString() {
        return "Seller{" +
                "userId=" + getUserId() +
                ", email='" + getEmail() + '\'' +
                ", businessName='" + businessName + '\'' +
                '}';
    }
}