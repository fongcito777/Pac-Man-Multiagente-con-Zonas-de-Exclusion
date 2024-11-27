import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfigWindow {
    public JFrame frame = new JFrame("Initial configuration");
    public JTextField textField = new JTextField("Enter ghost number");
    public JTextField textField2 = new JTextField("Enter fruit appear time");
    public JTextField textField3 = new JTextField("Enter fruit disappear time");
    public JTextField textField4 = new JTextField("Enter buffer zone size");
    public JTextField textField5 = new JTextField("Enter ghost limit on buffer");
    public JButton closeButton = new JButton("Close");
    private ConfigCallback callback;

    public ConfigWindow(ConfigCallback callback) {
        this.callback = callback;
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        // Add the JTextField to the JFrame
        frame.add(textField);
        frame.add(textField2);
        frame.add(textField3);
        frame.add(textField4);
        frame.add(textField5);

        // Set the frame's layout
        //frame.setLayout(new java.awt.FlowLayout());
        frame.setLayout(new GridLayout(6,1));

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (callback != null) {
                    callback.onConfigComplete(textField.getText(), textField2.getText(), textField3.getText(), textField4.getText(), textField5.getText());
                }
                frame.dispose();
            }
        });

        frame.add(closeButton);

        frame.setVisible(true);
    }
}