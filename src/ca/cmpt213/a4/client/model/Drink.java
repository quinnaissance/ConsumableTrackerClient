package ca.cmpt213.a4.client.model;

import java.text.DecimalFormat;
import java.time.LocalDate;

/**
 * Drink object extending Consumable
 * @author Steven Quinn (301462499) – CMPT 213 D100 – Fall 2021
 */
public class Drink extends Consumable {

    private double volume;

    /**
     * Default constructor
     */
    public Drink() {
        this("", "", 1, 1, LocalDate.now());
    }

    /**
     * Constructor
     * @param name Name of item
     * @param notes Description of item
     * @param price Price of item
     * @param volume Volume of item
     * @param expiry Date of expiry as LocalDate object
     */
    public Drink(String name, String notes, double price, double volume, LocalDate expiry) {
        this.setName(name);
        this.setNotes(notes);
        this.setPrice(price);
        this.setVolume(volume);
        this.setExpiryDate(expiry);
    }

    /**
     * Sets all params used in constructor
     * @param name Name of item
     * @param notes Description of item
     * @param price Price of item
     * @param volume Volume of item
     * @param expiry Date of expiry as LocalDate object
     */
    public void setParams(String name, String notes, double price, double volume, LocalDate expiry) {
        this.setName(name);
        this.setNotes(notes);
        this.setPrice(price);
        this.setVolume(volume);
        this.setExpiryDate(expiry);
    }

    /**
     * Get the volume of the drink
     * @return Item volume
     */
    public double getVolume() {
        return volume;
    }

    /**
     * Get volume of item formatted and rounded to 2 decimal places
     * @return Formatted volume
     */
    public String getVolumeFormatted() {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(Math.round(getVolume() * 100.0) / 100.0);
    }

    /**
     * Set the volume of the item
     * @param volume Item volume
     */
    public void setVolume(double volume) {
        this.volume = volume;
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
                + "\nVolume: " + getVolumeFormatted()
                + "\nExpiry date: " + getExpiryDate().toString()
                + "\nThis drink item " + timeUntilExpiryString();
    }

    /**
     * Overrided Object.equals method to make List.contains work properly
     * @param o Other object
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof Drink
                && ((Drink) o).getName().equals(getName())
                && ((Drink) o).getNotes().equals(getNotes())
                && ((Drink) o).getPrice() == (getPrice())
                && ((Drink) o).getVolume() == (getVolume())
                && ((Drink) o).getExpiryDate().equals(getExpiryDate());
    }

}