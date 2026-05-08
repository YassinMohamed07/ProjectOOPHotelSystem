package utils;
import database.HotelDatabase;
import exceptions.*;
import java.time.LocalDate;
import java.time.Period;
import models.Guest;

public class ValidationUtil {

    //Validates that a password meets strength requirements:
    // minimum 8 chars, at least one uppercase, one lowercase,
    //one digit, and one special character.

    public static void validatePassword(String password) throws WeakPwordException {
        if (password == null || password.length() < 8) {throw new WeakPwordException("Password must be at least 8 characters long");}
        if (!containsMatch(password, Character::isUpperCase)) {throw new WeakPwordException("Password must contain at least one uppercase letter (A-Z)");}
        if (!containsMatch(password, Character::isLowerCase)) {throw new WeakPwordException("Password must contain at least one lowercase letter (a-z)");}
        if (!containsMatch(password, Character::isDigit)) {throw new WeakPwordException("Password must contain at least one number (0-9)");}
        String specialChars = "_.@$!%*?&";
        if (!containsMatch(password, c -> specialChars.indexOf(c) >= 0)) {throw new WeakPwordException("Password must contain at least one special character: @$!%*?&");}
    }
    private static boolean containsMatch(String text, CharPredicate predicate) {
        for (char c : text.toCharArray()) {
            if (predicate.test(c)) {
                return true;
            }
        }
        return false;
    }
    @FunctionalInterface
    private interface CharPredicate {
        boolean test(char c);
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

