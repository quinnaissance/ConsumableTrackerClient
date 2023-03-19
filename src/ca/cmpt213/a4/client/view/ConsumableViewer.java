package ca.cmpt213.a4.client.view;

import ca.cmpt213.a4.client.control.ConsumableManager;
import ca.cmpt213.a4.client.control.ConsumableServerCommunicator;
import ca.cmpt213.a4.client.model.Consumable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * GUI display of Consumable list including filters and ability to add new items
 * @author Steven Quinn (301462499) – CMPT 213 D100 – Fall 2021
 */
public class ConsumableViewer {

    private String mainWindowTitle;
    private JFrame mainFrame;
    private JScrollPane consumablePane;
    private JPanel panelForConsumablePane;
    private JPanel northPanel;

    private ButtonGroup expiryBg;
    private JButton addButton;
    private ConsumableManager conman;
    private ConsumableManager.ExpiryPeriod criteria;
    private ConsumableServerCommunicator csc;

    // enum for consumable type
    public enum consumableType {
        FOOD("Food"),
        DRINK("Drink");

        private final String type;

        consumableType(String t) {
            this.type = t;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    /**
     * Constructor
     */
    public ConsumableViewer(String windowTitle) {
        this.mainWindowTitle = windowTitle;

        // Initiate manager
        conman = new ConsumableManager();
        csc = new ConsumableServerCommunicator("http://localhost:8080");

        // Main JFrame
        initializeMainFrame();

        // Filter buttons panel
        JPanel northButtonPanel = new JPanel();
        northButtonPanel.setLayout(new FlowLayout());
        int borderPadding = 15;
        northButtonPanel.setBorder(BorderFactory.createEmptyBorder(borderPadding, borderPadding, borderPadding, borderPadding));

        // Initialize filter buttons
        initializeFilterButtons(northButtonPanel);

        // Panel for loading banner
        northPanel = new JPanel(new BorderLayout());
        northPanel.add(northButtonPanel, BorderLayout.CENTER);
        mainFrame.add(northPanel, BorderLayout.NORTH);

        // Consumable info scroller
        JPanel borderMiddlePanel = new JPanel();
        borderMiddlePanel.setLayout(new BorderLayout());

        // Scroll pane
        consumablePane = new JScrollPane();
        consumablePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        consumablePane.setPreferredSize(new Dimension(400, 280));
        panelForConsumablePane = new JPanel();
        panelForConsumablePane.setLayout(new BoxLayout(panelForConsumablePane, BoxLayout.Y_AXIS));
        consumablePane.getViewport().add(panelForConsumablePane);

        // 'Add item' button
        JPanel addPanel = new JPanel();
        addPanel.setBorder(BorderFactory.createEmptyBorder(borderPadding, borderPadding, borderPadding, borderPadding));
        addPanel.setLayout(new FlowLayout());
        addButton = new JButton("Add item");
        addPanel.add(addButton);

        // Fetch server list and populate panels
        populateScrollListWithFilteredItems(true);
        ServerContactBanner loadBanner = new ServerContactBanner("Contacting server",
                "List received!", "Unable to get list from server!");
        employServerContactBanner(loadBanner, northPanel);

        List<Consumable> fetchedList = csc.listAll();
        if (fetchedList != null) {
            // Successful fetch
            conman.setConsumableList(fetchedList);
            retireServerContactBanner(600, loadBanner, true, northPanel);
        } else {
            // Failed to fetch
            retireServerContactBanner(600, loadBanner, false, northPanel);
        }
        populateScrollListWithFilteredItems(true);

        // Add scroll panel to mainFrame
        borderMiddlePanel.add(consumablePane);
        mainFrame.add(borderMiddlePanel, BorderLayout.CENTER);

        // Listeners
        addButton.addActionListener(getAddItemListener());
        mainFrame.addWindowListener(getMainFrameExitListener());

        // Enable
        mainFrame.add(addPanel, BorderLayout.SOUTH);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    /**
     * Helper method for mainFrame
     */
    private void initializeMainFrame() {
        mainFrame = new JFrame(mainWindowTitle);
        mainFrame.setSize(510, 450);
        mainFrame.setMinimumSize(new Dimension(430, 370));
        mainFrame.setMaximumSize(new Dimension(800, 600));
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    /**
     * Helper method  to initialize all the expiry period filter buttons
     * @param buttonPanel Panel containing the buttons
     */
    private void initializeFilterButtons(JPanel buttonPanel) {
        JToggleButton allButton = new JToggleButton("All");
        JToggleButton expiredButton = new JToggleButton("Expired");
        JToggleButton unexpiredButton = new JToggleButton("Not Expired");
        JToggleButton weekExpireButton = new JToggleButton("Expiring in 7 Days");

        // Add filter buttons to button group
        expiryBg = new ButtonGroup();
        expiryBg.add(allButton);
        expiryBg.add(expiredButton);
        expiryBg.add(unexpiredButton);
        expiryBg.add(weekExpireButton);

        // Default filter setting "All"
        expiryBg.setSelected(allButton.getModel(), true);
        criteria = ConsumableManager.ExpiryPeriod.ALL;

        // Add filter button panel to main
        buttonPanel.add(allButton);
        buttonPanel.add(expiredButton);
        buttonPanel.add(unexpiredButton);
        buttonPanel.add(weekExpireButton);

        // Listeners
        allButton.addActionListener(getExpiryButtonListener());
        expiredButton.addActionListener(getExpiryButtonListener());
        unexpiredButton.addActionListener(getExpiryButtonListener());
        weekExpireButton.addActionListener(getExpiryButtonListener());
    }

    /**
     * Helper method to get the Add Item button listener
     * @return ActionListener
     */
    private ActionListener getAddItemListener() {
        ActionListener addItemListener = e -> {
            ConsumableViewerAddPane pane = new ConsumableViewerAddPane();

            // Get Consumable with desired info
            Consumable addPaneConsumable = pane.getConsumable();
            boolean serverUp = csc.checkStatus();
            if (addPaneConsumable != null) {
                // Set up server banner
                ServerContactBanner scb = new ServerContactBanner("Contacting server",
                        "Added item!", "Unable to add item!");
                employServerContactBanner(scb, northPanel);

                // Attempt to add item to server
                if (serverUp) {
                    boolean add = csc.addItem(addPaneConsumable);
                    if (add) {
                        // Attempt to replace conman list
                        List<Consumable> updatedList = csc.listAll();
                        if (updatedList != null) {
                            conman.setConsumableList(updatedList);
                            retireServerContactBanner(1200, scb, true, northPanel);
                        } else {
                            retireServerContactBanner(1200, scb, false, northPanel);
                        }
                    } else {
                        retireServerContactBanner(1200, scb, false, northPanel);
                    }
                } else { // Server down
                    retireServerContactBanner(1200, scb, false, northPanel);
                }

                populateScrollListWithFilteredItems(true);
            }
        };
        return addItemListener;
    }

    /**
     * Helper method to get the event listener for expiry filters
     * @return ActionListener
     */
    private ActionListener getExpiryButtonListener() {
        ActionListener expiryListener = e -> {
            updateFilterStatus(e);

            // Fetch updated list
            ServerContactBanner loadBanner = new ServerContactBanner("Contacting server",
                    "List updated from server!", "Unable to get list from server!");
            employServerContactBanner(loadBanner, northPanel);

            List<Consumable> fetchedList = csc.listAll();
            if (fetchedList != null) {
                // Successful fetch
                conman.setConsumableList(fetchedList);
                retireServerContactBanner(400, loadBanner, true, northPanel);
            } else {
                // Failed to fetch
                retireServerContactBanner(400, loadBanner, false, northPanel);
            }


            populateScrollListWithFilteredItems(true);
        };
        return expiryListener;
    }

    /**
     * Helper method containing mainFrame exit listener
     * @return WindowListener
     */
    private WindowListener getMainFrameExitListener() {
        return new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}

            @Override
            public void windowClosing(WindowEvent e) {
                windowClosed(e);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                // Banner for save
                ServerContactBanner loadBanner = new ServerContactBanner("Contacting server",
                        "Server list saved!", "Unable to save server list!");
                employServerContactBanner(loadBanner, northPanel);

                // Save server list
                boolean saveSuccess = csc.exit();
                if (saveSuccess) {
                    // Successful save
                    retireServerContactBanner(500, loadBanner, true, northPanel);
                } else {
                    // Unsuccessful save
                    retireServerContactBanner(500, loadBanner, false, northPanel);
                }

                // Close once banner is gone
                Timer t = new Timer(1200, e1 -> {
                    mainFrame.setVisible(false);
                    mainFrame.dispose();
                    System.exit(0);
                    ((Timer) e.getSource()).stop();
                });
                t.start();

            }

            @Override
            public void windowIconified(WindowEvent e) {}

            @Override
            public void windowDeiconified(WindowEvent e) {}

            @Override
            public void windowActivated(WindowEvent e) {}

            @Override
            public void windowDeactivated(WindowEvent e) {}
        };
    }

    /**
     * Get the ConsumableManager tied to this object
     * @return ConsumableManager
     */
    public ConsumableManager getConsumableManager() {
        return conman;
    }

    /**
     * Get the panel used for the server banner
     * @return JPanel
     */
    public JPanel getNorthPanel() {
        return northPanel;
    }

    /**
     * Get the communicator being used by the program
     * @return ConsumableServerCommunicator
     */
    public ConsumableServerCommunicator getCommunicator() {
        return csc;
    }

    /**
     * Enables/disables the GUI so user interaction is not possible
     * Used to intuit that the program is busy doing something
     * @param status
     */
    public void setGUIEnabled(boolean status) {

        // Add button
        addButton.setEnabled(status);

        // Item panels
        panelForConsumablePane.setEnabled(status);
        for (Component c : panelForConsumablePane.getComponents())
            c.setEnabled(status);

        // Filter buttons
        Enumeration<AbstractButton> buttonGroupEnum = expiryBg.getElements();
        while (buttonGroupEnum.hasMoreElements()) {
            buttonGroupEnum.nextElement().setEnabled(status);
        }

        // Scroll pane
        consumablePane.setEnabled(status);
    }

    /**
     * Start a ServerContactBanner in a particular JPanel
     *
     * @param scb    ServerContactBanner to employ
     * @param parent JPanel to add banner to
     */
    public void employServerContactBanner(ServerContactBanner scb, JPanel parent) {
        setGUIEnabled(false);

        if (parent.getComponentCount() >= 1 && parent.getComponent(0) instanceof ServerContactBanner)
            parent.remove(0);

        parent.add(scb, BorderLayout.NORTH);
        parent.repaint();
        parent.revalidate();
    }

    /**
     * Get rid of ServerContactBanner
     *
     * @param banner
     * @param parentPanel
     */
    public void retireServerContactBanner(int initialDelay, ServerContactBanner banner, boolean successful, JPanel parentPanel) {

        // Outside timer is for initial delay
        // (to show user that server is being contacted)
        Timer t = new Timer(initialDelay, e -> {

            // Trigger end screen
            banner.triggerEnd(successful);

            // Inside timer is for delay between success/failure display
            // and removal of banner from the panel that contains it
            Timer t1 = new Timer(1400, e1 -> {
                parentPanel.remove(banner);
                parentPanel.repaint();
                parentPanel.revalidate();

                // Stop this timer
                Timer timer = (Timer) e1.getSource();
                timer.stop();

            });
            t1.start();
            ((Timer) e.getSource()).stop();
            setGUIEnabled(true);
        });
        t.start();
    }

    /**
     * Generates a List of ConsumableInfoPanels with given Consumable information
     *
     * @param conList
     * @param parent
     * @param grandparent
     * @return List of ConsumableInfoPanels with correct indices
     * @precondition JPanel parent must be JScrollPane containing objects
     */
    private List<ConsumableInfoPanel> generateConsumablePanels(List<Consumable> conList, JPanel parent, JScrollPane grandparent) {
        List<ConsumableInfoPanel> newList = new ArrayList<>();

        int index = 1;
        // Traverse given list and generate panels
        for (Consumable c : conList) {
            newList.add(new ConsumableInfoPanel(ConsumableViewer.this, index, c, parent, grandparent));
            index++;
        }

        return newList;
    }

    /**
     * Update this object's ExpiryPeriod based on button selection
     *
     * @param e Action with button click text
     */
    private void updateFilterStatus(ActionEvent e) {
        String ac = e.getActionCommand();
        switch (ac) {
            case "All" -> criteria = ConsumableManager.ExpiryPeriod.ALL;
            case "Expired" -> criteria = ConsumableManager.ExpiryPeriod.EXPIRED;
            case "Not Expired" -> criteria = ConsumableManager.ExpiryPeriod.UNEXPIRED;
            case "Expiring in 7 Days" -> criteria = ConsumableManager.ExpiryPeriod.EXPIRING_WITHIN_WEEK;
        }
    }

    /**
     * Refresh JScrollPane GUI
     * (must do when updating list)
     */
    public void revalidateConsumableScrollList() {
        panelForConsumablePane.repaint();
        panelForConsumablePane.revalidate();
    }

    /**
     * Populate the JScrollPane with Consumable panels depending on selected filter buttons
     */
    public void populateScrollListWithFilteredItems(boolean resetScrollPane) {

        // Remove all current panels
        if (panelForConsumablePane.getComponentCount() > 0)
            panelForConsumablePane.removeAll();

        // Generate new list
        List<ConsumableInfoPanel> newList = generateConsumablePanels(ConsumableManager.getSublistWithExpiryCriteria(conman.getConsumableList(),
                criteria), panelForConsumablePane, consumablePane);

        // Add all panels from list
        for (ConsumableInfoPanel c : newList) {
            panelForConsumablePane.add(c);
        }

        // If there are no panels, add a blank message panel
        if (panelForConsumablePane.getComponentCount() == 0)
            panelForConsumablePane.add(ConsumableInfoPanel.getBlankMessagePanel(ConsumableManager.getEmptyListWarning(criteria)));

        revalidateConsumableScrollList();

        // Reset scroll position
        if (resetScrollPane)
            consumablePane.getVerticalScrollBar().setValue(0);
    }

}