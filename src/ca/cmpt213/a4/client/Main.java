package ca.cmpt213.a4.client;

import ca.cmpt213.a4.client.view.ConsumableViewer;

import javax.swing.*;

/**
 * Contains main method for ConsumableViewer
 * @author Steven Quinn (301462499) – CMPT 213 D100 – Fall 2021
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Run program
            new ConsumableViewer("My Consumables Tracker");
        });
    }
}