package com.billing.billingapp.auth.dto;

public class AuthResponseDto {

    private String token;
    private String email;
    private String name;
    private String businessName;
    private String address;
    private String phone;
    private String gstNumber;

    public AuthResponseDto(String token, String email, String name,
                           String businessName, String address,
                           String phone, String gstNumber) {
        this.token = token;
        this.email = email;
        this.name = name;
        this.businessName = businessName;
        this.address = address;
        this.phone = phone;
        this.gstNumber = gstNumber;
    }

    public String getToken() { return token; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getBusinessName() { return businessName; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getGstNumber() { return gstNumber; }
}