package ca.cmpt213.a4.client.view;

import ca.cmpt213.a4.client.control.ConsumableManager;
import ca.cmpt213.a4.client.model.Consumable;
import com.google.gson.JsonArray;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.List;

/**
 * Consumable information panel containing Consumable.toString and a delete button
 * @author Steven Quinn (301462499) – CMPT 213 D100 – Fall 2021
 */
public class ConsumableInfoPanel extends JPanel {

    private final String DELETE_ICON_LOC = "resources/trash.png";
    private final String CONFIRM_WINDOW_ICON_LOC = "resources/clippy.png";

    private JButton removeButton;
    private Consumable panelConsumable;
    private int listIndex;
    private String borderText;
    private Border emptyBorder;
    private Border mainBorder;

    /**
     * Constructor
     * @param parentViewer The ConsumableViewer generating this panel
     * @param index Starter list index #
     * @param item Consumable object tied to panel information
     * @param parent JPanel containing this panel
     * @param grandparent JScrollPane containing the parent panel
     */
    public ConsumableInfoPanel(ConsumableViewer parentViewer, int index, Consumable item, JPanel parent, JScrollPane grandparent) {
        super();
        panelConsumable = item;
        borderText = "Item #" + index + " (" + Utilities.capitalizeFirstLetter(panelConsumable.getCategory()) + ")";
        setMaximumSize(new Dimension((int) getMaximumSize().getWidth(), 190));

        // Panel preferences
        emptyBorder = BorderFactory.createEmptyBorder(12, 15, 10, 15);
        mainBorder = BorderFactory.createTitledBorder(borderText);
        setBorder(new CompoundBorder(emptyBorder, mainBorder));

        // Layout
        FlowLayout flow = new FlowLayout();
        flow.setHgap(10);
        setLayout(flow);

        // Consumable information
        JTextArea newTextArea = new JTextArea();
        newTextArea.setEditable(false);
        newTextArea.setHighlighter(null);
        newTextArea.setBackground(getBackground());
        newTextArea.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        newTextArea.setText(item.toString());

        // Add scroll ability in case text is excessively long
        JScrollPane newTextAreaScroll = new JScrollPane(newTextArea);
        newTextAreaScroll.setBorder(null);
        newTextAreaScroll.setPreferredSize(new Dimension((int) ((int) grandparent.getPreferredSize().getWidth() * 0.7), 120));

        // Trash button
        ImageIcon trashIcon = new ImageIcon(DELETE_ICON_LOC);
        removeButton = new JButton(trashIcon);
        removeButton.setBackground(Color.white);
        removeButton.setOpaque(false);
        removeButton.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        removeButton.setPreferredSize(new Dimension(50, 50));

        // If parent is disabled, disable button
        // Used for ConsumableViewer.setGUIEnabled()
        if (!parent.isEnabled())
            setEnabled(false);

        // Trash button listener
        removeButton.addActionListener(e -> {

            // Show message dialog to delete item
            String deleteMessage = "Are you sure you want to delete " + getPanelConsumable().getName() + "?";
            int response = JOptionPane.showConfirmDialog(grandparent, deleteMessage, "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, new ImageIcon(CONFIRM_WINDOW_ICON_LOC));

            // "yes" to dialog, remove item
            if (response == 0) {

                // New banner for item removal
                ServerContactBanner scb = new ServerContactBanner("Contacting server",
                        "Removed " + getPanelConsumable().getName() + "!",
                        "Unable to remove " + getPanelConsumable().getName() + "!");
                parentViewer.employServerContactBanner(scb, parentViewer.getNorthPanel());

                // Attempt to remove item from server
                JsonArray responseArr = parentViewer.getCommunicator().removeItem(getPanelConsumable());
                if (responseArr != null) {

                    // Attempt to replace conman list
                    List<Consumable> updatedList = ConsumableManager.deserializeConsumableList(responseArr.toString());
                    parentViewer.getConsumableManager().setConsumableList(updatedList);
                    parentViewer.retireServerContactBanner(600, scb,
                            true, parentViewer.getNorthPanel());

                } else {
                    parentViewer.retireServerContactBanner(600, scb,
                            false, parentViewer.getNorthPanel());
                }

                // Repopulate panels
                parentViewer.populateScrollListWithFilteredItems(false);
            }
        });

        // Add components and return panel
        add(newTextAreaScroll);
        add(removeButton);
    }

    /**
     * Get the list index given to the object
     * @return index
     */
    public int getIndex() {
        return listIndex;
    }

    /**
     * Set the list index and update the border
     * @param index List index
     */
    public void setIndex(int index) {
        this.listIndex = index;
        updateBorder();
    }

    /**
     * Overriding setEnabled
     * @param b
     */
    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        removeButton.setEnabled(b);
    }

    /**
     * Get Consumable object displayed by this panel
     * @return Consumable
     */
    public Consumable getPanelConsumable() {
        return panelConsumable;
    }

    /**
     * Set the consumable item this panel is about and update the border
     * @param item Consumable item
     */
    public void setPanelConsumable(Consumable item) {
        panelConsumable = item;
        updateBorder();
    }

    /**
     * Update the border of the panel to reflect provided list index value
     */
    private void updateBorder() {
        borderText = "Item #" + listIndex + " (" + Utilities.capitalizeFirstLetter(panelConsumable.getCategory()) + ")";
        emptyBorder = BorderFactory.createEmptyBorder(15, 15, 20, 15);
        mainBorder = BorderFactory.createTitledBorder(borderText);
        setBorder(new CompoundBorder(emptyBorder, mainBorder));
    }

    /**
     * Return a blank panel that notifies user of a lack of item
     * @param message Message to print in JLabel
     * @return JPanel
     */
    public static JPanel getBlankMessagePanel(String message) {

        // Consumable panel
        JPanel blankPanel = new JPanel();;
        Border emptyBorder = BorderFactory.createEmptyBorder(30, 15, 15, 15);
        blankPanel.setBorder(emptyBorder);
        blankPanel.setLayout(new FlowLayout());

        // Empty list message
        JLabel lbl = new JLabel(message);
        lbl.setHorizontalAlignment(JLabel.CENTER);
        blankPanel.add(lbl);

        // Return panel
        return blankPanel;
    }
}
