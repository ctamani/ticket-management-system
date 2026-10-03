package javaapplication1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;


@SuppressWarnings("serial")
public class Tickets extends JFrame implements ActionListener {

    // class level member objects
    Dao dao = new Dao(); // for CRUD operations
    Boolean chkIfAdmin = null;
    String loggedInUser;

    // Main menu object items
    private JMenu mnuFile    = new JMenu("File");
    private JMenu mnuAdmin   = new JMenu("Admin");
    private JMenu mnuTickets = new JMenu("Tickets");
    private JScrollPane tableScrollPane;

    // Sub menu item objects for all Main menu item objects
    JMenuItem mnuItemExit;
    JMenuItem mnuItemUpdate;
    JMenuItem mnuItemDelete;
    JMenuItem mnuItemOpenTicket;
    JMenuItem mnuItemViewTicket;
    JMenuItem mnuItemViewById;
    JMenuItem mnuItemClose;

    // Status bar label
    private JLabel lblStatusBar;

    public Tickets(String username, Boolean isAdmin) {
        loggedInUser = username;
        chkIfAdmin   = isAdmin;
        createMenu();
        prepareGUI();
    }

    private void createMenu() {

        /* Initialize sub menu items **************************************/

        // initialize sub menu item for File main menu
        mnuItemExit = new JMenuItem("Exit");
        // add to File main menu item
        mnuFile.add(mnuItemExit);

        // initialize first sub menu items for Admin main menu
        mnuItemUpdate = new JMenuItem("Update Ticket");
        // add to Admin main menu item
        mnuAdmin.add(mnuItemUpdate);

        // initialize second sub menu items for Admin main menu
        mnuItemDelete = new JMenuItem("Delete Ticket");
        // add to Admin main menu item
        mnuAdmin.add(mnuItemDelete);

        // initialize first sub menu item for Tickets main menu
        mnuItemOpenTicket = new JMenuItem("Open Ticket");
        // add to Ticket Main menu item
        mnuTickets.add(mnuItemOpenTicket);

        // initialize second sub menu item for Tickets main menu
        mnuItemViewTicket = new JMenuItem("View Ticket");
        // add to Ticket Main menu item
        mnuTickets.add(mnuItemViewTicket);

        // initialize any more desired sub menu items below
        mnuItemViewById = new JMenuItem("View Ticket By ID");
        mnuTickets.add(mnuItemViewById);

        mnuItemClose = new JMenuItem("Close Ticket");
        mnuAdmin.add(mnuItemClose);

        /* Add action listeners for each desired menu item *************/
        mnuItemExit.addActionListener(this);
        mnuItemUpdate.addActionListener(this);
        mnuItemDelete.addActionListener(this);
        mnuItemOpenTicket.addActionListener(this);
        mnuItemViewTicket.addActionListener(this);
        mnuItemViewById.addActionListener(this);
        mnuItemClose.addActionListener(this);

        // Style the menus
        UIStyle.styleMenu(mnuAdmin);
        UIStyle.styleMenu(mnuTickets);
        UIStyle.styleMenuItem(mnuItemExit);
        UIStyle.styleMenuItem(mnuItemUpdate);
        UIStyle.styleMenuItem(mnuItemDelete);
        UIStyle.styleMenuItem(mnuItemOpenTicket);
        UIStyle.styleMenuItem(mnuItemViewTicket);
        UIStyle.styleMenuItem(mnuItemViewById);
        UIStyle.styleMenuItem(mnuItemClose);
    }

