/**
 * 
 */
package edu.wofford.wocoin.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import edu.wofford.wocoin.GUIController;
import edu.wofford.wocoin.SQLController;

/**
 * @author wickerma
 *
 */
public class RootFrame implements ItemListener {
	private JPanel cards;
    private UserUI userPanel;
    private AdminUI adminPanel;
    
    private void addComponentToPane(Container pane) {
        JPanel comboBoxPane = new JPanel(); //use FlowLayout
        String[] comboBoxItems = {"User", "Administrator"};
        
        JComboBox<String> cb = new JComboBox<>(comboBoxItems);
        cb.setEditable(false);
        cb.addItemListener(this);

        userPanel = new UserUI(new GUIController(new SQLController()));
        adminPanel = new AdminUI(new GUIController(new SQLController()));

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(userPanel);
        btnLogout.addActionListener(adminPanel);
        comboBoxPane.add(btnLogout);
        comboBoxPane.add(cb);

        cards = new JPanel(new CardLayout());
        cards.add(userPanel, "User");
        cards.add(adminPanel, "Administrator");
        
        pane.add(comboBoxPane, BorderLayout.PAGE_START);
        pane.add(cards, BorderLayout.CENTER);
    }
    
    @Override
    public void itemStateChanged(ItemEvent evt) {
        CardLayout cl = (CardLayout)(cards.getLayout());
        adminPanel.logout();
        userPanel.logout();
        cl.show(cards, (String)evt.getItem());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("WoCoin");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        RootFrame rootFrame = new RootFrame();
        rootFrame.addComponentToPane(frame.getContentPane());
        
        //Display the window.
        frame.pack();
        frame.setSize(new Dimension(700,600));
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
