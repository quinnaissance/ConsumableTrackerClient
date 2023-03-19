package ca.cmpt213.a4.client.view;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Small banner that displays a task in-progress and then denotes
 * whether said task succeeded or failed
 * @author Steven Quinn (301462499) – CMPT 213 D100 – Fall 2021
 */
public class ServerContactBanner extends JPanel {

    private final Icon LOAD_ICON = new ImageIcon("resources/load4.gif");
    private final String[] BANNER_ELLIPSES = {"", ".", "..", "..."};

    private Font startFont = new Font("Arial", Font.PLAIN, 12);
    private Font endFont = new Font("Arial", Font.BOLD, 14);

    private FlowLayout layout;
    private String startMsg;
    private String successMsg;
    private String failureMsg;
    private Timer ellipsesTimer;

    /**
     * Constructor
     * @param startMsg Starting message
     * @param successMsg Message upon success
     * @param failureMsg Message upon failure
     */
    public ServerContactBanner(String startMsg, String successMsg, String failureMsg) {
        super();
        this.startMsg = startMsg;
        this.successMsg = successMsg;
        this.failureMsg = failureMsg;

        layout = new FlowLayout(FlowLayout.CENTER);
        layout.setHgap(10);
        setLayout(layout);
        setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(0,5,0,5)));

        JLabel loadLabel = new JLabel(LOAD_ICON);
        JLabel bannerMessage = new JLabel(this.startMsg);
        bannerMessage.setFont(startFont);

        add(loadLabel);
        add(bannerMessage);

        ellipsesTimer = new Timer(400, new ActionListener() {

            int index = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                bannerMessage.setText(ServerContactBanner.this.startMsg + BANNER_ELLIPSES[index]);

                if (index == BANNER_ELLIPSES.length - 1) {
                    index = 0;
                } else {
                    index++;
                }
            }
        });
        ellipsesTimer.start();

    }

    /**
     * Trigger that in-progress task has been completed
     * @param success success status
     */
    public void triggerEnd(boolean success) {
        removeAll();

        JLabel endLabel = new JLabel();
        endLabel.setForeground(Color.WHITE);
        endLabel.setFont(endFont);

        if (success) {
            setBackground(Utilities.VALID_COLOR);
            endLabel.setText(successMsg);
        } else {
            endLabel.setText(failureMsg);
            setBackground(Utilities.INVALID_COLOR);
        }

        add(endLabel);

        repaint();
        revalidate();
    }

}
