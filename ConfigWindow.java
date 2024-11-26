import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfigWindow {
    public JFrame frame = new JFrame("Initial configuration");
    public JTextField textField = new JTextField("Enter ghost number");
    public JButton closeButton = new JButton("Close");
    private ConfigCallback callback;

    public ConfigWindow(ConfigCallback callback) {
        this.callback = callback;
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        // Add the JTextField to the JFrame
        frame.add(textField);

        // Set the frame's layout
        frame.setLayout(new java.awt.FlowLayout());

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (callback != null) {
                    callback.onConfigComplete(textField.getText());
                }
                frame.dispose();
            }
        });

        frame.add(closeButton);

        frame.setVisible(true);
    }
}