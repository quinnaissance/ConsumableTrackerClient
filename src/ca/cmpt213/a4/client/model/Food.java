package ca.cmpt213.a4.client.model;

import java.text.DecimalFormat;
import java.time.LocalDate;

/**
 * Food object extending Consumable
 * @author Steven Quinn (301462499) – CMPT 213 D100 – Fall 2021
 */
public class Food extends Consumable {

    private double weight;

    /**
     * Default constructor
     */
    public Food() {
        this("", "", 1, 1, LocalDate.now());
    }

    /**
     * Constructor
     * @param name Name of item
     * @param notes Description of item
     * @param price Price of item
     * @param weight Weight of item
     * @param expiry Date of expiry as LocalDate object
     */
    public Food(String name, String notes, double price, double weight, LocalDate expiry) {
        this.setName(name);
        this.setNotes(notes);
        this.setPrice(price);
        this.setWeight(weight);
        this.setExpiryDate(expiry);
    }

    /**
     * Sets all params used in constructor
     * @param name Name of item
     * @param notes Description of item
     * @param price Price of item
     * @param weight Weight of item
     * @param expiry Date of expiry as LocalDate object
     */
    public void setParams(String name, String notes, double price, double weight, LocalDate expiry) {
        this.setName(name);
        this.setNotes(notes);
        this.setPrice(price);
        this.setWeight(weight);
        this.setExpiryDate(expiry);
    }

    /**
     * Get the weight of the item
     * @return weight as double
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Get weight of item formatted and rounded to 2 decimal places
     * @return Formatted weight
     */
    public String getWeightFormatted() {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(Math.round(getWeight() * 100.0) / 100.0);
    }

    /**
     * Set the weight of the item
     * @param weight Item weight
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Formatted as per assignment requirements
     * @return Formatted string
     */
    @Override
    public String toString() {
        return "Name: " + getName()
                + "\nNotes: " + getNotes()
                + "\nPrice: " + getPriceCurrency()
                + "\nWeight: " + getWeightFormatted()
                + "\nExpiry date: " + getExpiryDate().toString()
                + "\nThis food item " + timeUntilExpiryString();
    }

    /**
     * Overrided Object.equals method to make List.contains work properly
     * @param o Other object
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof Food
                && ((Food) o).getName().equals(getName())
                && ((Food) o).getNotes().equals(getNotes())
                && ((Food) o).getPrice() == (getPrice())
                && ((Food) o).getWeight() == (getWeight())
                && ((Food) o).getExpiryDate().equals(getExpiryDate());
    }
}
