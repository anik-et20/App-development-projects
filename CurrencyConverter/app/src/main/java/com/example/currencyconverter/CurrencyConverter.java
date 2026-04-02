package com.example.currencyconverter;
import java.util.HashMap;

public class CurrencyConverter {

    private static final HashMap<String, Double> rates = new HashMap<>();

    static {
        rates.put("INR", 1.0);
        rates.put("USD", 0.012);
        rates.put("EUR", 0.011);
        rates.put("JPY", 1.65);
    }

    public static double convert(String from, String to, double amount) {
        double inInr = amount / rates.get(from);
        return inInr * rates.get(to);
    }
}