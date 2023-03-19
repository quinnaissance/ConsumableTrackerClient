package ca.cmpt213.a4.client.model;

import java.time.LocalDate;

/**
 * Factory class to produce Consumable objects (Food/Drink)
 * @author Steven Quinn (301462499) – CMPT 213 D100 – Fall 2021
 */
public class ConsumableFactory {

    /**
     * Return a Consumable Food or Drink object
     * @param type "food" or "drink"
     * @return Consumable Food/Drink object
     */
    public static Consumable getInstance(String type, String name, String notes, double price,
                                         double measurement, LocalDate expiry) {
        // Get food or drink
        return switch (type.toLowerCase()) {
            case "food" -> new Food(name, notes, price, measurement, expiry);
            case "drink" -> new Drink(name, notes, price, measurement, expiry);
            default -> null;
        };
    }
}
