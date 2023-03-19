package ca.cmpt213.a4.client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Misc UI utilities
 * @author Steven Quinn (301462499) – CMPT 213 D100 – Fall 2021
 */
public class Utilities {

    // Colour scheme
    public static final Color ENABLED_COLOR = Color.white;
    public static final Color DISABLED_COLOR = Color.lightGray;
    public static final Color VALID_COLOR = Color.decode("#228B22");
    public static final Color INVALID_COLOR = Color.decode("#800000");
    public static final Color FLASH_COLOR = Color.decode("#FF7F7F");

    /**
     * Capitalize first letter of String
     * @param input Input String
     * @return Capitalized String
     */
    public static String capitalizeFirstLetter(String input) {
        if (input.length() == 0)
            return "";
        if (input.length() == 1)
            return input.toUpperCase();

        return input.substring(0,1).toUpperCase() + input.substring(1);
    }

    /**
     * Flashing JTextField
     * @param field JTextField
     * @param flashCol Flash color
     * @param delay Time between flashes
     * @param totalTime Total time flashing
     */
    public static void flashingJTextField(JTextField field, Color flashCol, int delay, int totalTime) {
        Color oldBg = field.getBackground();

        int totalCount = totalTime / delay;
        Timer t = new Timer(delay, new ActionListener() {
            int count = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (count % 2 == 0) {
                    field.setBackground(flashCol);
                } else {
                    field.setBackground(null);
                    if (count >= totalCount) {
                        ((Timer) e.getSource()).stop();
                        field.setBackground(oldBg);
                    }
                }
                count++;
            }
        });
        t.start();
    }
}
