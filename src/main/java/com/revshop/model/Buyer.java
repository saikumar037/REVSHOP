package com.revshop.model;

public class Buyer extends User {
    public Buyer() {
        super();
        setUserType(UserType.BUYER);
    }

    public Buyer(String email, String password, String firstName, String lastName,
                 String phone, String address) {
        super(email, password, firstName, lastName, phone, address, UserType.BUYER);
    }
}