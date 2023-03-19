package ca.cmpt213.a4.client.model;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Abstract class with all fields/methods for Consumable items
 * @author Steven Quinn (301462499) – CMPT 213 D100 – Fall 2021
 */
public abstract class Consumable implements Comparable<Consumable> {

    private String name;
    private String notes;
    private double price;
    private LocalDate expiryDate;

    /**
     * Get the name of the item
     *
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the item
     *
     * @param n Name
     */
    public void setName(String n) {
        name = n;
    }

    /**
     * Get the notes of the item
     *
     * @return Notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Set the notes of the item
     *
     * @param n Notes
     */
    public void setNotes(String n) {
        notes = n;
    }

    /**
     * Get the price of the item
     *
     * @return price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Set price of the item
     *
     * @param p price
     */
    public void setPrice(double p) {
        price = p;
    }

    /**
     * Get price formatted to currency
     * and rounded to two decimal places
     *
     * @return
     */
    public String getPriceCurrency() {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(Math.round(getPrice() * 100.0) / 100.0);
    }

    /**
     * Get the expiry date object for the item
     *
     * @return LocalDate expiry date object
     */
    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    /**
     * Set expiry date of the item
     *
     * @param expiry Expiry date
     */
    public void setExpiryDate(LocalDate expiry) {
        expiryDate = expiry;
    }

    /**
     * Get "food" or "drink" to discern types
     *
     * @return
     */
    public String getCategory() {
        return getClass().getSimpleName();
    }

    /**
     * Relative number of days from today to item expiry date
     * - expired | 0 expires today | + unexpired
     *
     * @return
     */
    public int timeUntilExpiryInt() {
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), getExpiryDate());
    }

    /**
     * Gets the expiry time and formats as String
     * as per assignment requirements for toString()
     *
     * @return
     */
    public String timeUntilExpiryString() {
        // Get number of days between dates
        int daysUntil = timeUntilExpiryInt();

        // Format syntactically as per sample output
        if (daysUntil > 0)
            return "will expire in " + daysUntil + " day(s).";
        else if (daysUntil < 0)
            return "has been expired for " + Math.abs(daysUntil) + " day(s).";
        else
            return "will expire today.";
    }

    /**
     * Convert object to string
     *
     * @return
     */
    @Override
    public abstract String toString();

    /**
     * Convert newlines to HTML line breaks and add tags
     * @return Formatted toString
     */
    public String toStringHtml() {
        return "<html>" + toString().replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\n", "<br/>") + "</html>";
    }

    /**
     * Override compareTo to sort by expiry date
     *
     * @param item Consumable item
     * @return compareTo comparison
     */
    @Override
    public int compareTo(Consumable item) {
        return getExpiryDate().compareTo(item.getExpiryDate());
    }
}
