package com.kaldar.kaldar.utils;
import com.kaldar.kaldar.exceptions.EmptyRequiredFieldException;

public class UtilValidation {

    public static void validateCustomerRequiredInput(String firstName, String lastName, String email,
                                                     String phoneNumber, String address, String password) {
        if (firstName == null || firstName.trim().isEmpty())throw new EmptyRequiredFieldException("First name is required");
        if (lastName == null ||lastName.trim().isEmpty())throw new EmptyRequiredFieldException("Last name is required");
        if (email == null|| email.trim().isEmpty())throw new EmptyRequiredFieldException("Email address is required");
        if (address == null|| address.trim().isEmpty())throw new EmptyRequiredFieldException("Address is required");
        if (phoneNumber == null|| phoneNumber.trim().isEmpty())throw new EmptyRequiredFieldException("Phone number is required");
        if (password == null|| password.trim().isEmpty())throw new EmptyRequiredFieldException("Password is required");
    }
}
