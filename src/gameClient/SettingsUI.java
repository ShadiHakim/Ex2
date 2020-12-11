package gameClient;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This is a ui class that extends from JFrame represents a settings frame in which the user
 * can enter his ID and scenario number.
 * @author shadihakim
 */
public class SettingsUI extends JFrame implements ActionListener{
    private ClickListener clickListener;

    // JTextField
    private JTextField tID;
    private JTextField tScenario;

    private JLabel errorLabel;

    /**
     * A simple constructor that builds the frame
     */
    public SettingsUI(){
        super("Ex2 - Settings");
        init();
    }

    /**
     * This function initialize all the buttons and fields on the frame
     */
    private void init() {
        // Panel to define the layout. We are using GridBagLayout
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel headingPanel = new JPanel();
        JLabel headingLabel = new JLabel("Please fill your ID and scenario number");
        headingPanel.add(headingLabel);

        // Panel to define the layout. We are using GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        // Constraints for the layout
        GridBagConstraints constr = new GridBagConstraints();
        constr.insets = new Insets(5, 5, 5, 5);
        constr.anchor = GridBagConstraints.WEST;

        // Set the initial grid values to 0,0
        constr.gridx=0;
        constr.gridy=0;

        // labels to display text
        JLabel lID = new JLabel("ID:");
        JLabel lScenario = new JLabel("Scenario:");

        // Declare Text fields
        tID = new JTextField(20);
        tScenario = new JTextField(20);

        panel.add(lID, constr);
        constr.gridx=1;
        panel.add(tID, constr);
        constr.gridx=0; constr.gridy=1;

        panel.add(lScenario, constr);
        constr.gridx=1;
        panel.add(tScenario, constr);
        constr.gridx=0; constr.gridy=2;

        constr.gridwidth = 2;
        constr.anchor = GridBagConstraints.CENTER;

        // Button with text "Start"
        JButton button = new JButton("Start");
        // add a listener to button
        button.addActionListener(this);

        // Add label and button to panel
        panel.add(button, constr);

        constr.gridy=3;

        errorLabel = new JLabel(" ");
        panel.add(errorLabel,constr);

        mainPanel.add(headingPanel);
        mainPanel.add(panel);

        // Add panel to frame
        this.add(mainPanel);
        this.pack();
        this.setSize(350, 200);
        this.setMinimumSize(new Dimension(350, 200));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getRootPane().setDefaultButton(button);
        this.setVisible(true);
    }

    /**
     * Calling the click function
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (clickListener != null) clickListener.onClick(e);
    }

    /**
     * This function simulate the args which contains the ID and the scenario number
     * @return
     */
    public String[] getData(){
        String[] args = new String[2];
        args [0] = tID.getText();
        args [1] = tScenario.getText();
        return args;
    }

    /**
     * This function make an error message appear
     * @param e
     */
    public void error(String e){
        errorLabel.setForeground(Color.red);
        errorLabel.setText(e);
    }

    /**
     * allows clicks events to be caught
     * @param clickListener
     */
    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    /**
     * This inner interface helps get the click function out
     */
    public interface ClickListener {
        void onClick(ActionEvent e);
    }
}
