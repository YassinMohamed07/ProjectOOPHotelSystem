package utils;

import database.HotelDatabase;
import exceptions.*;
import java.time.LocalDate;
import java.time.Period;
import models.Guest;
public class ValidationUtil {
    public static void validatePassword(String password) throws WeakPwordException{
        // Check no1: Not null and minimum length
        if (password == null || password.length() < 8) {
            throw new WeakPwordException("Password must be at least 8 characters long");
        }

        // Check no2: has at least one uppercase letter
        boolean hasUppercase = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
                break;
            }
        }
        if (!hasUppercase) {
            throw new WeakPwordException("Password must contain at least one uppercase letter (A-Z)");
        }

        // Check no3: Has at least one lowercase letter
        boolean hasLowercase = false;
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                hasLowercase = true;
                break;
            }
        }
        if (!hasLowercase) {
            throw new WeakPwordException("Password must contain at least one lowercase letter (a-z)");
        }

        // Check no4: Has at least one digit
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigit = true;
                break;
            }
        }
        if (!hasDigit) {
            throw new WeakPwordException("Password must contain at least one number (0-9)");
        }

        // Check no5: Has at least one special character
        String specialChars = "@$!%*?&";
        boolean hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (specialChars.indexOf(c) >= 0) {
                hasSpecial = true;
                break;
            }
        }
        if (!hasSpecial) {
            throw new WeakPwordException("Password must contain at least one special character: @$!%*?&");
        }
    }

    public static void validateDateOfBirth(LocalDate dob) throws InvalidDateException {
        if (dob == null) {
            throw new InvalidDateException("Birth date required");
        }
        if (dob.isAfter(LocalDate.now())) {
            throw new InvalidDateException("Birth date cannot be in the future");
        }
        int age = Period.between(dob, LocalDate.now()).getYears();
        if (age < 18) {
            throw new InvalidDateException("Must be 18+ to register (you are " + age + ")");
        }
    }

    public static void validateUsername(String username) throws WeakPwordException {
        if (username == null || username.trim().length() < 3) {
            throw new WeakPwordException("Username must be 3+ characters");
        }
        // Check if username already exists in database
        if (HotelDatabase.guests != null) {
            for (Guest g : HotelDatabase.guests) {
                if (g.getUsername().equalsIgnoreCase(username)) {
                    throw new WeakPwordException("Username '" + username + "' already taken");
                }
            }
        }
    }
}