    private void prepareGUI() {

        //Menu bar
        JMenuBar bar = new JMenuBar();
        bar.setBackground(UIStyle.BG_TOP);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIStyle.CARD_BORDER));
        bar.add(mnuFile); // add main menu items in order, to JMenuBar
        if (chkIfAdmin) {
            bar.add(mnuAdmin);
        }
        bar.add(mnuTickets);

        // add menu bar components to frame
        setJMenuBar(bar);

        // Gradient background 
        JPanel bg = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, UIStyle.BG_TOP, 0, getHeight(), UIStyle.BG_BOT));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bg.setOpaque(false);

        // Welcome header
        JLabel lblWelcome = new JLabel(
            "  Welcome, " + loggedInUser + (chkIfAdmin ? "  [Administrator]" : ""),
            SwingConstants.LEFT);
        lblWelcome.setForeground(UIStyle.TEXT_PRI);
        lblWelcome.setOpaque(true);
        lblWelcome.setBackground(UIStyle.CARD_BG);
        lblWelcome.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIStyle.CARD_BORDER),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        lblWelcome.setPreferredSize(new Dimension(600, 42));
        bg.add(lblWelcome, BorderLayout.NORTH);

        // Status bar
        lblStatusBar = new JLabel("  Ready", SwingConstants.LEFT);
        lblStatusBar.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblStatusBar.setForeground(UIStyle.TEXT_SEC);
        lblStatusBar.setOpaque(true);
        lblStatusBar.setBackground(UIStyle.BG_TOP);
        lblStatusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UIStyle.CARD_BORDER),
            BorderFactory.createEmptyBorder(4, 12, 4, 12)));
        lblStatusBar.setPreferredSize(new Dimension(600, 26));
        bg.add(lblStatusBar, BorderLayout.SOUTH);

        setContentPane(bg);

        addWindowListener(new WindowAdapter() {
            // define a window close operation
            public void windowClosing(WindowEvent wE) {
                System.exit(0);
            }
        });
        // set frame options
        setSize(700, 460);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // implement actions for sub menu items
        if (e.getSource() == mnuItemExit) {
            System.exit(0);
        } else if (e.getSource() == mnuItemOpenTicket) {

            // get ticket information
            String ticketDesc = JOptionPane.showInputDialog(null, "Enter a ticket description");

            // insert ticket information to database
            int id = dao.insertRecords(loggedInUser, ticketDesc);

            // display results if successful or not to console / dialog box
            if (id != 0) {
                System.out.println("Ticket ID : " + id + " created successfully!!!");
                JOptionPane.showMessageDialog(null, "Ticket id: " + id + " created");
                setStatus("Ticket #" + id + " opened successfully.");
                refreshTicketTable();
            } else
                System.out.println("Ticket cannot be created!!!");
        }

        else if (e.getSource() == mnuItemViewTicket) {
            refreshTicketTable();
            setStatus("Viewing tickets for: " + (chkIfAdmin ? "all users (admin)" : loggedInUser));
        }
  
        else if (e.getSource() == mnuItemDelete) {
            String ticketIdStr = JOptionPane.showInputDialog(null, "Enter ticket id to delete");

            if (ticketIdStr != null && !ticketIdStr.trim().isEmpty()) {
                int ticketId = Integer.parseInt(ticketIdStr);

                int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to delete ticket number " + ticketId + "?",
                    "Delete Confirmation",
                    JOptionPane.YES_NO_OPTION
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    int status = dao.deleteRecords(ticketId);

                    if (status > 0) {
                        JOptionPane.showMessageDialog(null, "Ticket " + ticketId + " deleted successfully.");
                        setStatus("Ticket #" + ticketId + " deleted.");
                        refreshTicketTable();
                    } else {
                        JOptionPane.showMessageDialog(null, "Ticket " + ticketId + " was not found.");
                    }
                }
            }
        }
        else if (e.getSource() == mnuItemUpdate) {
            String ticketIdStr = JOptionPane.showInputDialog(null, "Enter ticket id to update");
            String newDesc     = JOptionPane.showInputDialog(null, "Enter new ticket description");

            if (ticketIdStr != null && newDesc != null &&
                !ticketIdStr.trim().isEmpty() && !newDesc.trim().isEmpty()) {

                int ticketId = Integer.parseInt(ticketIdStr);
                int status   = dao.updateRecords(ticketId, newDesc);

                if (status > 0) {
                    JOptionPane.showMessageDialog(null, "Ticket " + ticketId + " updated successfully.");
                    setStatus("Ticket #" + ticketId + " updated.");
                    refreshTicketTable();
                } else {
                    JOptionPane.showMessageDialog(null, "Ticket " + ticketId + " was not found.");
                }
            }
        }

        else if (e.getSource() == mnuItemViewById) {
            String ticketIdStr = JOptionPane.showInputDialog(null, "Enter ticket id to view");

            if (ticketIdStr != null && !ticketIdStr.trim().isEmpty()) {
                try {
                    int ticketId = Integer.parseInt(ticketIdStr);

                    JTable jt = new JTable(
                        TicketsJTable.buildTableModel(
                            dao.readRecordByTicketNum(ticketId, loggedInUser, chkIfAdmin)
                        )
                    );
                    jt.setFillsViewportHeight(true);
                    UIStyle.styleTable(jt);

                    if (tableScrollPane != null) {
                        remove(tableScrollPane);
                    }

                    tableScrollPane = new JScrollPane(jt);
                    tableScrollPane.setBounds(20, 50, 340, 250);
                    UIStyle.styleScrollPane(tableScrollPane);
                    add(tableScrollPane);

                    revalidate();
                    repaint();
                    setStatus("Showing ticket #" + ticketId);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        else if (e.getSource() == mnuItemClose) {
            String ticketIdStr = JOptionPane.showInputDialog(null, "Enter ticket id to close");

            if (ticketIdStr != null && !ticketIdStr.trim().isEmpty()) {
                int ticketId = Integer.parseInt(ticketIdStr);

                int status = dao.closeTicket(ticketId);

                if (status > 0) {
                    JOptionPane.showMessageDialog(null, "Ticket " + ticketId + " closed successfully.");
                    setStatus("Ticket #" + ticketId + " closed.");
                    refreshTicketTable();
                } else {
                    JOptionPane.showMessageDialog(null, "Ticket " + ticketId + " was not found.");
                }
            }
        }

    }

    private void refreshTicketTable() {
        // retrieve all tickets details for viewing in JTable
        try {
            // Use JTable built in functionality to build a table model and
            // display the table model off your result set!!!
            JTable jt = new JTable(TicketsJTable.buildTableModel(dao.readRecords(loggedInUser, chkIfAdmin)));
            jt.setFillsViewportHeight(true);
            UIStyle.styleTable(jt);

            if (tableScrollPane != null) {
                getContentPane().remove(tableScrollPane);
            }

            tableScrollPane = new JScrollPane(jt);
            UIStyle.styleScrollPane(tableScrollPane);

            getContentPane().add(tableScrollPane, BorderLayout.CENTER);
            revalidate();
            repaint();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void setStatus(String msg) {
        if (lblStatusBar != null) lblStatusBar.setText("  " + msg);
    }
}
