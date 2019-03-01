package com.melardev.spring.shoppingcartweb.dtos.response.addresses;

import com.melardev.spring.shoppingcartweb.models.Address;

public class AddressExcludeUserDto {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String city;
    private final String country;
    private final String zipCode;
    private final String streetAddress;

    public AddressExcludeUserDto(Long id, String firstName, String lastName, String country, String city, String streetAddress, String zipCode) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.country = country;
        this.zipCode = zipCode;
        this.streetAddress = streetAddress;
    }

    public static AddressExcludeUserDto build(Address address) {
        return new AddressExcludeUserDto(address.getId(), address.getFirstName(), address.getLastName(), address.getCity(), address.getCountry(), address.getStreetAddress(), address.getZipCode());
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
